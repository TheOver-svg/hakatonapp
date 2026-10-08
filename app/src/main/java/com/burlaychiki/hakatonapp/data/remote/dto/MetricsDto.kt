package com.burlaychiki.hakatonapp.data.remote.dto

data class MetricsDto(
    val cpuLoad: Float = 0f,
    val ramLoad: Float = 0f,
    val totalRam: Double = 0.0,
    val gpuLoad: Float = 0f,
    val systemProcesses: List<ProcessDto>? = null
)