package com.burlaychiki.hakatonapp.data.remote.interceptor

import com.burlaychiki.hakatonapp.data.local.ConnectionProvider
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val provider: ConnectionProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = provider.current?.token
        val request = if (token != null) {
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else chain.request()
        return chain.proceed(request)
    }
}