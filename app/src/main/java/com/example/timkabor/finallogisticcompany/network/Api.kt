package com.example.timkabor.finallogisticcompany.network

import com.example.timkabor.finallogisticcompany.models.DispatchOrderResponse
import com.example.timkabor.finallogisticcompany.models.LoginBody
import com.example.timkabor.finallogisticcompany.models.LoginResponse
import com.example.timkabor.finallogisticcompany.models.MapTokenResponse
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 * Created by Java-Ai-BOT on 05.10.2018.
 */
interface Api {
    @GET("api/v1/orders/dispatch")
    fun getDispatchOrders(@Header("Authorization") token: String?, @Query("page") page: Int): Observable<DispatchOrderResponse>

    @POST("api/v1/login/")
    fun getAuthToken(@Body loginBody: LoginBody): Observable<LoginResponse>

    @GET("api/v1/credentials/")
    fun getMapToken(@Header("Authorization") token: String?): Call<MapTokenResponse>

    @Multipart
    @POST("api/v1/orders/sign/")
    fun uploadSignature(@Header("Authorization") token: String?, @Part file: MultipartBody.Part,
                        @Part order_id: MultipartBody.Part): Observable<ResponseBody>

    companion object Factory {
        private val DEBUG = false //TODO: off in production

        fun create(): Api {

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = if (DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

            val client = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()
            val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create()
            val retrofit = Retrofit.Builder()
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl("http://vilunov.me:1339/")
                    .build()
            return retrofit.create(Api::class.java)
        }
    }
}