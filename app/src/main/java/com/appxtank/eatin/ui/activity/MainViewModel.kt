package com.appxtank.eatin.ui.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.appxtank.eatin.data.local.DatabaseService
import com.appxtank.eatin.data.remote.NetworkService
import com.appxtank.eatin.data.remote.response.ExcludeList
import com.appxtank.eatin.data.remote.response.MenuResponse
import com.appxtank.eatin.data.remote.response.Variation
import com.appxtank.eatin.di.ActivityScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    fun checkForExcludedItem(variationList: List<Variation>): List<Variation> {
        if (selectedVariation == null) {
            return variationList
        }


        val menuResponse: MenuResponse = menuResponse.value!!
        val listType = object : TypeToken<ArrayList<ExcludeList>>() {}.type
        var excludeList: ArrayList<ArrayList<ExcludeList>>? = ArrayList()

        for (elementList in menuResponse.variants.exclude_list)
            excludeList?.add(
                Gson().fromJson<ArrayList<ExcludeList>>(
                    elementList.toString(),
                    listType
                )
            )

        if (excludeList != null && excludeList.size > 0) {
            for (excludeItemList in excludeList) {
                for (i in 0 until excludeItemList.size) {
                    if (excludeItemList[i].group_id.toInt() == selectedVariants
                        && excludeItemList[i].variation_id.toInt() == selectedVariation!!.id.toInt()
                    ) {
                        if (i != 1) {
                            for (variationItem in variationList) {
                                if (excludeItemList[i + 1].variation_id.toInt() == variationItem.id.toInt())
                                    variationItem.isExcluded = true
                            }
                        } else {
                            continue
                        }
                    }
                }
            }
        }


        return variationList
    }
}
