package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuditReport
import com.example.data.Language
import com.example.data.PresetTemplate
import com.example.data.VideoType
import com.example.data.YtInputData
import com.example.data.local.AppDatabase
import com.example.data.local.AuditRepository
import com.example.network.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class YtSeoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuditRepository by lazy {
        val db = AppDatabase.getDatabase(application)
        AuditRepository(db.auditDao())
    }

    val savedAudits: StateFlow<List<AuditReport>> = repository.allSavedAudits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _inputState = MutableStateFlow(
        YtInputData(
            topic = "১০ মিনিটে সুস্বাদু চিকেন ফ্রাই রেসিপি",
            draftTitle = "চিকেন ফ্রাই তৈরি করার নিয়ম",
            targetKeywords = "chicken fry recipe, bangladeshi recipe, easy cooking",
            draftDescription = "সহজে বাড়িতে বানিয়ে ফেলুন ক্রিসপি চিকেন ফ্রাই।",
            videoType = VideoType.SHORTS,
            language = Language.BENGALI
        )
    )
    val inputState: StateFlow<YtInputData> = _inputState.asStateFlow()

    private val _auditReportState = MutableStateFlow<AuditReport?>(null)
    val auditReportState: StateFlow<AuditReport?> = _auditReportState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAutoFixing = MutableStateFlow(false)
    val isAutoFixing: StateFlow<Boolean> = _isAutoFixing.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isSavedCurrent = MutableStateFlow(false)
    val isSavedCurrent: StateFlow<Boolean> = _isSavedCurrent.asStateFlow()

    fun updateTopic(topic: String) {
        _inputState.value = _inputState.value.copy(topic = topic)
    }

    fun updateDraftTitle(title: String) {
        _inputState.value = _inputState.value.copy(draftTitle = title)
    }

    fun updateTargetKeywords(keywords: String) {
        _inputState.value = _inputState.value.copy(targetKeywords = keywords)
    }

    fun updateDraftDescription(desc: String) {
        _inputState.value = _inputState.value.copy(draftDescription = desc)
    }

    fun updateVideoType(type: VideoType) {
        _inputState.value = _inputState.value.copy(videoType = type)
    }

    fun updateLanguage(lang: Language) {
        _inputState.value = _inputState.value.copy(language = lang)
    }

    fun applyPreset(preset: PresetTemplate) {
        _inputState.value = preset.inputData
    }

    fun runAudit(isAutoFix: Boolean = false) {
        val currentInput = _inputState.value
        if (currentInput.topic.isBlank() && currentInput.draftTitle.isBlank()) {
            _errorMessage.value = if (currentInput.language == Language.BENGALI) {
                "দয়া করে ভিডিওর বিষয় (Topic) বা টাইটেল প্রদান করুন।"
            } else {
                "Please enter a video topic or draft title."
            }
            return
        }

        viewModelScope.launch {
            if (isAutoFix) {
                _isAutoFixing.value = true
            } else {
                _isLoading.value = true
            }
            _errorMessage.value = null
            _isSavedCurrent.value = false

            try {
                val report = GeminiApiService.analyzeAndAuditVideo(currentInput, isAutoFix = isAutoFix)
                _auditReportState.value = report
            } catch (e: Exception) {
                _errorMessage.value = "Audit Failed: ${e.message}"
            } finally {
                _isLoading.value = false
                _isAutoFixing.value = false
            }
        }
    }

    fun saveCurrentAudit() {
        val report = _auditReportState.value ?: return
        viewModelScope.launch {
            try {
                repository.saveAudit(report)
                _isSavedCurrent.value = true
            } catch (e: Exception) {
                _errorMessage.value = "Save Failed: ${e.message}"
            }
        }
    }

    fun deleteSavedAudit(report: AuditReport) {
        viewModelScope.launch {
            try {
                repository.deleteAudit(report.timestamp)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun setAuditReport(report: AuditReport) {
        _auditReportState.value = report
        _isSavedCurrent.value = true
    }
}
