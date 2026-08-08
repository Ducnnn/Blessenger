package com.ducnnn.blessenger.ui.settings

import androidx.lifecycle.ViewModel
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsState(UserDataManager.getSavedId()))
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    fun onUserIdChanged(newId: String){
        UserDataManager.saveUserId(newId)
        _uiState.update { currentState ->
            currentState.copy(userId = newId)
        }

    }
}