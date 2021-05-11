package com.github.code.gambit.data.remote.services.auth

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.AWSCognitoUserPoolTokens
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.kotlin.core.Amplify
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.ServiceResult
import com.github.code.gambit.utility.extention.defaultBuilder

class AuthServiceImpl : AuthService {

    override suspend fun login(authData: AuthData): ServiceResult<Unit> {
        return try {
            val result = Amplify.Auth.signIn(authData.email, authData.password)
            if (result.isSignInComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("Login incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun signUp(authData: AuthData): ServiceResult<Unit> {
        val options = AuthSignUpOptions.builder().defaultBuilder(authData)
        return try {
            val result = Amplify.Auth.signUp(authData.email, authData.password, options)
            if (result.isSignUpComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("SignUp incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun confirmSignUp(authData: AuthData): ServiceResult<Unit> {
        return try {
            val result = Amplify.Auth.confirmSignUp(authData.email, authData.confirmationCode!!)
            if (result.isSignUpComplete) {
                ServiceResult.Success(Unit)
            } else {
                ServiceResult.Error(Exception("Confirmation incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun fetchSession(): ServiceResult<AWSCognitoAuthSession> {
        return try {
            val authSession = Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession
            ServiceResult.Success(authSession)
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun fetchIdToken(): ServiceResult<String> {
        when (val session = fetchSession()) {
            is ServiceResult.Error -> {
                return ServiceResult.Error(session.exception)
            }
            is ServiceResult.Success<AWSCognitoAuthSession> -> {
                val tokens: AWSCognitoUserPoolTokens = session.data.userPoolTokens.value
                    ?: return ServiceResult.Error(Exception("token not generated, user might be not authentication"))
                return ServiceResult.Success(tokens.idToken)
            }
            else -> {
                return ServiceResult.Error(Exception("Illegal state!!"))
            }
        }
    }

    override suspend fun fetchUserAttribute(): ServiceResult<List<AuthUserAttribute>> {
        return try {
            val attributes = Amplify.Auth.fetchUserAttributes()
            ServiceResult.Success(attributes)
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun resentConfirmationCode(email: String): ServiceResult<Unit> {
        return try {
            Amplify.Auth.resendSignUpCode(email)
            ServiceResult.Success(Unit)
        } catch (e: AuthException) {
            ServiceResult.Error(e)
        }
    }
}
