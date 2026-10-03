package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MockKnowledgeRepository
import com.example.data.room.AppDatabase
import com.example.model.AnalysisProgress
import com.example.model.Case
import com.example.model.CaseMedia
import com.example.model.CaseStatus
import com.example.model.EscalationStatus
import com.example.model.KnowledgeAsset
import com.example.model.KnowledgeType
import com.example.model.MediaType
import com.example.model.TimelineEvent
import com.example.service.AIService
import com.example.service.CaseService
import com.example.service.EscalationService
import com.example.service.KnowledgeService
import com.example.service.LocalCaseService
import com.example.service.MediaService
import com.example.service.MockAIService
import com.example.service.MockEscalationService
import com.example.service.MockKnowledgeService
import com.example.service.MockMediaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class MainTab {
    HOME,
    CASES,
    VAULT,
    SETTINGS
}

sealed class Screen {
    data object Home : Screen()
    data object CasesList : Screen()
    data object VaultList : Screen()
    data object Settings : Screen()

    data object NewCase : Screen()
    data object MediaReview : Screen()
    data object Analysis : Screen()
    data class Result(val caseId: String) : Screen()
    data class EvidenceViewer(val assetId: String, val fromCaseId: String? = null) : Screen()
    data class FlowchartViewer(val assetId: String) : Screen()
    data class VaultDetail(val assetId: String) : Screen()
    data class CaseDetail(val caseId: String) : Screen()
    data class Escalation(val caseId: String) : Screen()
}

class MedicaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val caseService: CaseService = LocalCaseService(database.caseDao())
    private val knowledgeService: KnowledgeService = MockKnowledgeService()
    private val aiService: AIService = MockAIService()
    private val mediaService: MediaService = MockMediaService()
    private val escalationService: EscalationService = MockEscalationService(caseService)

    // Navigation state
    private val _currentTab = MutableStateFlow(MainTab.HOME)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val screenStack = mutableListOf<Screen>(Screen.Home)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Cases
    val allCases: StateFlow<List<Case>> = caseService.getAllCases()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Draft Case being created
    private val _draftCase = MutableStateFlow(createNewDraftCase())
    val draftCase: StateFlow<Case> = _draftCase.asStateFlow()

    // Active Selected Case (for Detail or Result)
    private val _selectedCase = MutableStateFlow<Case?>(null)
    val selectedCase: StateFlow<Case?> = _selectedCase.asStateFlow()

    // Analysis Pipeline Progress
    private val _analysisProgress = MutableStateFlow<AnalysisProgress?>(null)
    val analysisProgress: StateFlow<AnalysisProgress?> = _analysisProgress.asStateFlow()

    // Vault search & filtering
    private val _vaultSearchQuery = MutableStateFlow("")
    val vaultSearchQuery: StateFlow<String> = _vaultSearchQuery.asStateFlow()

    private val _vaultSelectedType = MutableStateFlow<KnowledgeType?>(null)
    val vaultSelectedType: StateFlow<KnowledgeType?> = _vaultSelectedType.asStateFlow()

    val filteredVaultAssets: StateFlow<List<KnowledgeAsset>> = combine(
        knowledgeService.getAllAssets(),
        _vaultSearchQuery,
        _vaultSelectedType
    ) { assets, query, typeFilter ->
        assets.filter { asset ->
            val matchesType = typeFilter == null || asset.type == typeFilter
            val q = query.trim().lowercase()
            val matchesQuery = q.isEmpty() ||
                    asset.title.lowercase().contains(q) ||
                    asset.description.lowercase().contains(q) ||
                    asset.category.lowercase().contains(q) ||
                    asset.tags.any { it.lowercase().contains(q) } ||
                    asset.source.lowercase().contains(q)
            matchesType && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MockKnowledgeRepository.assets)

    // Settings
    var aiMode = MutableStateFlow("OFFLINE")
    var networkStatus = MutableStateFlow("OFFLINE (Field Isolation)")
    var emergencyContact = MutableStateFlow("Metro Emergency Dispatch / Medical Control")
    var isDarkTheme = MutableStateFlow(true)

    fun toggleDarkTheme(enabled: Boolean) {
        isDarkTheme.value = enabled
    }

    fun switchTab(tab: MainTab) {
        _currentTab.value = tab
        val screen = when (tab) {
            MainTab.HOME -> Screen.Home
            MainTab.CASES -> Screen.CasesList
            MainTab.VAULT -> Screen.VaultList
            MainTab.SETTINGS -> Screen.Settings
        }
        screenStack.clear()
        screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.lastIndex)
            val previous = screenStack.last()
            _currentScreen.value = previous
            when (previous) {
                is Screen.Home -> _currentTab.value = MainTab.HOME
                is Screen.CasesList -> _currentTab.value = MainTab.CASES
                is Screen.VaultList -> _currentTab.value = MainTab.VAULT
                is Screen.Settings -> _currentTab.value = MainTab.SETTINGS
                else -> Unit
            }
            return true
        }
        return false
    }

    private fun createNewDraftCase(): Case {
        val idNum = (1000..9999).random()
        val timeStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        return Case(
            id = "MDC-$idNum",
            title = "Field Case #MDC-$idNum",
            createdAtFormatted = "Today, $timeStr",
            timestampMs = System.currentTimeMillis(),
            media = emptyList(),
            context = "",
            status = CaseStatus.DRAFT,
            timeline = listOf(
                TimelineEvent(timeStr, "Case Created", "Responder initiated case in field")
            )
        )
    }

    fun startNewCase() {
        _draftCase.value = createNewDraftCase()
        navigateTo(Screen.NewCase)
    }

    fun updateDraftContext(newContext: String) {
        _draftCase.value = _draftCase.value.copy(context = newContext)
    }

    fun addPhotoToDraft(filename: String? = null, description: String = "") {
        val media = mediaService.createPhotoMedia(filename, description)
        _draftCase.value = _draftCase.value.copy(
            media = _draftCase.value.media + media
        )
    }

    fun addVideoToDraft(filename: String? = null, duration: String = "00:25", description: String = "") {
        val media = mediaService.createVideoMedia(filename, duration, description)
        _draftCase.value = _draftCase.value.copy(
            media = _draftCase.value.media + media
        )
    }

    fun addAudioToDraft(filename: String? = null, duration: String = "00:30", description: String = "") {
        val media = mediaService.createAudioMedia(filename, duration, description)
        _draftCase.value = _draftCase.value.copy(
            media = _draftCase.value.media + media
        )
    }

    fun addImportedMediaToDraft(name: String, type: MediaType) {
        val media = mediaService.createImportedMedia(name, type)
        _draftCase.value = _draftCase.value.copy(
            media = _draftCase.value.media + media
        )
    }

    fun removeMediaFromDraft(mediaId: String) {
        _draftCase.value = _draftCase.value.copy(
            media = _draftCase.value.media.filterNot { it.id == mediaId }
        )
    }

    fun loadPresetScenario(scenario: String) {
        val presets = mediaService.getPresetMediaForScenario(scenario)
        val contextText = when (scenario) {
            "RESPIRATORY" -> "Male approx 45 yo. Rapid onset severe inspiratory stridor, intercostal indrawing, sat 89% on ambient air."
            "HEMORRHAGE" -> "Worksite trauma. Deep proximal femoral laceration with bright red pulsing bleed. Estimated 600ml blood loss."
            "TRAUMA" -> "Fall from height onto concrete. Brief LOC ~30s. Scalp swelling at right temporal region. Repetitive questioning."
            else -> ""
        }
        val titleText = when (scenario) {
            "RESPIRATORY" -> "Acute Airway Stridor & Tachypnea"
            "HEMORRHAGE" -> "Femoral Laceration & Hemorrhagic Risk"
            "TRAUMA" -> "Blunt Head Impact & Concussion"
            else -> "Field Emergency Case"
        }
        _draftCase.value = _draftCase.value.copy(
            title = titleText,
            media = presets,
            context = contextText
        )
    }

    fun executeAnalysisForDraft() {
        navigateTo(Screen.Analysis)
        viewModelScope.launch {
            val current = _draftCase.value
            aiService.runMultimodalAnalysis(current).collect { progress ->
                _analysisProgress.value = progress
            }

            val result = aiService.generateResult(current)
            val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val updated = current.copy(
                status = CaseStatus.COMPLETED,
                result = result,
                escalationStatus = if (result.escalationRecommended) EscalationStatus.RECOMMENDED else EscalationStatus.NOT_REQUIRED,
                timeline = current.timeline + listOf(
                    TimelineEvent(timeStr, "Analysis Completed", "Triage decision: ${result.urgencyLevel.label} (${result.triageCode})")
                )
            )

            caseService.saveCase(updated)
            _draftCase.value = updated
            _selectedCase.value = updated

            navigateTo(Screen.Result(updated.id))
        }
    }

    fun selectCase(caseId: String) {
        viewModelScope.launch {
            val c = allCases.value.find { it.id == caseId }
            _selectedCase.value = c
            if (c != null) {
                navigateTo(Screen.CaseDetail(caseId))
            }
        }
    }

    fun reopenResult(caseId: String) {
        val c = allCases.value.find { it.id == caseId }
        _selectedCase.value = c
        if (c?.result != null) {
            navigateTo(Screen.Result(caseId))
        }
    }

    fun confirmEscalation(caseId: String, notes: String) {
        viewModelScope.launch {
            val target = allCases.value.find { it.id == caseId } ?: _selectedCase.value ?: return@launch
            val updated = escalationService.confirmEscalation(target, emergencyContact.value, notes)
            _selectedCase.value = updated
            navigateTo(Screen.CaseDetail(caseId))
        }
    }

    fun rejectEscalation(caseId: String, reason: String) {
        viewModelScope.launch {
            val target = allCases.value.find { it.id == caseId } ?: _selectedCase.value ?: return@launch
            val updated = escalationService.rejectEscalation(target, reason)
            _selectedCase.value = updated
            navigateTo(Screen.CaseDetail(caseId))
        }
    }

    fun getAssetById(id: String): KnowledgeAsset? {
        return knowledgeService.getAssetById(id)
    }

    fun getRelatedAssets(assetId: String): List<KnowledgeAsset> {
        return knowledgeService.getRelatedAssets(assetId)
    }

    fun setVaultQuery(q: String) {
        _vaultSearchQuery.value = q
    }

    fun setVaultFilter(type: KnowledgeType?) {
        _vaultSelectedType.value = type
    }
}
