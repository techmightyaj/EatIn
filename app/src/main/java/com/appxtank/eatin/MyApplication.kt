package com.appxtank.eatin


import android.app.Application
import com.appxtank.eatin.data.local.DatabaseService
import com.appxtank.eatin.data.remote.NetworkService
import com.appxtank.eatin.di.component.ApplicationComponent
import com.appxtank.eatin.di.component.DaggerApplicationComponent
import com.appxtank.eatin.di.module.ApplicationModule
import javax.inject.Inject

class MyApplication : Application(){
    lateinit var applicationComponent: ApplicationComponent

    @Inject
    lateinit var networkService: NetworkService

    @Inject
    lateinit var databaseService: DatabaseService

    override fun onCreate() {
        super.onCreate()
        getDependencies()
    }

    private fun getDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }
}