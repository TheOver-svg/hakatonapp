package com.burlaychiki.hakatonapp.domain.model

data class PcMetrics(
    val cpu: Float,
    val gpu: Float,
    val ram: Float,
    val totalRamMb: Float = 0f
)