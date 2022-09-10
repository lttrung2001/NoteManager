package com.pnam.note.di

import com.pnam.note.database.data.locals.ImageLocals
import com.pnam.note.database.data.locals.impl.ImageLocalsImpl
import com.pnam.note.database.data.networks.LoginNetworks
import com.pnam.note.database.data.networks.NoteNetworks
import com.pnam.note.database.data.networks.impl.LoginRetrofitServiceImpl
import com.pnam.note.database.data.networks.impl.NoteRetrofitServiceImpl
import com.pnam.note.database.repositories.ImageRepositories
import com.pnam.note.database.repositories.LoginRepositories
import com.pnam.note.database.repositories.NoteRepositories
import com.pnam.note.database.repositories.impl.ImageRepositoriesImpl
import com.pnam.note.database.repositories.impl.LoginRepositoriesImpl
import com.pnam.note.database.repositories.impl.NoteRepositoriesImpl
import com.pnam.note.ui.addnote.AddNoteUseCase
import com.pnam.note.ui.addnote.AddNoteUseCaseImpl
import com.pnam.note.ui.addnoteimages.AddNoteImagesUseCase
import com.pnam.note.ui.addnoteimages.AddNoteImagesUseCaseImpl
import com.pnam.note.ui.changepassword.ChangePasswordUseCase
import com.pnam.note.ui.changepassword.ChangePasswordUseCaseImpl
import com.pnam.note.ui.dashboard.DashboardUseCase
import com.pnam.note.ui.dashboard.DashboardUseCaseImpl
import com.pnam.note.ui.editnote.EditNoteUseCase
import com.pnam.note.ui.editnote.EditNoteUseCaseImpl
import com.pnam.note.ui.forgotpassword.ForgotPasswordUseCase
import com.pnam.note.ui.forgotpassword.ForgotPasswordUseCaseImpl
import com.pnam.note.ui.login.LoginUseCase
import com.pnam.note.ui.login.LoginUseCaseImpl
import com.pnam.note.ui.register.RegisterUseCase
import com.pnam.note.ui.register.RegisterUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModules {
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

    @Binds
    abstract fun getImageRepository(repository: ImageRepositoriesImpl): ImageRepositories

    // Usecase
    @Binds
    abstract fun getLoginUseCase(useCase: LoginUseCaseImpl): LoginUseCase

    @Binds
    abstract fun getRegisterUseCase(useCase: RegisterUseCaseImpl): RegisterUseCase

    @Binds
    abstract fun getChangePasswordUseCase(useCase: ChangePasswordUseCaseImpl): ChangePasswordUseCase

    @Binds
    abstract fun getDashboardUseCase(useCase: DashboardUseCaseImpl): DashboardUseCase

    @Binds
    abstract fun getAddNoteUseCase(useCase: AddNoteUseCaseImpl): AddNoteUseCase

    @Binds
    abstract fun getEditNoteUseCase(useCase: EditNoteUseCaseImpl): EditNoteUseCase

    @Binds
    abstract fun getForgotPasswordUseCase(useCase: ForgotPasswordUseCaseImpl): ForgotPasswordUseCase

    @Binds
    abstract fun getAddNoteImages(useCase: AddNoteImagesUseCaseImpl): AddNoteImagesUseCase
}