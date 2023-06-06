package uz.gita.contactappcompose.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uz.gita.contactappcompose.domain.repository.AppRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    private val repository: AppRepository,
    private val direction: HomeDirection
) : HomeContract.ViewModel, ViewModel() {

    override val uiState = MutableStateFlow(HomeContract.UiState())

    init {
        repository.retrieveAllContacts()
            .onEach { contacts -> uiState.update { it.copy(contacts = contacts) } }
            .launchIn(viewModelScope)
    }

    override fun onEventDispatcher(intent: HomeContract.Intent) {
        when (intent) {
            is HomeContract.Intent.Delete -> repository.delete(intent.contact)

            is HomeContract.Intent.OpenEditContact -> {
                viewModelScope.launch { direction.navigateToAddEditScreen(data = intent.updateData) }
            }

            is HomeContract.Intent.OpenAddContact -> {
                viewModelScope.launch { direction.navigateToAddEditScreen(data = null) }
            }
        }
    }
}