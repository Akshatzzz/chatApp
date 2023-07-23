package com.akshat.chatapp

data class SignInRequest(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePicture: String?
)
