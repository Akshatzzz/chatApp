package com.akshat.chatapp.model

data class SignInState(
    val isSignSuccessful: Boolean = false,
    val signInError: String? = null
)
