package com.appxtank.eatin.di.module

import android.content.Context
import com.appxtank.eatin.BuildConfig
import com.appxtank.eatin.MyApplication
import com.appxtank.eatin.data.remote.NetworkService
import com.appxtank.eatin.data.remote.Networking
import com.appxtank.eatin.di.ApplicationContext
import com.appxtank.eatin.di.DatabaseInfo

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: MyApplication) {

    @ApplicationContext
    @Provides
    fun provideContext(): Context = application

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @DatabaseInfo
    fun provideDatabaseName(): String = "dummy_db"

    @Provides
    @DatabaseInfo
    fun provideDatabaseVersion(): Int = 1

    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService =
            Networking.create(
                    BuildConfig.BASE_URL,
                    application.cacheDir,
                    10 * 1024 * 1024 // 10MB
            )
}
