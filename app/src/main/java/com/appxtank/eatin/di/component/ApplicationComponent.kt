package com.appxtank.eatin.di.component

import android.content.Context
import com.appxtank.eatin.MyApplication
import com.appxtank.eatin.data.local.DatabaseService
import com.appxtank.eatin.data.remote.NetworkService
import com.appxtank.eatin.di.ApplicationContext
import com.appxtank.eatin.di.module.ApplicationModule
import com.appxtank.eatin.utils.NetworkHelper

import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: MyApplication)

    @ApplicationContext
    fun getContext(): Context

    fun getCompositeDisposable(): CompositeDisposable

    fun getNetworkService(): NetworkService

    fun getDatabaseService(): DatabaseService

    fun getNetworkHelper(): NetworkHelper
}
