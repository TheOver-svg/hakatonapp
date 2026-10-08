package com.burlaychiki.hakatonapp.data.qr

import android.net.Uri
import com.burlaychiki.hakatonapp.domain.model.PairingPayload
import org.json.JSONObject
import javax.inject.Inject

class QrPayloadParser @Inject constructor() {

    fun parse(raw: String): PairingPayload? {
        val text = raw.trim()
        val id = when {
            text.startsWith("{") -> fromJson(text)
            text.startsWith("http://") || text.startsWith("https://") -> fromUrl(text)
            else -> text
        }
        return id?.takeIf(::isValid)?.let(::PairingPayload)
    }

    private fun fromJson(text: String): String? = runCatching {
        val json = JSONObject(text)
        json.optString("sessionId").ifBlank { json.optString("id") }
    }.getOrNull()

    private fun fromUrl(text: String): String? {
        val uri = Uri.parse(text)
        val fromQuery = uri.getQueryParameter("sessionId") ?: uri.getQueryParameter("id")
        if (!fromQuery.isNullOrBlank()) return fromQuery

        val last = uri.lastPathSegment
        return last?.takeUnless { it.equals("JoinSession", ignoreCase = true) }
    }

    private fun isValid(id: String): Boolean =
        id.isNotEmpty() && id.length <= 128 && id.none { it.isWhitespace() }
}