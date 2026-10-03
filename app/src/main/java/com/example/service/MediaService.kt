package com.example.service

import com.example.model.CaseMedia
import com.example.model.MediaType
import java.util.UUID

interface MediaService {
    fun createPhotoMedia(filename: String? = null, description: String = ""): CaseMedia
    fun createVideoMedia(filename: String? = null, duration: String = "00:25", description: String = ""): CaseMedia
    fun createAudioMedia(filename: String? = null, duration: String = "00:30", description: String = ""): CaseMedia
    fun createImportedMedia(name: String, type: MediaType, size: String = "1.5 MB"): CaseMedia
    fun getPresetMediaForScenario(scenario: String): List<CaseMedia>
}

class MockMediaService : MediaService {

    override fun createPhotoMedia(filename: String?, description: String): CaseMedia {
        val id = "MED-" + UUID.randomUUID().toString().take(6).uppercase()
        val name = filename ?: "photo_field_${System.currentTimeMillis() % 1000}.jpg"
        return CaseMedia(
            id = id,
            type = MediaType.IMAGE,
            filename = name,
            description = description.ifEmpty { "Field capture: Clinical presentation visual" },
            size = "2.8 MB",
            local = true,
            status = "READY",
            mockPreviewType = "PHOTO_CAPTURE"
        )
    }

    override fun createVideoMedia(filename: String?, duration: String, description: String): CaseMedia {
        val id = "MED-" + UUID.randomUUID().toString().take(6).uppercase()
        val name = filename ?: "video_recording_${System.currentTimeMillis() % 1000}.mp4"
        return CaseMedia(
            id = id,
            type = MediaType.VIDEO,
            filename = name,
            description = description.ifEmpty { "Field clip: Respiratory effort & motion" },
            duration = duration,
            size = "18.4 MB",
            local = true,
            status = "READY",
            mockPreviewType = "AIRWAY_VIDEO"
        )
    }

    override fun createAudioMedia(filename: String?, duration: String, description: String): CaseMedia {
        val id = "MED-" + UUID.randomUUID().toString().take(6).uppercase()
        val name = filename ?: "audio_tracheal_${System.currentTimeMillis() % 1000}.m4a"
        return CaseMedia(
            id = id,
            type = MediaType.AUDIO,
            filename = name,
            description = description.ifEmpty { "Field recording: Auscultation acoustics" },
            duration = duration,
            size = "760 KB",
            local = true,
            status = "READY",
            mockPreviewType = "STRIDOR_AUDIO"
        )
    }

    override fun createImportedMedia(name: String, type: MediaType, size: String): CaseMedia {
        val id = "MED-" + UUID.randomUUID().toString().take(6).uppercase()
        return CaseMedia(
            id = id,
            type = type,
            filename = name,
            description = "Imported field asset",
            size = size,
            local = true,
            status = "READY",
            mockPreviewType = "IMPORTED"
        )
    }

    override fun getPresetMediaForScenario(scenario: String): List<CaseMedia> {
        return when (scenario) {
            "RESPIRATORY" -> listOf(
                createPhotoMedia("patient_tripod_posture.jpg", "Anterior neck with sternal retraction"),
                createAudioMedia("tracheal_stridor_sample.m4a", "00:32", "Laryngeal acoustic inspiratory sound"),
                createVideoMedia("breathing_effort_clip.mp4", "00:20", "Intercostal indrawing and tachypnea")
            )
            "HEMORRHAGE" -> listOf(
                createPhotoMedia("femoral_wound_photo.jpg", "Deep puncture with arterial oozing"),
                createAudioMedia("vocal_perfusion_eval.m4a", "00:15", "Slurred speech and faint responses")
            )
            "TRAUMA" -> listOf(
                createPhotoMedia("temporal_contusion.jpg", "Scalp contusion at temporal bone"),
                createVideoMedia("pupillary_reflex_check.mp4", "00:18", "Evaluation of pupil reactivity")
            )
            else -> listOf(
                createPhotoMedia("field_overview.jpg", "Patient primary survey inspection")
            )
        }
    }
}
