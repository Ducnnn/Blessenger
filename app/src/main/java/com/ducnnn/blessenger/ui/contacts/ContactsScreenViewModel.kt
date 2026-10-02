package com.ducnnn.blessenger.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ducnnn.blessenger.db.DatabaseManager
import com.ducnnn.blessenger.user.UserDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ContactsScreenViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ContactsState())
    val uiState: StateFlow<ContactsState> = _uiState.asStateFlow()

    init {
        observeContacts()
    }

    private fun observeContacts() {
        viewModelScope.launch {
            DatabaseManager.observeContacts().collect { newMap ->
                _uiState.update {
                    it.copy(contacts = newMap)
                }
            }
        }
    }

    fun onNewContactIdChanged(id: String) {
        _uiState.update {
            it.copy(newContactId = id.trim())
        }
    }

    fun onNewContactNameChanged(name: String) {
        _uiState.update {
            it.copy(newContactName = name.trim())
        }
    }

    fun showAddContactDialog() {
        _uiState.update {
            it.copy(showAddContactDialog = true)
        }
    }

    fun dismissAddContactDialog() {
        _uiState.update {
            it.copy(
                newContactName = "",
                newContactId = "",
                addError = null,
                showAddContactDialog = false
            )
        }
    }

    fun showDeleteContactDialog(id: String) {
        _uiState.update {
            it.copy(contactPendingDeletion = id)
        }
    }

    fun dismissDeleteContactDialog() {
        _uiState.update {
            it.copy(contactPendingDeletion = null)
        }
    }

    fun deleteContact() {
        viewModelScope.launch {
            DatabaseManager.deleteContact(uiState.value.contactPendingDeletion!!)
        }
        dismissDeleteContactDialog()
    }


    private fun validateContactId(id: String): AddContactErrors =
        when {
            !Regex("[0-9a-f]{8}").matches(id.lowercase()) -> AddContactErrors.IncorrectId
            id == UserDataManager.getSavedId() -> AddContactErrors.SameId
            id == "ffffffff" -> AddContactErrors.CommonId
            else -> AddContactErrors.Success
        }


    fun addContact(id: String, name: String) {
        val confirmAddAccountResult = validateContactId(id)
        if (confirmAddAccountResult != AddContactErrors.Success) {
            _uiState.update {
                it.copy(addError = confirmAddAccountResult.errorMessage)
            }
            return
        }

        _uiState.update {
            it.copy(newContactName = name.ifEmpty { id })
        }

        viewModelScope.launch {
            DatabaseManager.addContact(id, uiState.value.newContactName.lowercase())
        }
        dismissAddContactDialog()
    }


}

enum class AddContactErrors(val errorMessage: String?) {
    Success(null),
    SameId("You cannot add id of this device"),
    CommonId("You cannot add common id that is used for common mesh network chat"),
    IncorrectId("Id must be 8 letters long and contain only hexadecimals")
}