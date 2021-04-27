package com.github.code.gambit.network.auth

import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.cognito.AWSCognitoUserPoolTokens
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.kotlin.core.Amplify
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthResult
import com.github.code.gambit.utility.defaultBuilder

class AuthService_Impl : AuthService {

    override suspend fun login(authData: AuthData): AuthResult<Unit> {
        return try {
            val result = Amplify.Auth.signIn(authData.email, authData.password)
            if (result.isSignInComplete) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("Login incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }

    override suspend fun signUp(authData: AuthData): AuthResult<Unit> {
        val options = AuthSignUpOptions.builder().defaultBuilder(authData)
        return try {
            val result = Amplify.Auth.signUp(authData.email, authData.password, options)
            if (result.isSignUpComplete) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("SignUp incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }

    override suspend fun confirmSignUp(authData: AuthData): AuthResult<Unit> {
        return try {
            val result = Amplify.Auth.confirmSignUp(authData.email, authData.confirmationCode!!)
            if (result.isSignUpComplete) {
                AuthResult.Success(Unit)
            } else {
                AuthResult.Error(Exception("Confirmation incomplete + ${result.nextStep}"))
            }
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }

    override suspend fun fetchSession(): AuthResult<AWSCognitoAuthSession> {
        return try {
            val authSession = Amplify.Auth.fetchAuthSession() as AWSCognitoAuthSession
            AuthResult.Success(authSession)
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }

    override suspend fun fetchIdToken(): AuthResult<String> {
        when (val session = fetchSession()) {
            is AuthResult.Error -> {
                return AuthResult.Error(session.exception)
            }
            is AuthResult.Success<AWSCognitoAuthSession> -> {
                val tokens: AWSCognitoUserPoolTokens = session.data.userPoolTokens.value
                    ?: return AuthResult.Error(Exception("token not generated, user might be not authentication"))
                return AuthResult.Success(tokens.idToken)
            }
            else -> {
                return AuthResult.Error(Exception("Illegal state!!"))
            }
        }
    }

    override suspend fun fetchUserAttribute(): AuthResult<List<AuthUserAttribute>> {
        return try {
            val attributes = Amplify.Auth.fetchUserAttributes()
            AuthResult.Success(attributes)
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }

    override suspend fun resentConfirmationCode(email: String): AuthResult<Unit> {
        return try {
            Amplify.Auth.resendSignUpCode(email)
            AuthResult.Success(Unit)
        } catch (e: AuthException) {
            AuthResult.Error(e)
        }
    }
}
