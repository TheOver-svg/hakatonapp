package com.burlaychiki.hakatonapp.di

import com.burlaychiki.hakatonapp.data.repository.FakeMetricsRepository
import com.burlaychiki.hakatonapp.data.repository.FakePcControlRepository
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
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
    abstract fun bindMetricsRepository(impl: FakeMetricsRepository): MetricsRepository

    @Binds
    @Singleton
    abstract fun bindPcControlRepository(impl: FakePcControlRepository): PcControlRepository
}