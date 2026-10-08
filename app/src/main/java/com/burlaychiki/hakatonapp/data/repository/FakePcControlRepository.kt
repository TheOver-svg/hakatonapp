package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.domain.model.CommandResult
import com.burlaychiki.hakatonapp.domain.repository.PcControlRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakePcControlRepository @Inject constructor() : PcControlRepository {

    override suspend fun shutdown(delaySeconds: Int): CommandResult {
        delay(600)
        val text = if (delaySeconds == 0) "негайно" else "через ${delaySeconds / 60} хв"
        return CommandResult(success = true, message = "ПК буде вимкнено $text")
    }

    override suspend fun cancelShutdown(): CommandResult {
        delay(600)
        return CommandResult(success = true, message = "Вимкнення скасовано")
    }

    override suspend fun openFile(path: String): CommandResult {
        delay(600)
        return CommandResult(success = true, message = "Відкриваю: $path")
    }
}