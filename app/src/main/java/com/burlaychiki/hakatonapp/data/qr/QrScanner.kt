package com.burlaychiki.hakatonapp.data.qr

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

sealed interface QrScanResult {
    data class Success(val rawValue: String) : QrScanResult
    data object Cancelled : QrScanResult
    data class Error(val message: String) : QrScanResult
}

class QrScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()

    suspend fun scan(): QrScanResult = suspendCancellableCoroutine { cont ->
        val scanner = GmsBarcodeScanning.getClient(context, options)
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val value = barcode.rawValue
                cont.resume(
                    if (value != null) QrScanResult.Success(value)
                    else QrScanResult.Error("Порожній QR")
                )
            }
            .addOnCanceledListener { cont.resume(QrScanResult.Cancelled) }
            .addOnFailureListener {
                cont.resume(QrScanResult.Error(it.message ?: "Помилка сканування"))
            }
    }
}