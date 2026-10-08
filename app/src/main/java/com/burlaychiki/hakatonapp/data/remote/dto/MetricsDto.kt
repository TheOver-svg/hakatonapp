package com.burlaychiki.hakatonapp.data.remote.dto

data class MetricsDto(
    val cpuLoad: Float = 0f,
    val ramLoad: Float = 0f,
    val totalRam: Double = 0.0,

    // Бекенд додасть згодом, імена уточнимо
    val gpuLoad: Float = 0f,
    val processes: List<ProcessDto>? = null
)