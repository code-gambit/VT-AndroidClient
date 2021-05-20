package com.github.code.gambit.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.code.gambit.data.model.File
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.repositories.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState

    fun setEvent(event: HomeEvent) {
        _homeState.postValue(HomeState.Loading)
        viewModelScope.launch {
            when (event) {
                HomeEvent.GetFiles -> getFile()
            }
        }
    }

    private suspend fun getFile() {
        homeRepository.getFiles().collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> {
                    _homeState.postValue(HomeState.FilesLoaded(it.data))
                }
            }
        }
    }

    private fun postError(exception: Exception) {
        _homeState.postValue(HomeState.Error(exception.message!!))
    }
}
sealed class HomeEvent {
    object GetFiles : HomeEvent()
}
sealed class HomeState {
    object Loading : HomeState()
    data class FilesLoaded(val files: List<File>) : HomeState()
    data class Error(val message: String) : HomeState()
}
