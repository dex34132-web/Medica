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
import com.example.ondevice.manager.LocalModelManager
import com.example.ondevice.models.LocalMediaInput
import com.example.ondevice.models.LocalMedicaResult
import com.example.ondevice.models.ModalityType
import com.example.ondevice.models.MultimodalActionPlan
import com.example.ondevice.neural.ExternalNeuralVaultNetwork
import com.example.ondevice.rag.LocalRagEngine
import com.example.ondevice.runtime.LocalInferenceEngine
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
    CHAT,
    CASES,
    VAULT,
    SETTINGS;

    companion object {
        val HOME = CHAT
    }
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val isUser: Boolean,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val attachedMedia: List<LocalMediaInput> = emptyList(),
    val multimodalPlan: MultimodalActionPlan? = null,
    val modelUsed: String = "Gemini Nano (On-Device)",
    val latencyMs: Long = 0
)

data class DownloadableModelItem(
    val id: String,
    val name: String,
    val provider: String,
    val size: String,
    val quantization: String,
    val memoryRequired: String,
    val isDownloaded: Boolean,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isActive: Boolean = false,
    val sha256: String = "sha256-verified-weight"
)

class MedicaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val caseService: CaseService = LocalCaseService(database.caseDao())

    // Active bottom navigation tab
    private val _currentTab = MutableStateFlow(MainTab.CHAT)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    // Chat Conversation Stream
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _selectedChatModel = MutableStateFlow("Gemini Nano (On-Device)")
    val selectedChatModel: StateFlow<String> = _selectedChatModel.asStateFlow()

    // Downloadable Models Catalog for Settings
    private val _downloadableModels = MutableStateFlow<List<DownloadableModelItem>>(
        listOf(
            DownloadableModelItem(
                id = "gemini-nano",
                name = "Gemini Nano (AICore)",
                provider = "Google System Service",
                size = "1.45 GB",
                quantization = "INT4 System",
                memoryRequired = "2.8 GB RAM",
                isDownloaded = true,
                isActive = true
            ),
            DownloadableModelItem(
                id = "llama-3.2-3b",
                name = "Llama 3.2 3B Instruct Mobile",
                provider = "Meta · Google LiteRT",
                size = "1.89 GB",
                quantization = "4-bit Q4_K_M",
                memoryRequired = "2.4 GB RAM",
                isDownloaded = true,
                isActive = false
            ),
            DownloadableModelItem(
                id = "gemma-2b",
                name = "Gemma 2B Ultra-Compact",
                provider = "Google DeepMind · LiteRT",
                size = "1.12 GB",
                quantization = "INT4 Quantized",
                memoryRequired = "1.4 GB RAM",
                isDownloaded = false,
                isActive = false
            ),
            DownloadableModelItem(
                id = "mistral-7b",
                name = "Mistral 7B Mobile v0.3",
                provider = "Mistral AI · ONNX Mobile",
                size = "3.80 GB",
                quantization = "4-bit Q4_0",
                memoryRequired = "4.2 GB RAM",
                isDownloaded = false,
                isActive = false
            ),
            DownloadableModelItem(
                id = "whisper-mobile",
                name = "Whisper Mobile (Acoustics & Voice)",
                provider = "OpenAI · LiteRT Audio",
                size = "140 MB",
                quantization = "INT8 Precision",
                memoryRequired = "250 MB RAM",
                isDownloaded = true,
                isActive = false
            ),
            DownloadableModelItem(
                id = "mobilenet-v4",
                name = "MobileNetV4 Medical Vision",
                provider = "Google Vision · LiteRT",
                size = "48 MB",
                quantization = "INT8 Precision",
                memoryRequired = "90 MB RAM",
                isDownloaded = true,
                isActive = false
            )
        )
    )
    val downloadableModels: StateFlow<List<DownloadableModelItem>> = _downloadableModels.asStateFlow()

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

    // On-Device Architecture Visualizer screen state
    private val _showOnDeviceArchitectureScreen = MutableStateFlow(false)
    val showOnDeviceArchitectureScreen: StateFlow<Boolean> = _showOnDeviceArchitectureScreen.asStateFlow()

    fun openArchitectureScreen() {
        _showArchitectureScreen.value = true
    }

    fun closeArchitectureScreen() {
        _showArchitectureScreen.value = false
    }

    fun openOnDeviceArchitectureScreen() {
        _showOnDeviceArchitectureScreen.value = true
    }

    fun closeOnDeviceArchitectureScreen() {
        _showOnDeviceArchitectureScreen.value = false
    }

    // On-device AI case assessment state
    private val _activeCaseEvaluation = MutableStateFlow<LocalMedicaResult?>(null)
    val activeCaseEvaluation: StateFlow<LocalMedicaResult?> = _activeCaseEvaluation.asStateFlow()

    private val _evaluatingCaseId = MutableStateFlow<String?>(null)
    val evaluatingCaseId: StateFlow<String?> = _evaluatingCaseId.asStateFlow()

    fun evaluateCaseLocally(caseRecord: CaseRecord) {
        viewModelScope.launch {
            _evaluatingCaseId.value = caseRecord.id
            delay(150)
            val mediaInputs = caseRecord.mediaItems.map { item ->
                val mod = when (item.type) {
                    MediaType.PHOTO -> ModalityType.IMAGE
                    MediaType.VIDEO -> ModalityType.VIDEO
                    MediaType.AUDIO -> ModalityType.AUDIO
                }
                LocalMediaInput(item.id, mod, item.name, "Local captured file: ${item.durationOrSize}")
            }
            val contextScope = LocalRagEngine.buildLocalContext(
                caseId = caseRecord.id,
                chiefComplaint = caseRecord.title,
                demographics = caseRecord.demographic,
                observations = caseRecord.notes,
                attachedMedia = mediaInputs
            )
            val model = LocalModelManager.activeModel.value
            val result = LocalInferenceEngine.synthesizeLocalReasoning(
                context = contextScope,
                modelSpec = model,
                runtime = model.targetRuntime
            )
            _activeCaseEvaluation.value = result
            _evaluatingCaseId.value = null
        }
    }

    fun dismissCaseEvaluation() {
        _activeCaseEvaluation.value = null
        _evaluatingCaseId.value = null
    }

    // External Neural Vault Query state
    private val _neuralVaultQueryAnswer = MutableStateFlow<MultimodalActionPlan?>(null)
    val neuralVaultQueryAnswer: StateFlow<MultimodalActionPlan?> = _neuralVaultQueryAnswer.asStateFlow()

    private val _isNeuralVaultQuerying = MutableStateFlow(false)
    val isNeuralVaultQuerying: StateFlow<Boolean> = _isNeuralVaultQuerying.asStateFlow()

    fun queryNeuralVault(query: String) {
        viewModelScope.launch {
            _isNeuralVaultQuerying.value = true
            val plan = ExternalNeuralVaultNetwork.queryNeuralVault(query)
            _neuralVaultQueryAnswer.value = plan
            _isNeuralVaultQuerying.value = false
        }
    }

    fun dismissNeuralVaultAnswer() {
        _neuralVaultQueryAnswer.value = null
        _isNeuralVaultQuerying.value = false
    }

    // Vault search and filtering state
    private val _vaultSearchQuery = MutableStateFlow("")
    val vaultSearchQuery: StateFlow<String> = _vaultSearchQuery.asStateFlow()

    private val _selectedVaultTypeFilter = MutableStateFlow<String?>("ALL")
    val selectedVaultTypeFilter: StateFlow<String?> = _selectedVaultTypeFilter.asStateFlow()

    fun setVaultSearchQuery(q: String) {
        _vaultSearchQuery.value = q
    }

    fun setVaultTypeFilter(filter: String?) {
        _selectedVaultTypeFilter.value = filter
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

    // Chat Actions
    fun sendChatMessage(text: String, media: List<LocalMediaInput> = emptyList()) {
        if (text.isBlank() && media.isEmpty()) return

        val userMsg = ChatMessage(
            isUser = true,
            text = text,
            attachedMedia = media
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiGenerating.value = true

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val plan = ExternalNeuralVaultNetwork.recognizeAndSynthesizeSteps(
                caseId = "CHAT-${System.currentTimeMillis() % 10000}",
                complaint = text,
                demographics = "(Field Emergency Consultation)",
                observations = text,
                attachedMedia = media
            )
            val latency = (System.currentTimeMillis() - startTime).coerceAtLeast(110)

            val responseBuilder = StringBuilder()
            responseBuilder.append("### ${plan.conditionRecognized}\n\n")
            responseBuilder.append("${plan.aiExplanation}\n\n")

            responseBuilder.append("**Priority Clinical Steps:**\n")
            plan.textSteps.forEachIndexed { i, step ->
                responseBuilder.append("${i + 1}. $step\n")
            }

            if (plan.contraindications.isNotEmpty()) {
                responseBuilder.append("\n**⚠️ Critical Contraindications:**\n")
                plan.contraindications.forEach { caution ->
                    responseBuilder.append("• $caution\n")
                }
            }

            val assistantMsg = ChatMessage(
                isUser = false,
                text = responseBuilder.toString().trim(),
                multimodalPlan = plan,
                modelUsed = _selectedChatModel.value,
                latencyMs = latency
            )

            _chatMessages.value = _chatMessages.value + assistantMsg
            _isAiGenerating.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun selectChatModel(modelName: String) {
        _selectedChatModel.value = modelName
    }

    // Model Download Actions
    fun downloadModel(modelId: String) {
        viewModelScope.launch {
            _downloadableModels.value = _downloadableModels.value.map {
                if (it.id == modelId) it.copy(isDownloading = true, downloadProgress = 0.05f) else it
            }
            for (step in 1..10) {
                delay(120)
                _downloadableModels.value = _downloadableModels.value.map {
                    if (it.id == modelId) it.copy(downloadProgress = (step * 0.1f).coerceAtMost(1f)) else it
                }
            }
            _downloadableModels.value = _downloadableModels.value.map {
                if (it.id == modelId) it.copy(isDownloading = false, isDownloaded = true, downloadProgress = 1f) else it
            }
        }
    }

    fun activateModel(modelId: String) {
        _downloadableModels.value = _downloadableModels.value.map {
            it.copy(isActive = (it.id == modelId))
        }
        val target = _downloadableModels.value.find { it.id == modelId }
        if (target != null) {
            _selectedChatModel.value = target.name
        }
    }

    fun deleteModel(modelId: String) {
        _downloadableModels.value = _downloadableModels.value.map {
            if (it.id == modelId) it.copy(isDownloaded = false, isActive = false, downloadProgress = 0f) else it
        }
    }
}
