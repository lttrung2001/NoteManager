package com.pnam.note.di

import com.pnam.note.database.data.networks.BaseAuthorizationInterceptor
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.data.networks.impl.LoginRetrofitServiceImpl
import com.pnam.note.database.data.networks.impl.NoteRetrofitServiceImpl
import com.pnam.note.database.repositories.LoginRepositories
import com.pnam.note.database.repositories.NoteRepositories
import com.pnam.note.database.repositories.impl.LoginRepositoriesImpl
import com.pnam.note.database.repositories.impl.NoteRepositoriesImpl
import com.pnam.note.ui.dashboard.DashboardUseCase
import com.pnam.note.ui.dashboard.DashboardUseCaseImpl
import com.pnam.note.ui.login.LoginUseCase
import com.pnam.note.ui.login.LoginUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.jetbrains.annotations.NotNull

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModules {

    // Datastore current user

    // Database
    @Binds
    abstract fun getLoginNetworks(networks: LoginRetrofitServiceImpl): LoginNetworks

    @Binds
    abstract fun getNoteNetworks(networks: NoteRetrofitServiceImpl): NoteNetworks

    // Repositories
    @Binds
    abstract fun getLoginRepository(repository: LoginRepositoriesImpl): LoginRepositories

    @Binds
    abstract fun getNoteRepository(repository: NoteRepositoriesImpl): NoteRepositories

    // Usecase
    @Binds
    abstract fun getLoginUseCase(useCase: LoginUseCaseImpl): LoginUseCase

    @Binds
    abstract fun getDashboardUseCase(useCase: DashboardUseCaseImpl): DashboardUseCase
}