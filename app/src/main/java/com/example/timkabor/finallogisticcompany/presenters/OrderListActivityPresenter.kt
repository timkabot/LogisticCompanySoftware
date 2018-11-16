package com.example.timkabor.finallogisticcompany.presenters

import android.graphics.Bitmap
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.models.DispatchOrderResponse
import com.example.timkabor.finallogisticcompany.network.Api
import com.example.timkabor.finallogisticcompany.OrderListView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


@InjectViewState
class OrderListActivityPresenter : MvpPresenter<OrderListView>() {
    private val TAG = "ORDERLIST"
    @Inject
    lateinit var apiService: Api
    private var next: Int = 1
    private var totalPages: Int = 1 // will update after first response

    init {
    }

    fun downloadNextOrders() {
        if (next == null || next < 0 || next > totalPages) {
            Log.d(TAG, "End page reached.")
            return
        }
        val obs = apiService.getDispatchOrders("Token " + App.getAuthToken(), next)
        obs.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    run {
                        if (result != null) {
                            result as DispatchOrderResponse
                            // In case if cache data was saved and suddenly Internet appeared,
                            // so we need to clear cached values and show new.
                            if (next == 1)
                                viewState.clearList()
                            if (result.next != null)
                                next = result.next
                            totalPages = result.total_pages
                            Log.d(TAG, "Orders length: " + result.results?.size.toString())

                            viewState.updateOrders(result.results)
                            Log.d(TAG, "Received data successfully!")
                        } else {
                            Log.d(TAG, "Received null data!")
                        }
                    }
                }, { error ->
                    val token = App.getAuthToken()
                    if (token == null || token.length < 4)
                        viewState.goToLoginActivity()
                    else
                        viewState.showOrdersFromCache()
                    Log.d(TAG, "getDispatchOrders(): " + error)
                })
    }

    fun sendAllImages(a: List<Pair<Bitmap, String>>) {
        a.map { pair ->
            SignatureSender.sendSignature(pair.component1(), pair.component2(),
                    viewState, apiService, saveOnFail = false)
        }
    }


}