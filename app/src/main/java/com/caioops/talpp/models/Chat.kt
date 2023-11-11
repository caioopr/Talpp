package com.caioops.talpp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Chat(
    val senderUserId: String = "",
    val receiverUserId: String = "",
    val name: String = "",
    val photo: String = "",
    val lastMessage: String = "",
    @ServerTimestamp
    val date: Date ?= null,
)
