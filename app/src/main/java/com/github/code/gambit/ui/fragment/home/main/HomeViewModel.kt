package com.github.code.gambit.ui.fragment.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.repositories.home.HomeRepository
import com.github.code.gambit.ui.fragment.home.filtercomponent.Filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(private val homeRepository: HomeRepository) : ViewModel() {

    private val _homeState = MutableLiveData<HomeState>()
    val homeState: LiveData<HomeState> get() = _homeState

    fun setEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.GetFiles -> {
                    _homeState.postValue(HomeState.Loading())
                    getFile(event.clearCache)
                }
                is HomeEvent.GenerateUrl -> {
                    _homeState.postValue(HomeState.Loading())
                    generateUrl(event.file)
                }
                is HomeEvent.GetUrls -> {
                    _homeState.postValue(HomeState.LoadingUrl)
                    getUrls(event.file.id)
                }
                is HomeEvent.SearchFile -> {
                    _homeState.postValue(HomeState.Loading(true))
                    searchFile(event.searchString)
                }
                is HomeEvent.FilterFiles -> {
                    _homeState.postValue(HomeState.Loading())
                    filterFiles(event.filter)
                }
                is HomeEvent.DeleteFile -> {
                    _homeState.postValue(HomeState.Loading())
                    deleteFile(event.file)
                }
                is HomeEvent.UpdateUrl -> {
                    if (event.delete) {
                        updateUrl(event.updatedUrl, true)
                    } else {
                        updateUrl(event.updatedUrl)
                    }
                }
            }
        }
    }

    private suspend fun getFile(clearCache: Boolean) {
        homeRepository.getFiles(clearCache).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> {
                    _homeState.postValue(HomeState.FilesLoaded(it.data))
                }
            }
        }
    }

    private suspend fun deleteFile(file: File) {
        homeRepository.deleteFile(file).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> _homeState.postValue(HomeState.FileDeleted(file))
            }
        }
    }

    private suspend fun filterFiles(filter: Filter) {
        homeRepository.searchFileByFilter(filter).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> {
                    _homeState.postValue(HomeState.FilterResult(it.data, filter))
                }
            }
        }
    }

    private suspend fun getUrls(fileId: String) {
        homeRepository.getUrls(fileId).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> _homeState.postValue(HomeState.UrlsLoaded(it.data))
            }
        }
    }

    private suspend fun generateUrl(file: File) {
        homeRepository.generateUrl(file).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> _homeState.postValue(HomeState.UrlGenerated(it.data))
            }
        }
    }

    private suspend fun updateUrl(url: Url, delete: Boolean = false) {
        if (delete) {
            homeRepository.deleteUrl(url).collect {
                when (it) {
                    is ServiceResult.Error -> {
                        _homeState.postValue(
                            HomeState.UrlUpdateResult(false, null, true)
                        )
                        Timber.tag("error").i(it.exception)
                        postError(it.exception)
                    }
                    is ServiceResult.Success -> _homeState.postValue(
                        HomeState.UrlUpdateResult(true, it.data, true)
                    )
                }
            }
            return
        }
        homeRepository.updateUrl(url).collect {
            when (it) {
                is ServiceResult.Error -> {
                    _homeState.postValue(HomeState.UrlUpdateResult(false, null))
                    Timber.tag("error").i(it.exception)
                    postError(it.exception)
                }
                is ServiceResult.Success -> _homeState.postValue(
                    HomeState.UrlUpdateResult(true, it.data)
                )
            }
        }
    }

    private suspend fun searchFile(searchString: String) {
        homeRepository.searchFile(searchString).collect {
            when (it) {
                is ServiceResult.Error -> postError(it.exception)
                is ServiceResult.Success -> _homeState.postValue(
                    HomeState.FilesLoaded(it.data, true)
                )
            }
        }
    }

    private fun postError(exception: Exception) {
        _homeState.postValue(HomeState.Error(exception.message!!))
    }
}

sealed class HomeEvent {
    data class GetFiles(val clearCache: Boolean = false) : HomeEvent()
    data class FilterFiles(val filter: Filter) : HomeEvent()
    data class DeleteFile(val file: File) : HomeEvent()
    data class GetUrls(val file: File) : HomeEvent()
    data class GenerateUrl(val file: File) : HomeEvent()
    data class UpdateUrl(val updatedUrl: Url, val delete: Boolean = false) : HomeEvent()
    data class SearchFile(val searchString: String) : HomeEvent()
}

sealed class HomeState {
    object LoadingUrl : HomeState()
    data class Loading(val isSearchResultLoading: Boolean = false) : HomeState()
    data class FilesLoaded(val files: List<File>, val isSearchResult: Boolean = false) : HomeState()
    data class FileDeleted(val file: File) : HomeState()
    data class FilterResult(val files: List<File>, val filter: Filter) : HomeState()
    data class UrlsLoaded(val urls: List<Url>) : HomeState()
    data class UrlGenerated(val url: Url) : HomeState()
    data class UrlUpdateResult(
        val success: Boolean,
        val updatedUrl: Url?,
        val delete: Boolean = false
    ) : HomeState()

    data class Error(val message: String) : HomeState()
}
