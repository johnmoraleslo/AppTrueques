package com.johnmoraleslo.apptrueques.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johnmoraleslo.apptrueques.data.model.Item
import com.johnmoraleslo.apptrueques.data.repository.ItemRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface ExploreUiState {
    object Loading : ExploreUiState
    data class Success(val items: List<Item>) : ExploreUiState
    data class Error(val message: String) : ExploreUiState
}

@OptIn(FlowPreview::class)
class ExploreViewModel : ViewModel() {
    private val repository = ItemRepository()

    private val _uiState = MutableStateFlow<ExploreUiState>(ExploreUiState.Loading)
    val uiState: StateFlow<ExploreUiState> = _uiState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    // Filtro por estado: "Todos", "Nuevo" o "Usado"
    private val _conditionFilter = MutableStateFlow("Todos")
    val conditionFilter: StateFlow<String> = _conditionFilter

    init {
        // Espera 300 ms después de escribir para no llamar la API en cada letra
        _searchQuery
            .debounce(300L)
            .distinctUntilChanged()
            .onEach { query -> fetchItems(query) }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onConditionFilterChange(filter: String) {
        _conditionFilter.value = filter
    }

    fun retry() {
        fetchItems(_searchQuery.value)
    }

    private fun fetchItems(query: String) {
        viewModelScope.launch {
            _uiState.value = ExploreUiState.Loading
            repository.getItems(query.ifBlank { null })
                .onSuccess { list ->
                    _uiState.value = ExploreUiState.Success(list)
                }
                .onFailure {
                    _uiState.value = ExploreUiState.Error("No se pudieron cargar los artículos. Revisa tu conexión.")
                }
        }
    }
}
