package com.appxtank.eatin.di.component

import com.appxtank.eatin.di.ActivityModule
import com.appxtank.eatin.di.ActivityScope
import com.appxtank.eatin.ui.activity.MainActivity


import dagger.Component

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: MainActivity)
}
