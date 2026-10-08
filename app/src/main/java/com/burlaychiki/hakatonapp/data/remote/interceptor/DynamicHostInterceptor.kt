package com.burlaychiki.hakatonapp.data.remote.interceptor

import com.burlaychiki.hakatonapp.data.local.ConnectionProvider
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class DynamicHostInterceptor @Inject constructor(
    private val provider: ConnectionProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val connection = provider.current ?: throw IOException("ПК не підключено")

        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .scheme("http")
            .host(connection.host)
            .port(connection.port)
            .build()

        return chain.proceed(request.newBuilder().url(newUrl).build())
    }
}