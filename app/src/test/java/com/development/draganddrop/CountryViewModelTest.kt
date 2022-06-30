package com.development.draganddrop

import androidx.lifecycle.Observer
import com.development.draganddrop.data.CountriesRepository
import com.development.draganddrop.presentation.CountriesViewModel
import com.development.draganddrop.presentation.CountryListUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class CountryViewModelTest {

    private val mockRepository: CountriesRepository = mock()
    @OptIn(ExperimentalCoroutinesApi::class)
    val dispatcher = StandardTestDispatcher()
    lateinit var viewModel: CountriesViewModel
    val viewStateObserver: Observer<CountryListUIState> = mock()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        viewModel = CountriesViewModel(mockRepository)


    }

}