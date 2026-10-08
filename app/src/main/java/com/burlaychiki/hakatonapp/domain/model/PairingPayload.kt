package com.burlaychiki.hakatonapp.domain.model

data class PairingPayload(
    val host: String,
    val port: Int,
    val pairingCode: String,
    val pcName: String
)