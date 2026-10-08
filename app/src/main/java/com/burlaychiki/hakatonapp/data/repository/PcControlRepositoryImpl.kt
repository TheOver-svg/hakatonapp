package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.data.remote.socket.HubClient
import com.burlaychiki.hakatonapp.domain.model.CommandResult
import com.burlaychiki.hakatonapp.domain.repository.PcControlRepository
import com.burlaychiki.hakatonapp.util.toUserMessage
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PcControlRepositoryImpl @Inject constructor(
    private val hub: HubClient
) : PcControlRepository {

    override suspend fun shutdown(delaySeconds: Int): CommandResult =
        execute("Команду вимкнення надіслано") {
            val time = if (delaySeconds > 0) delaySeconds.toString() else ""
            hub.command("TurnOfPc", time)
        }

    override suspend fun cancelShutdown(): CommandResult =
        execute("Скасування надіслано") { hub.command("CancelShutdown") }

    override suspend fun openFile(path: String): CommandResult =
        execute("Команду відкриття надіслано") { hub.command("OpenFile", path) }

    private suspend fun execute(okMessage: String, block: suspend () -> Unit): CommandResult =
        try {
            block()
            CommandResult(success = true, message = okMessage)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            CommandResult(success = false, message = e.toUserMessage())
        }
}