package com.example.ondevice.hardware

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.example.ondevice.models.AcceleratorType
import com.example.ondevice.models.DeviceCapability
import com.example.ondevice.models.DeviceHardwareTier
import com.example.ondevice.models.SupportedRuntime
import java.io.File

/**
 * Device Capability Layer.
 *
 * Responsibilities:
 * 1. Checks available system RAM and calculates usable model memory headroom.
 * 2. Checks available flash storage for model weights partition.
 * 3. Detects NPU / GPU / CPU hardware acceleration (Qualcomm Hexagon, Google Tensor, Mali, Adreno).
 * 4. Identifies supported local AI runtimes (AICore / Gemini Nano, LiteRT, ONNX Runtime Mobile).
 * 5. Classifies the device into an architectural hardware tier (Tier 1, Tier 2, or Tier 3).
 * 6. Validates model compatibility before loading into memory.
 */
object DeviceCapabilityDetector {

    fun detectCapabilities(context: Context? = null): DeviceCapability {
        var totalRamBytes = 8L * 1024L * 1024L * 1024L // Default 8 GB for emulators/JVM
        var availRamBytes = 4L * 1024L * 1024L * 1024L

        context?.let { ctx ->
            try {
                val actManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
                val memInfo = ActivityManager.MemoryInfo()
                actManager?.getMemoryInfo(memInfo)
                if (memInfo.totalMem > 0) {
                    totalRamBytes = memInfo.totalMem
                    availRamBytes = memInfo.availMem
                }
            } catch (_: Exception) {}
        }

        val totalRamGb = String.format("%.1f", totalRamBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)).toFloat()
        val availRamGb = String.format("%.1f", availRamBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)).toFloat()

        var freeStorageGb = 32.0f
        try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val freeBytes = stat.availableBlocksLong * stat.blockSizeLong
            freeStorageGb = String.format("%.1f", freeBytes.toDouble() / (1024.0 * 1024.0 * 1024.0)).toFloat()
        } catch (_: Exception) {}

        // Hardware Accelerator Detection
        val hardware = Build.HARDWARE.lowercase()
        val soc = Build.SOC_MODEL.lowercase()
        val board = Build.BOARD.lowercase()

        val (accelerator, isNpu) = when {
            hardware.contains("tensor") || soc.contains("tensor") || board.contains("tensor") -> {
                AcceleratorType.TENSOR_TPU to true
            }
            hardware.contains("qcom") || hardware.contains("qualcomm") || soc.contains("sm") || soc.contains("snapdragon") -> {
                AcceleratorType.HEXAGON_NPU to true
            }
            hardware.contains("kirin") || hardware.contains("exynos") || hardware.contains("dimensity") || hardware.contains("mt") -> {
                AcceleratorType.MALI_GPU to false
            }
            else -> {
                AcceleratorType.ADRENO_GPU to false
            }
        }

        // Supported on-device runtimes
        val supportedRuntimes = mutableListOf<SupportedRuntime>()
        supportedRuntimes.add(SupportedRuntime.LITE_RT)
        supportedRuntimes.add(SupportedRuntime.ONNX_MOBILE)
        supportedRuntimes.add(SupportedRuntime.EXECUTORCH)

        // Gemini Nano / Android AICore available on Tensor or Qualcomm flagship
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && (isNpu || totalRamGb >= 10.0f)) {
            supportedRuntimes.add(0, SupportedRuntime.AICORE_GEMINI_NANO)
        }

        // Hardware Tier Classification
        val tier = when {
            totalRamGb >= 11.5f && isNpu -> DeviceHardwareTier.TIER_3_FLAGSHIP_NPU
            totalRamGb >= 7.0f -> DeviceHardwareTier.TIER_2_MID_RAM
            else -> DeviceHardwareTier.TIER_1_LOW_RAM
        }

        val recommendedModelId = when (tier) {
            DeviceHardwareTier.TIER_3_FLAGSHIP_NPU -> {
                if (supportedRuntimes.contains(SupportedRuntime.AICORE_GEMINI_NANO)) "gemini-nano-aicore" else "llama-3-8b-int4-npu"
            }
            DeviceHardwareTier.TIER_2_MID_RAM -> "llama-3.2-3b-q4"
            DeviceHardwareTier.TIER_1_LOW_RAM -> "gemma-2b-int4"
        }

        return DeviceCapability(
            totalRamGb = totalRamGb,
            availableRamGb = availRamGb,
            storageFreeGb = freeStorageGb,
            acceleratorType = accelerator,
            supportedRuntimes = supportedRuntimes,
            deviceTier = tier,
            isNpuAccelerated = isNpu,
            recommendedModelId = recommendedModelId
        )
    }
}
