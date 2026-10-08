package com.burlaychiki.hakatonapp.di

import com.burlaychiki.hakatonapp.data.repository.MetricsRepositoryImpl
import com.burlaychiki.hakatonapp.data.repository.PairingRepositoryImpl
import com.burlaychiki.hakatonapp.data.repository.PcControlRepositoryImpl
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import com.burlaychiki.hakatonapp.domain.repository.PcControlRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMetricsRepository(impl: MetricsRepositoryImpl): MetricsRepository

    @Binds
    @Singleton
    abstract fun bindPcControlRepository(impl: PcControlRepositoryImpl): PcControlRepository

    @Binds
    @Singleton
    abstract fun bindPairingRepository(impl: PairingRepositoryImpl): PairingRepository
}