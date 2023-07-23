package com.akshat.chatapp

import androidx.lifecycle.ViewModel
import com.akshat.chatapp.model.SignInState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInRequest) {
        _state.update {
            it.copy(
                isSignSuccessful = result.data != null, signInError = result.errorMessage
            )
        }
    }
    fun resetState() {
        _state.update { SignInState() }
    }
}