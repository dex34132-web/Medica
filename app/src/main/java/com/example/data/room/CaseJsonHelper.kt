package com.example.data.room

import com.example.model.CaseRecord
import com.example.model.MediaType
import com.example.model.UploadedMedia
import org.json.JSONArray
import org.json.JSONObject

object CaseJsonHelper {

    fun toEntity(case: CaseRecord): CaseEntity {
        val array = JSONArray()
        case.mediaItems.forEach { m ->
            val obj = JSONObject().apply {
                put("id", m.id)
                put("type", m.type.name)
                put("name", m.name)
                put("uriString", m.uriString ?: "")
                put("durationOrSize", m.durationOrSize)
                put("timestamp", m.timestamp)
            }
            array.put(obj)
        }

        return CaseEntity(
            id = case.id,
            title = case.title,
            demographic = case.demographic,
            timeAgo = case.timeAgo,
            timestampMs = case.timestampMs,
            status = case.status,
            notes = case.notes,
            mediaJson = array.toString()
        )
    }

    fun toRecord(entity: CaseEntity): CaseRecord {
        val media = mutableListOf<UploadedMedia>()
        runCatching {
            val array = JSONArray(entity.mediaJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                media.add(
                    UploadedMedia(
                        id = obj.optString("id", "media_$i"),
                        type = runCatching { MediaType.valueOf(obj.optString("type")) }.getOrDefault(MediaType.PHOTO),
                        name = obj.optString("name", "file"),
                        uriString = obj.optString("uriString").takeIf { it.isNotBlank() },
                        durationOrSize = obj.optString("durationOrSize", "Local file"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        }

        return CaseRecord(
            id = entity.id,
            title = entity.title,
            demographic = entity.demographic,
            timeAgo = entity.timeAgo,
            timestampMs = entity.timestampMs,
            status = entity.status,
            notes = entity.notes,
            mediaItems = media,
            isExpanded = false
        )
    }
}
