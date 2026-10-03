#!/usr/bin/env python3
"""Smoke-tests Medica's downloadable local models (Lite / Core / Max).

Boots llama-server on a free port for each GGUF, sends a real medical
prompt, and validates that a structured JSON result comes back.
"""
import json
import subprocess
import sys
import time
import urllib.request
import urllib.error
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
BIN = Path("/tmp/llama.cpp/build/bin/llama-server")

MODELS = [
    ("Medica Lite", ROOT / "models/qwen2.5-0.5b-instruct-q4_k_m.gguf", 18433),
    ("Medica Core", ROOT / "models/qwen2.5-1.5b-instruct-q4_k_m.gguf", 18434),
    ("Medica Max", ROOT / "models/qwen2.5-3b-instruct-q4_k_m.gguf", 18435),
]

PROMPT = (
    "You are Medica, an offline field-responder assistant. "
    "Casualty: sudden shortness of breath, absent breath sounds on the left "
    "after blunt chest trauma. Respond with ONLY valid JSON keys: "
    "assessment (string), priority (int 1-4), actions (array of 2-3 strings), "
    "confidence (float 0-1)."
)


def wait_healthy(port, proc, timeout=180):
    deadline = time.time() + timeout
    while time.time() < deadline:
        if proc.poll() is not None:
            raise RuntimeError(f"server exited early with code {proc.returncode}")
        try:
            with urllib.request.urlopen(f"http://127.0.0.1:{port}/health", timeout=3) as r:
                if r.status == 200:
                    return
        except Exception:
            time.sleep(1)
    raise RuntimeError("server never became healthy")


def chat(port, prompt):
    body = json.dumps({
        "model": "medica",
        "messages": [
            {"role": "system", "content": "You output valid JSON only."},
            {"role": "user", "content": prompt},
        ],
        "temperature": 0.2,
        "max_tokens": 220,
    }).encode()
    req = urllib.request.Request(
        f"http://127.0.0.1:{port}/v1/chat/completions",
        data=body, headers={"Content-Type": "application/json"})
    with urllib.request.urlopen(req, timeout=300) as r:
        return json.load(r)


def parse_json(text):
    t = text.strip()
    if t.startswith("```"):
        t = t.strip("`")
        t = t[t.find("{"):]
    start, end = t.find("{"), t.rfind("}")
    if start == -1 or end == -1:
        raise ValueError("no JSON object in output")
    return json.loads(t[start:end + 1])


def main():
    results = []
    for name, path, port in MODELS:
        print(f"\n=== {name} :: {path.name} ({path.stat().st_size/1e9:.2f} GB) ===")
        row = {"model": name, "file": path.name, "ok": False}
        err = open(f"/tmp/medica_{port}.log", "wb")
        proc = subprocess.Popen(
            [str(BIN), "-m", str(path), "--port", str(port), "--host", "127.0.0.1",
             "-c", "4096", "--jinja", "--no-warmup", "-np", "1"],
            stdout=subprocess.DEVNULL, stderr=err)
        try:
            t0 = time.time()
            wait_healthy(port, proc)
            row["load_s"] = round(time.time() - t0, 1)
            resp = chat(port, PROMPT)
            content = resp["choices"][0]["message"]["content"]
            usage = resp.get("usage", {})
            parsed = parse_json(content)
            assert isinstance(parsed.get("assessment"), str) and parsed["assessment"]
            assert isinstance(parsed.get("priority"), int) and 1 <= parsed["priority"] <= 4
            assert isinstance(parsed.get("actions"), list) and len(parsed["actions"]) >= 2
            assert isinstance(parsed.get("confidence"), (int, float))
            row.update(ok=True, assessment=parsed["assessment"],
                       priority=parsed["priority"], confidence=parsed["confidence"],
                       tokens=usage.get("completion_tokens"),
                       content=content[:400])
            print(f"  OK  load={row['load_s']}s  tokens={row.get('tokens')}")
            print(f"  assessment: {parsed['assessment']}")
        except Exception as e:
            row["error"] = f"{type(e).__name__}: {e}"
            try:
                tail = Path(f"/tmp/medica_{port}.log").read_text()[-500:]
                row["log_tail"] = tail
                print(f"  FAIL {row['error']}\n  log: {tail}")
            except Exception:
                print(f"  FAIL {row['error']}")
        finally:
            proc.terminate()
            try:
                proc.wait(timeout=15)
            except subprocess.TimeoutExpired:
                proc.kill()
        results.append(row)

    out = ROOT / "models" / "test-results.json"
    out.write_text(json.dumps(results, indent=2))
    print(f"\nwrote {out}")
    failed = [r for r in results if not r["ok"]]
    print(f"PASS {len(results)-len(failed)}/{len(results)}")
    sys.exit(1 if failed else 0)


if __name__ == "__main__":
    main()
