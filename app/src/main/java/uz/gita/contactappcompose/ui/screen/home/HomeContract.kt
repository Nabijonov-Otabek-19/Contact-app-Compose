package uz.gita.contactappcompose.ui.screen.home

import kotlinx.coroutines.flow.StateFlow
import uz.gita.contactappcompose.data.common.ContactData

interface HomeContract {

    sealed interface Intent {
        class OpenEditContact(val updateData: ContactData) : Intent
        class Delete(val contact: ContactData) : Intent
        object OpenAddContact : Intent
    }

    data class UiState(
        val contacts: List<ContactData> = listOf(),
        val updateData: ContactData? = null
    )

    interface ViewModel {
        val uiState: StateFlow<UiState>
        fun onEventDispatcher(intent: Intent)
    }

    interface Direction {
        suspend fun navigateToAddEditScreen(data: ContactData?)
    }
}