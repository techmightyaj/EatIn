package com.appxtank.eatin.ui.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appxtank.eatin.data.local.DatabaseService
import com.appxtank.eatin.data.remote.NetworkService
import com.appxtank.eatin.data.remote.response.MenuResponse
import com.appxtank.eatin.data.remote.response.Variation
import com.appxtank.eatin.di.ActivityScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScope
class MainViewModel @Inject constructor(
    private val compositeDisposable: CompositeDisposable,
    private val databaseService: DatabaseService,
    private val networkService: NetworkService
) {

    companion object {
        const val TAG = "MainViewModel"
    }

    val menuResponse = MutableLiveData<MenuResponse>()
    var selectedVariants = 0
    var selectedVariation: Variation? = null

    fun getMenu() {
        compositeDisposable.add(
            networkService.fetchMenu()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        menuResponse.postValue(it)
                    },
                    {
                        Log.d(TAG, it.toString())
                    }
                )
        )
    }

//    fun parseMenuItems(menuResponse: MenuResponse){
//        var map = LinkedHashMap<VariantGroup, List<Variation>>()
//        for(i in menuResponse.variants.variant_groups.indices)
//            map[menuResponse.variants.variant_groups[i]] = menuResponse.variants.variant_groups[i].variations
//
//        parsedMenuItems.postValue(map)
//    }
}
