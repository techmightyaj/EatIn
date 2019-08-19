package com.appxtank.eatin.di

import android.app.Activity
import android.content.Context


import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private val activity: Activity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context = activity
}
