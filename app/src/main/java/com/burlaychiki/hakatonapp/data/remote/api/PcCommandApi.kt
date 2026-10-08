package com.burlaychiki.hakatonapp.data.remote.api

import com.burlaychiki.hakatonapp.data.remote.dto.PairRequestDto
import com.burlaychiki.hakatonapp.data.remote.dto.PairResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface PcCommandApi {
    @POST("api/pair")
    suspend fun pair(@Body request: PairRequestDto): PairResponseDto
}