package com.ducnnn.blessenger.ui.contacts

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactsScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ContactsState())
    private val uiState: StateFlow<ContactsState> = _uiState.asStateFlow()

}