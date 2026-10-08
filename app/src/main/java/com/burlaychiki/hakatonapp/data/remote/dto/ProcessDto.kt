package com.burlaychiki.hakatonapp.data.remote.dto

data class ProcessDto(
    val id: String? = null,
    val name: String? = null,
    val cpuUsage: Float = 0f,
    val memoryUsage: Float = 0f
)