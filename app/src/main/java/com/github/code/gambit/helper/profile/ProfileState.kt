package com.github.code.gambit.helper.profile

import com.github.code.gambit.data.model.User

sealed class ProfileState {
    object Loading : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class ProfileLoaded(val user: User) : ProfileState()
    object PasswordUpdated : ProfileState()
    data class UserNameUpdated(val name: String) : ProfileState()
    object ProfileUpdateFailed : ProfileState()
}
