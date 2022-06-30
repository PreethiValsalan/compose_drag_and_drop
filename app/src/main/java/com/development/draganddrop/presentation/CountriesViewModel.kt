package com.development.draganddrop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.development.draganddrop.data.CountriesRepository
import com.development.draganddrop.domain.Country
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val repository: CountriesRepository
): ViewModel() {

   private val _countryListUIState =  MutableStateFlow<CountryListUIState>(CountryListUIState.Loading)
   val countryListUIState: StateFlow<CountryListUIState> = _countryListUIState


    fun loadCountries() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
            _countryListUIState.value = CountryListUIState.Error(exception)
        }
        viewModelScope.launch(exceptionHandler) {
            repository.loadCountries().collect {
                _countryListUIState.value = when(it.isEmpty()) {
                    true -> CountryListUIState.Empty
                    false -> CountryListUIState.Loaded(it)
                }
            }
        }
    }

    fun onMove(from: Int, to: Int) {
        val minValue = min(from, to)
        val maxValue = max(from, to)
        val countries = (_countryListUIState.value as CountryListUIState.Loaded).countries
        _countryListUIState.value = CountryListUIState.Loaded(countries.map {
            when {
                it.index < minValue || it.index > maxValue -> it.copy()
                it.index == from -> it.copy(index = to)
                else -> when {
                    from > to -> it.copy(index = it.index + 1)
                    else -> it.copy(index = it.index - 1)
                }
            }
        }.sortedBy { it.index })
    }
}

sealed class CountryListUIState {

    object Empty : CountryListUIState()
    object Loading : CountryListUIState()
    data class Error(val ex: Throwable) : CountryListUIState()
    data class Loaded(val countries: List<Country>) : CountryListUIState()
}