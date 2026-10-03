package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.room.CaseJsonHelper
import com.example.model.CaseRecord
import com.example.model.MediaType
import com.example.model.UploadedMedia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context verifies Medica app name`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Medica", appName)
    }

    @Test
    fun `multimodal uploads create valid media items for photo video and audio`() {
        val photo = UploadedMedia("p1", MediaType.PHOTO, "wound.jpg", durationOrSize = "2.4 MB")
        val video = UploadedMedia("v1", MediaType.VIDEO, "chest_movement.mp4", durationOrSize = "00:24 · 14.8 MB")
        val audio = UploadedMedia("a1", MediaType.AUDIO, "breath_sounds.m4a", durationOrSize = "00:30 · Audio Note")

        assertEquals(MediaType.PHOTO, photo.type)
        assertEquals(MediaType.VIDEO, video.type)
        assertEquals(MediaType.AUDIO, audio.type)
    }

    @Test
    fun `case json helper roundtrip preserves case record data integrity`() {
        val original = CaseRecord(
            id = "MED-78901",
            title = "Male, 54, Chest Pain",
            demographic = "(M, 54)",
            timeAgo = "Created 1h ago",
            status = "Active",
            notes = "Substernal chest tightness with radiating pain to left jaw.",
            mediaItems = listOf(
                UploadedMedia("p1", MediaType.PHOTO, "ecg_capture.jpg"),
                UploadedMedia("v1", MediaType.VIDEO, "respiration.mp4"),
                UploadedMedia("a1", MediaType.AUDIO, "heart_tones.m4a")
            )
        )

        val entity = CaseJsonHelper.toEntity(original)
        val reconstructed = CaseJsonHelper.toRecord(entity)

        assertEquals(original.id, reconstructed.id)
        assertEquals(original.title, reconstructed.title)
        assertEquals(original.demographic, reconstructed.demographic)
        assertEquals(original.mediaItems.size, reconstructed.mediaItems.size)
        assertEquals(MediaType.PHOTO, reconstructed.mediaItems[0].type)
        assertEquals(MediaType.VIDEO, reconstructed.mediaItems[1].type)
        assertEquals(MediaType.AUDIO, reconstructed.mediaItems[2].type)
    }
}
