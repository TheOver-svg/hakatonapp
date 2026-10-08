package com.burlaychiki.hakatonapp.domain.repository

import com.burlaychiki.hakatonapp.domain.model.CommandResult

interface PcControlRepository {
    suspend fun shutdown(delaySeconds: Int): CommandResult
    suspend fun cancelShutdown(): CommandResult
    suspend fun openFile(path: String): CommandResult
    suspend fun killProcess(processId: String): CommandResult
}