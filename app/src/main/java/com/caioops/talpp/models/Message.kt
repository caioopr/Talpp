package com.caioops.talpp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Message(
    val userId: String ="",
    val message: String="",
    @ServerTimestamp
    val date: Date? = null
)
