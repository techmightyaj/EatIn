package com.appxtank.eatin.data.remote

import com.appxtank.eatin.data.remote.response.MenuResponse
import io.reactivex.Single
import retrofit2.http.GET

interface NetworkService {
    @GET(EndPoints.MENU)
    fun fetchMenu():Single<MenuResponse>
}