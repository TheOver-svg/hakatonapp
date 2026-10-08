package com.burlaychiki.hakatonapp.data.qr

import com.burlaychiki.hakatonapp.domain.model.PairingPayload
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Serializable
private data class QrPayloadDto(
    val host: String,
    val port: Int,
    val pairingCode: String,
    val pcName: String = "PC"
)

class QrPayloadParser @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    fun parse(raw: String): PairingPayload? = runCatching {
        val dto = json.decodeFromString<QrPayloadDto>(raw)
        require(dto.host.isNotBlank() && dto.port in 1..65535 && dto.pairingCode.isNotBlank())
        PairingPayload(dto.host, dto.port, dto.pairingCode, dto.pcName)
    }.getOrNull()
}