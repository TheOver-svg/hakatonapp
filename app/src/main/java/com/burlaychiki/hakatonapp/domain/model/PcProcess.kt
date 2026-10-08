package com.burlaychiki.hakatonapp.domain.model

data class PcProcess(
    val pid: String,
    val name: String,
    val cpuPercent: Float,
    val memoryMb: Int
)