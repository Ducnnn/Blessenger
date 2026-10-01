package com.ducnnn.blessenger.ui.contacts

data class ContactsState(
    val contacts: Map<String, String> = emptyMap(),
    val showAddContactDialog: Boolean = false,
    val newContactId: String = "",
    val newContactName: String = "",
    val addError: String? = null,
    val contactPendingDeletion: String? = null
)