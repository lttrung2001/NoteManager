package com.pnam.note.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pnam.note.database.data.locals.AppDatabase
import com.pnam.note.database.data.locals.ImageLocals
import com.pnam.note.database.data.locals.LoginLocals
import com.pnam.note.database.data.locals.NoteLocals
import com.pnam.note.database.data.locals.dao.LoginDao
import com.pnam.note.database.data.locals.dao.NoteDao
import com.pnam.note.database.data.locals.impl.ImageLocalsImpl
import com.pnam.note.database.data.networks.impl.BaseAuthorizationInterceptor
import com.pnam.note.database.data.networks.impl.LoginRetrofitServiceImpl
import com.pnam.note.database.data.networks.impl.NetworkConnectionInterceptor
import com.pnam.note.database.data.networks.impl.NoteRetrofitServiceImpl
import com.pnam.note.utils.AppConstants.APP_NAME
import com.pnam.note.utils.RetrofitUtils.BASE_URL
import com.pnam.note.utils.RoomUtils.Companion.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppProvideModules {
    // Room
    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .build()

    @Provides
    @Singleton
    fun provideLoginDao(database: AppDatabase): LoginDao {
        return database.loginDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideLoginLocals(appDatabase: AppDatabase): LoginLocals {
        return appDatabase.loginDao()
    }

    @Provides
    @Singleton
    fun provideNoteLocals(appDatabase: AppDatabase): NoteLocals {
        return appDatabase.noteDao()
    }

    @Provides
    @Singleton
    fun provideImageLocals(impl: ImageLocalsImpl): ImageLocals {
        return impl
    }

    // Retrofit
    @Provides
    @Singleton
    fun providerOkHttp(
        authorizationInterceptor: BaseAuthorizationInterceptor.AuthorizationInterceptor,
        interceptor: NetworkConnectionInterceptor,
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authorizationInterceptor)
        .addInterceptor(logging)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(interceptor).build()

    @Provides
    @Singleton
    fun providerRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory,
        okHttp: OkHttpClient
    ): Retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(rxJava3CallAdapterFactory)
        .client(okHttp).build()

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder().setDateFormat("dd/MM/yyyy HH:mm:ss").create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Provides
    @Singleton
    // Đọc tài liệu
    fun provideRxJava3CallAdapterFactory(): RxJava3CallAdapterFactory =
        RxJava3CallAdapterFactory.create()

    @Provides
    @Singleton
    // Đọc tài liệu
    fun provideLogging(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    // Provide OkHttp here..
    // Service
    @Provides
    @Singleton
    fun provideLoginService(retrofit: Retrofit): LoginRetrofitServiceImpl.Service =
        retrofit.create(LoginRetrofitServiceImpl.Service::class.java)

    @Provides
    @Singleton
    fun provideNoteService(retrofit: Retrofit): NoteRetrofitServiceImpl.Service =
        retrofit.create(NoteRetrofitServiceImpl.Service::class.java)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    }
}