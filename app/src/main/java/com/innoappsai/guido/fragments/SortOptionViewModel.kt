package com.innoappsai.guido.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.innoappsai.guido.model.PlaceSortOption
import com.innoappsai.guido.model.SortType
import com.innoappsai.guido.model.placeSortOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SortOptionViewModel @Inject constructor() : ViewModel() {

    private val sortOptions = ArrayList(placeSortOptions)

    private var _sortOptionsState: MutableLiveData<List<PlaceSortOption>> = MutableLiveData()
    val sortOptionsState: LiveData<List<PlaceSortOption>> = _sortOptionsState

    private var _selectedSortOption: MutableSharedFlow<SortType> = MutableSharedFlow()
    val selectedSortOption: SharedFlow<SortType> = _selectedSortOption


    fun initAdapter(){
        viewModelScope.launch(Dispatchers.IO) {
            _sortOptionsState.postValue(sortOptions)
        }
    }

    fun onSortOptionSelected(optionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sortOptions.forEach {
                it.isChecked = it.name == optionName
            }

            _sortOptionsState.postValue(sortOptions)
        }

    }

    fun applySortOption() {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedSort = sortOptions.find { it.isChecked }?.type ?: SortType.DISTANCE
            sortOptions.forEach { it.isChecked = false }
            _selectedSortOption.emit(selectedSort)
        }

    }

}