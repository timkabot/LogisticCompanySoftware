package com.example.timkabor.finallogisticcompany.presenters

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.LoginView
import com.example.timkabor.finallogisticcompany.models.LoginBody
import com.example.timkabor.finallogisticcompany.models.MapTokenResponse
import com.example.timkabor.finallogisticcompany.network.Api
import com.example.timkabor.finallogisticcompany.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Java-Ai-BOT on 15.11.2018.
 */

@InjectViewState
class LoginActivityPresenter : MvpPresenter<LoginView>() {
    val TAG = "LOGIN_PRESENTER"
    @Inject
    lateinit var apiService: Api


    fun getAuthToken(loginInfo: LoginBody) {
        apiService.getAuthToken(loginInfo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    run {
                        if (result != null) {
                            App.saveAuthToken(result.token!!)
                            viewState.onLoginSuccess()
                            Log.d(TAG, "Received auth token successfully!")
                        } else {
                            Log.d(TAG, "Received null authtoken!")
                            viewState.onLoginFailed()
                            viewState.notify("Wrong Login")
                        }
                    }
                }, { error ->
                    Log.d(TAG, "getToken: ", error)
                    viewState.notify(R.string.incorrect_login_toast)
                })
    }

    fun getMapToken(driverToken: String): String? {
        val call = apiService.getMapToken("Token " + driverToken)
        call.enqueue(object : Callback<MapTokenResponse> {
            override fun onFailure(call: Call<MapTokenResponse>?, t: Throwable?) {
                Log.d(TAG, t.toString())
            }

            override fun onResponse(call: Call<MapTokenResponse>?, response: Response<MapTokenResponse>?) {
                // TODO: test it
                val token = response?.body()?.map_api_key
                if (token != null)
                    App.saveMapToken(token)
                else
                    Log.d(TAG, "Null token gain.")
            }
        })
        return App.getMapToken()
    }
}
