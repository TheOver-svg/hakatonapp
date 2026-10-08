package com.burlaychiki.hakatonapp.util

import com.microsoft.signalr.HubException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

fun Throwable.toUserMessage(): String {
    var current: Throwable? = this
    while (current != null) {
        when (current) {
            is UnknownHostException, is SocketException ->
                return "Мережа недоступна. Перевірте інтернет і адресу сервера"
            is SocketTimeoutException, is TimeoutException ->
                return "Час очікування вийшов. Спробуйте ще раз"
            is HubException ->
                return "Сервер не підтримує цю команду або не зміг її виконати"
        }
        current = current.cause
    }
    val text = message
    val isUkrainian = text != null && text.any { it in 'А'..'я' || it in "іІїЇєЄґҐ" }
    return if (isUkrainian) text!! else "Не вдалося виконати операцію"
}