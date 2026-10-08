package com.burlaychiki.hakatonapp.data.remote.dto

data class ProcessDto(
    val pid: Int = 0,
    val name: String? = null,
    val cpuPercent: Float = 0f,
    val memoryMb: Int = 0
)