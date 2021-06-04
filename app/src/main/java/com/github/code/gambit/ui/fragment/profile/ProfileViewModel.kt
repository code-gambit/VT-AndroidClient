package com.github.code.gambit.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.profile.ProfileState
import com.github.code.gambit.repositories.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableLiveData<ProfileState>()

    val profileState: LiveData<ProfileState> get() = _profileState

    fun setEvent(event: ProfileEvent) {
        viewModelScope.launch {
            when (event) {
                ProfileEvent.LogOut -> {
                    when (val it = profileRepository.logOut()) {
                        is ServiceResult.Error -> postError(it.exception)
                        is ServiceResult.Success -> postError(Exception("LOG OUT SUCCESS"))
                    }
                }
                ProfileEvent.GetUserInfoEvent -> {
                    _profileState.postValue(ProfileState.Loading)
                    profileRepository.getUser().onEach {
                        when (it) {
                            is ServiceResult.Error -> postError(it.exception)
                            is ServiceResult.Success -> {
                                _profileState.value = ProfileState.ProfileLoaded(it.data)
                            }
                        }
                    }.launchIn(viewModelScope)
                }
                is ProfileEvent.UpdateDisplayNameEvent -> {
                    _profileState.postValue(ProfileState.Loading)
                    when (val res = profileRepository.updateUserName(event.name)) {
                        is ServiceResult.Error -> postError(res.exception)
                        is ServiceResult.Success -> _profileState.postValue(ProfileState.UserNameUpdated(res.data))
                    }
                }
                is ProfileEvent.UpdatePasswordEvent -> {
                    _profileState.postValue(ProfileState.Loading)
                    when (val res = profileRepository.updateUserPassword(event.oldPassword, event.newPassword)) {
                        is ServiceResult.Error -> postError(res.exception)
                        is ServiceResult.Success -> {
                            if (res.data) {
                                _profileState.postValue(ProfileState.PasswordUpdated)
                            } else {
                                postError(Exception("Invalid old password!!"))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun postError(exception: Exception) {
        _profileState.postValue(ProfileState.Error(exception.message!!))
    }
}

sealed class ProfileEvent {
    object LogOut : ProfileEvent()
    object GetUserInfoEvent : ProfileEvent()
    data class UpdatePasswordEvent(val oldPassword: String, val newPassword: String) : ProfileEvent()
    data class UpdateDisplayNameEvent(val name: String) : ProfileEvent()
}
