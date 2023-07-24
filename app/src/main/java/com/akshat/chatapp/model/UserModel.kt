package com.akshat.chatapp.model

import java.security.acl.LastOwnerException

data class UserModel(
    val name: String?,
    val email: String?,
    val userId: String?,
    val profilePic: String? = null,
    val lastMessage: String?
) {
    constructor(name: String?, email: String?, lastMessage: String?, userId: String?) : this(
        name,
        email,
        userId,
        null,
        lastMessage
    )
}