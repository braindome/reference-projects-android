package com.example.placesapi.localdb

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NearbyPlaceViewModel(
    private val dao: NearbyPlaceDao
): ViewModel() {
    private val _sortType = MutableStateFlow(SortType.NAME)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _nearbyPlaces = _sortType
        .flatMapLatest { sortType ->
            when (sortType) {
                SortType.NAME -> dao.getNearbyPlacesOrderedByName()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(NearbyPlaceState())

    val state = combine(_state, _sortType, _nearbyPlaces) { state, sortType, nearbyPlaces ->
        state.copy(
            nearbyPlaces = nearbyPlaces,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NearbyPlaceState())

    /*
    fun saveNearbyPlaces() {
        val name = state.value.name
        val latLng = state.value.latLng

        if(name.isBlank() || latLng.isBlank() ) {
            return
        }

        val nearbyPlace = NearbyPlace(
            name = name,
            latLng = latLng
        )

        viewModelScope.launch {
            dao.upsertNearbyPlace(nearbyPlace)
        }
    }

     */



    fun saveNearbyPlaces(nearbyPlaceList: List<NearbyPlace>) {
        viewModelScope.launch {
            for (nearbyPlace in nearbyPlaceList) {
                dao.upsertNearbyPlace(nearbyPlace)
            }
        }
    }
}