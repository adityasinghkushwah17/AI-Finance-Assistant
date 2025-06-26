package com.adityacodes.financebuddy.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Loading)
    val authState: State<AuthState> = _authState

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    init {
        checkAuthState()
    }

    val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun checkAuthState() {
        _authState.value = if (auth.currentUser != null) {
            AuthState.Authenticated
        } else {
            AuthState.Unauthenticated
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = task.exception?.message
                }
            }
    }

    fun signUp(email: String, password: String) {
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Unauthenticated
                    _errorMessage.value = task.exception?.message
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun clearError() {
        _errorMessage.value = null
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}