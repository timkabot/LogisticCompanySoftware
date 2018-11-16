package com.example.timkabor.finallogisticcompany.presenters

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.timkabor.finallogisticcompany.MainView
import com.example.timkabor.finallogisticcompany.network.Api
import javax.inject.Inject


@InjectViewState
class MapActivityPresenter : MvpPresenter<MainView>() {
    private val TAG = "MAP_PRESENTER"
    @Inject
    lateinit var apiService: Api

    init {
        viewState.updateInfoAboutOrder()
    }

    fun sendSignature(bitmap: Bitmap, order_id: String) {
        SignatureSender.sendSignature(bitmap, order_id, viewState, apiService)
    }
}
