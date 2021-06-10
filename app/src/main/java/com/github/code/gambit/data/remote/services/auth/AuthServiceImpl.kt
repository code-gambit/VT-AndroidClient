package com.github.code.gambit.data.remote.services.auth

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.kotlin.core.Amplify
import com.github.code.gambit.data.remote.interceptor.AuthInterceptor
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.extention.defaultBuilder

class AuthServiceImpl(private val authInterceptor: AuthInterceptor) : AuthService {

    override suspend fun login(authData: AuthData): ServiceResult<Unit> {
        return authInterceptor.authRequest({
            Amplify.Auth.signIn(
                authData.email,
                authData.password
            )
        }) { result ->
            if (result.isSignInComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("Login incomplete + ${result.nextStep}"))
            }
        }
    }

    override suspend fun signUp(authData: AuthData): ServiceResult<Unit> {
        return authInterceptor.authRequest({
            val options = AuthSignUpOptions.builder().defaultBuilder(authData)
            Amplify.Auth.signUp(authData.email, authData.password, options)
        }) { result ->
            if (result.isSignUpComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("SignUp incomplete + ${result.nextStep}"))
            }
        }
    }

    override suspend fun logOut(): ServiceResult<Unit> {
        return authInterceptor.authRequest({ Amplify.Auth.signOut() }) { ServiceResult.Success(Unit) }
    }

    override suspend fun resetPassword(
        oldPassword: String,
        newPassword: String
    ): ServiceResult<Unit> {
        return authInterceptor.authRequest({
            Amplify.Auth.updatePassword(
                oldPassword,
                newPassword
            )
        }) {
            ServiceResult.Success(it)
        }
    }

    override suspend fun forgotPassword(userEmail: String): ServiceResult<Unit> {
        return authInterceptor.authRequest({ Amplify.Auth.resetPassword(userEmail) }) {
            ServiceResult.Success(Unit)
        }
    }

    override suspend fun changePassword(
        newPassword: String,
        confirmationCode: String
    ): ServiceResult<Unit> {
        return authInterceptor.authRequest({
            Amplify.Auth.confirmResetPassword(
                newPassword,
                confirmationCode
            )
        }) {
            ServiceResult.Success(Unit)
        }
    }

    override suspend fun updateUserName(fullName: String): ServiceResult<String> {
        return authInterceptor.authRequest({
            val attribute = AuthUserAttribute(AuthUserAttributeKey.name(), fullName)
            Amplify.Auth.updateUserAttribute(attribute)
        }) { result ->
            if (result.isUpdated) {
                ServiceResult.Success(fullName)
            } else {
                ServiceResult.Error(
                    AuthException(
                        "User name update failed",
                        "Switch your internet connection"
                    )
                )
            }
        }
    }

    override suspend fun confirmSignUp(authData: AuthData): ServiceResult<Unit> {
        return authInterceptor.authRequest({
            Amplify.Auth.confirmSignUp(
                authData.email,
                authData.confirmationCode!!
            )
        }) { result ->
            if (result.isSignUpComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("Confirmation incomplete + ${result.nextStep}"))
            }
        }
    }

    override suspend fun fetchSession(): ServiceResult<AWSCognitoAuthSession> {
        return authInterceptor.authRequest({ Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession }) {
            ServiceResult.Success(
                it
            )
        }
    }

    override suspend fun fetchIdToken(): ServiceResult<String> {
        return authInterceptor.authRequest({ fetchSession() }) { session ->
            when (session) {
                is ServiceResult.Error -> {
                    ServiceResult.Error(session.exception)
                }
                is ServiceResult.Success<AWSCognitoAuthSession> -> {
                    session.data.userPoolTokens.value?.let {
                        ServiceResult.Success(it.idToken)
                    }
                        ?: ServiceResult.Error(Exception("token not generated, user might be not authentication"))
                }
            }
        }
    }

    override suspend fun fetchUserAttribute(): ServiceResult<List<AuthUserAttribute>> {
        return authInterceptor.authRequest({ Amplify.Auth.fetchUserAttributes() }) {
            ServiceResult.Success(
                it
            )
        }
    }

    override suspend fun resentConfirmationCode(email: String): ServiceResult<Unit> {
        return authInterceptor.authRequest({ Amplify.Auth.resendSignUpCode(email) }) {
            ServiceResult.Success(
                Unit
            )
        }
    }
}
