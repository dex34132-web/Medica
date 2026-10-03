package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.room.AppDatabase
import com.example.model.CaseRecord
import com.example.model.LocalAiModel
import com.example.model.MediaType
import com.example.model.UploadedMedia
import com.example.model.VaultCaseFile
import com.example.model.VaultMediaCard
import com.example.service.CaseService
import com.example.service.LocalCaseService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class MainTab {
    HOME,
    CASES,
    VAULT,
    SETTINGS
}

class MedicaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val caseService: CaseService = LocalCaseService(database.caseDao())

    // Active bottom navigation tab
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Cases stream from Room database
    val allCases: StateFlow<List<CaseRecord>> = caseService.getAllCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Cases screen tab: "Active", "Pending", "All"
    private val _casesFilterTab = MutableStateFlow("Active")
    val casesFilterTab: StateFlow<String> = _casesFilterTab.asStateFlow()

    // Cases search
    private val _casesSearchQuery = MutableStateFlow("")
    val casesSearchQuery: StateFlow<String> = _casesSearchQuery.asStateFlow()

    private val _isCasesSearchActive = MutableStateFlow(false)
    val isCasesSearchActive: StateFlow<Boolean> = _isCasesSearchActive.asStateFlow()

    // Expanded case IDs in list
    private val _expandedCaseIds = MutableStateFlow<Set<String>>(emptySet())
    val expandedCaseIds: StateFlow<Set<String>> = _expandedCaseIds.asStateFlow()

    // New Case Upload Screen / Dialog state
    private val _showNewCaseScreen = MutableStateFlow(false)
    val showNewCaseScreen: StateFlow<Boolean> = _showNewCaseScreen.asStateFlow()

    // Draft Case inputs
    var draftTitle = MutableStateFlow("Male, 54, Chest Pain")
    var draftDemographic = MutableStateFlow("(M, 54)")
    var draftNotes = MutableStateFlow("")
    private val _draftMedia = MutableStateFlow<List<UploadedMedia>>(emptyList())
    val draftMedia: StateFlow<List<UploadedMedia>> = _draftMedia.asStateFlow()

    // Medical Vault Screen state
    private val _vaultCategory = MutableStateFlow("Medical Case Files")
    val vaultCategory: StateFlow<String> = _vaultCategory.asStateFlow()

    val vaultFiles = listOf(
        VaultCaseFile("vf_1", "CLINICAL_TRIAL_DATASET_01.XLSX", "XLSX", "Access-ready 2m ago", isLocked = true, isPrimary = true),
        VaultCaseFile("vf_2", "CASE_STUDY_DOCUMENT_02.PDF", "PDF", "Access-ready 1m ago", isLocked = true, isPrimary = false)
    )

    val vaultMediaCards = listOf(
        VaultMediaCard("vm_1", "Video Tutorial: Chest Pain Assessment", "VIDEO", "12:40 HD", "12:40"),
        VaultMediaCard("vm_2", "Interactive Flowchart: Abdominal Pain Triage", "FLOWCHART", "8 Nodes · Triage Guide"),
        VaultMediaCard("vm_3", "Anatomy Image Pack: Spinal Cord", "IMAGE", "6 High-Res References"),
        VaultMediaCard("vm_4", "Case Study: Male, 54, Chest Pain", "VIDEO", "08:15 Clinical Breakdown", "08:15")
    )

    // Settings Screen AI Models
    val localAiModels = listOf(
        LocalAiModel("m1", "Llama 3 8B Instruct", "4.2GB", "Status: Downloaded v1.1", downloadProgress = 0.65f, iconType = "META"),
        LocalAiModel("m2", "Mistral 7B Instruct v0.3", "3.8GB", "Currently Active", isCurrentlyActive = true, iconType = "META"),
        LocalAiModel("m3", "Mistral 7B Instruct v0.3", "3.8GB", "Status: Downloaded, v0.3", isCurrentlyActive = false, iconType = "ORANGE"),
        LocalAiModel("m4", "Gemma 7B", "3.5GB", "Status: Not Downloaded", isCurrentlyActive = false, iconType = "GRID")
    )

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // Theme: default Dark mode matching screenshots
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Active detail/viewer modal for files/media
    private val _viewingMediaTitle = MutableStateFlow<String?>(null)
    val viewingMediaTitle: StateFlow<String?> = _viewingMediaTitle.asStateFlow()

    // Backend Architecture Visualizer screen state
    private val _showArchitectureScreen = MutableStateFlow(false)
    val showArchitectureScreen: StateFlow<Boolean> = _showArchitectureScreen.asStateFlow()

    fun openArchitectureScreen() {
        _showArchitectureScreen.value = true
    }

    fun closeArchitectureScreen() {
        _showArchitectureScreen.value = false
    }

    init {
        viewModelScope.launch {
            caseService.seedInitialCasesIfEmpty()
        }
    }

    fun switchTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setCasesFilterTab(tab: String) {
        _casesFilterTab.value = tab
    }

    fun setCasesSearchQuery(query: String) {
        _casesSearchQuery.value = query
    }

    fun toggleCasesSearch() {
        _isCasesSearchActive.value = !_isCasesSearchActive.value
        if (!_isCasesSearchActive.value) {
            _casesSearchQuery.value = ""
        }
    }

    fun toggleCaseExpanded(id: String) {
        val current = _expandedCaseIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        _expandedCaseIds.value = current
    }

    fun openNewCaseScreen() {
        draftTitle.value = "Male, 54, Chest Pain"
        draftDemographic.value = "(M, 54)"
        draftNotes.value = ""
        _draftMedia.value = emptyList()
        _showNewCaseScreen.value = true
    }

    fun closeNewCaseScreen() {
        _showNewCaseScreen.value = false
    }

    fun addPhotoUpload(filename: String = "patient_clinical_photo.jpg", uri: String? = null) {
        val id = UUID.randomUUID().toString().take(6)
        val media = UploadedMedia(
            id = id,
            type = MediaType.PHOTO,
            name = filename,
            uriString = uri,
            durationOrSize = "2.4 MB · High Resolution"
        )
        _draftMedia.value = _draftMedia.value + media
    }

    fun addVideoUpload(filename: String = "respiratory_movement_clip.mp4", uri: String? = null, duration: String = "00:24") {
        val id = UUID.randomUUID().toString().take(6)
        val media = UploadedMedia(
            id = id,
            type = MediaType.VIDEO,
            name = filename,
            uriString = uri,
            durationOrSize = "$duration · 14.8 MB"
        )
        _draftMedia.value = _draftMedia.value + media
    }

    fun addMicrophoneAudioUpload(filename: String = "auscultation_breath_memo.m4a", uri: String? = null, duration: String = "00:32") {
        val id = UUID.randomUUID().toString().take(6)
        val media = UploadedMedia(
            id = id,
            type = MediaType.AUDIO,
            name = filename,
            uriString = uri,
            durationOrSize = "$duration · Audio Note"
        )
        _draftMedia.value = _draftMedia.value + media
    }

    fun removeUploadedMedia(id: String) {
        _draftMedia.value = _draftMedia.value.filterNot { it.id == id }
    }

    fun saveDraftCase() {
        viewModelScope.launch {
            val num = (78900..79999).random()
            val newCase = CaseRecord(
                id = "MED-$num",
                title = draftTitle.value.ifBlank { "Acute Case Assessment" },
                demographic = draftDemographic.value.ifBlank { "(M, 54)" },
                timeAgo = "Just now",
                timestampMs = System.currentTimeMillis(),
                status = "Active",
                notes = draftNotes.value.ifBlank { "Field triage observations and attached clinical uploads." },
                mediaItems = _draftMedia.value
            )
            caseService.saveCase(newCase)
            _showNewCaseScreen.value = false
            _currentTab.value = MainTab.CASES
        }
    }

    fun deleteCase(id: String) {
        viewModelScope.launch {
            caseService.deleteCase(id)
        }
    }

    fun setVaultCategory(cat: String) {
        _vaultCategory.value = cat
    }

    fun openMediaViewer(title: String) {
        _viewingMediaTitle.value = title
    }

    fun closeMediaViewer() {
        _viewingMediaTitle.value = null
    }

    fun triggerSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            delay(1200)
            _isSyncing.value = false
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
}
