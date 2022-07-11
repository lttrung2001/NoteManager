package com.pnam.note.di

import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.data.networks.impl.LoginRetrofitServiceImpl
import com.pnam.note.database.repositories.LoginRepositories
import com.pnam.note.database.repositories.impl.LoginRepositoriesImpl
import com.pnam.note.ui.login.LoginUseCase
import com.pnam.note.ui.login.LoginUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModules {
    // Datastore current user

    // Database
    @Binds
    abstract fun getLoginNetworks(networks: LoginRetrofitServiceImpl): LoginNetworks

    // Repositories
    @Binds
    abstract fun getLoginRepository(repository: LoginRepositoriesImpl): LoginRepositories

    // Usecase
    @Binds
    abstract fun getLoginUseCase(useCase: LoginUseCaseImpl): LoginUseCase
}