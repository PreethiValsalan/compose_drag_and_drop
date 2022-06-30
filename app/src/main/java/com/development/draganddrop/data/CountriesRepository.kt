package com.development.draganddrop.data

import com.development.draganddrop.api.ServiceApi
import com.development.draganddrop.domain.Country
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CountriesRepository @Inject constructor(
    private val serviceApi: ServiceApi
) {

    suspend fun loadCountries() : Flow<List<Country>> = flow {
        emit(serviceApi.getCountries().mapIndexed { i, country ->
            country.apply { index = i }
        })
    }
}