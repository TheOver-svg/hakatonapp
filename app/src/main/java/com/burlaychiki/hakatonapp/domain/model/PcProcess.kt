package com.burlaychiki.hakatonapp.domain.model

data class PcProcess(
    val pid: Int,
    val name: String,
    val cpuPercent: Float,
    val memoryMb: Int
)