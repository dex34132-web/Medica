package com.example.data.room

import com.example.model.ActionItem
import com.example.model.Case
import com.example.model.CaseMedia
import com.example.model.CaseStatus
import com.example.model.EscalationStatus
import com.example.model.MediaType
import com.example.model.MedicaResult
import com.example.model.TimelineEvent
import com.example.model.UrgencyLevel
import org.json.JSONArray
import org.json.JSONObject

object CaseJsonHelper {

    fun caseToEntity(case: Case): CaseEntity {
        return CaseEntity(
            id = case.id,
            title = case.title,
            createdAtFormatted = case.createdAtFormatted,
            timestampMs = case.timestampMs,
            mediaJson = mediaListToJson(case.media),
            context = case.context,
            status = case.status.name,
            resultJson = case.result?.let { resultToJson(it) },
            escalationStatus = case.escalationStatus.name,
            escalationContact = case.escalationContact,
            escalationNotes = case.escalationNotes,
            timelineJson = timelineListToJson(case.timeline)
        )
    }

    fun entityToCase(entity: CaseEntity): Case {
        return Case(
            id = entity.id,
            title = entity.title,
            createdAtFormatted = entity.createdAtFormatted,
            timestampMs = entity.timestampMs,
            media = jsonToMediaList(entity.mediaJson),
            context = entity.context,
            status = runCatching { CaseStatus.valueOf(entity.status) }.getOrDefault(CaseStatus.DRAFT),
            result = entity.resultJson?.let { jsonToResult(it) },
            escalationStatus = runCatching { EscalationStatus.valueOf(entity.escalationStatus) }.getOrDefault(EscalationStatus.NOT_REQUIRED),
            escalationContact = entity.escalationContact,
            escalationNotes = entity.escalationNotes,
            timeline = jsonToTimelineList(entity.timelineJson)
        )
    }

    private fun mediaListToJson(mediaList: List<CaseMedia>): String {
        val array = JSONArray()
        mediaList.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("type", m.type.name)
                put("filename", m.filename)
                put("description", m.description)
                put("duration", m.duration ?: "")
                put("size", m.size)
                put("local", m.local)
                put("status", m.status)
                put("uriString", m.uriString ?: "")
                put("mockPreviewType", m.mockPreviewType)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun jsonToMediaList(json: String): List<CaseMedia> {
        val list = mutableListOf<CaseMedia>()
        runCatching {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CaseMedia(
                        id = obj.optString("id", "media_$i"),
                        type = runCatching { MediaType.valueOf(obj.optString("type")) }.getOrDefault(MediaType.IMAGE),
                        filename = obj.optString("filename", "file"),
                        description = obj.optString("description", ""),
                        duration = obj.optString("duration").takeIf { it.isNotBlank() },
                        size = obj.optString("size", "1.0 MB"),
                        local = obj.optBoolean("local", true),
                        status = obj.optString("status", "READY"),
                        uriString = obj.optString("uriString").takeIf { it.isNotBlank() },
                        mockPreviewType = obj.optString("mockPreviewType", "STANDARD")
                    )
                )
            }
        }
        return list
    }

    private fun timelineListToJson(events: List<TimelineEvent>): String {
        val array = JSONArray()
        events.forEach { e ->
            val obj = JSONObject().apply {
                put("timestamp", e.timestamp)
                put("title", e.title)
                put("description", e.description)
            }
            array.put(obj)
        }
        return array.toString()
    }

    private fun jsonToTimelineList(json: String): List<TimelineEvent> {
        val list = mutableListOf<TimelineEvent>()
        runCatching {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TimelineEvent(
                        timestamp = obj.optString("timestamp", ""),
                        title = obj.optString("title", ""),
                        description = obj.optString("description", "")
                    )
                )
            }
        }
        return list
    }

    private fun resultToJson(r: MedicaResult): String {
        val obj = JSONObject()
        obj.put("caseId", r.caseId)
        obj.put("urgencyLevel", r.urgencyLevel.name)
        obj.put("triageCode", r.triageCode)

        val obs = JSONArray()
        r.observations.forEach { obs.put(it) }
        obj.put("observations", obs)

        val conds = JSONArray()
        r.possibleConditions.forEach { conds.put(it) }
        obj.put("possibleConditions", conds)

        val actions = JSONArray()
        r.immediateActions.forEach { a ->
            val aObj = JSONObject().apply {
                put("priority", a.priority)
                put("title", a.title)
                put("detail", a.detail)
                put("isCritical", a.isCritical)
            }
            actions.put(aObj)
        }
        obj.put("immediateActions", actions)

        val warns = JSONArray()
        r.warnings.forEach { warns.put(it) }
        obj.put("warnings", warns)

        obj.put("uncertainty", r.uncertainty)
        obj.put("confidence", r.confidence.toDouble())

        val ev = JSONArray()
        r.evidenceIds.forEach { ev.put(it) }
        obj.put("evidenceIds", ev)

        obj.put("escalationRecommended", r.escalationRecommended)
        obj.put("escalationReason", r.escalationReason ?: "")
        obj.put("limitations", r.limitations)
        return obj.toString()
    }

    private fun jsonToResult(json: String): MedicaResult? {
        return runCatching {
            val obj = JSONObject(json)
            val obs = mutableListOf<String>()
            val obsArray = obj.optJSONArray("observations") ?: JSONArray()
            for (i in 0 until obsArray.length()) {
                obs.add(obsArray.getString(i))
            }

            val conds = mutableListOf<String>()
            val condsArray = obj.optJSONArray("possibleConditions") ?: JSONArray()
            for (i in 0 until condsArray.length()) {
                conds.add(condsArray.getString(i))
            }

            val actions = mutableListOf<ActionItem>()
            val actionsArray = obj.optJSONArray("immediateActions") ?: JSONArray()
            for (i in 0 until actionsArray.length()) {
                val aObj = actionsArray.getJSONObject(i)
                actions.add(
                    ActionItem(
                        priority = aObj.optInt("priority", 1),
                        title = aObj.optString("title", ""),
                        detail = aObj.optString("detail", ""),
                        isCritical = aObj.optBoolean("isCritical", false)
                    )
                )
            }

            val warns = mutableListOf<String>()
            val warnsArray = obj.optJSONArray("warnings") ?: JSONArray()
            for (i in 0 until warnsArray.length()) {
                warns.add(warnsArray.getString(i))
            }

            val evList = mutableListOf<String>()
            val evArray = obj.optJSONArray("evidenceIds") ?: JSONArray()
            for (i in 0 until evArray.length()) {
                evList.add(evArray.getString(i))
            }

            MedicaResult(
                caseId = obj.optString("caseId"),
                urgencyLevel = runCatching { UrgencyLevel.valueOf(obj.optString("urgencyLevel")) }.getOrDefault(UrgencyLevel.MODERATE),
                triageCode = obj.optString("triageCode", "STANDARD"),
                observations = obs,
                possibleConditions = conds,
                immediateActions = actions,
                warnings = warns,
                uncertainty = obj.optString("uncertainty", ""),
                confidence = obj.optDouble("confidence", 0.85).toFloat(),
                evidenceIds = evList,
                escalationRecommended = obj.optBoolean("escalationRecommended", false),
                escalationReason = obj.optString("escalationReason").takeIf { it.isNotBlank() },
                limitations = obj.optString("limitations", "Decision support heuristic.")
            )
        }.getOrNull()
    }
}
