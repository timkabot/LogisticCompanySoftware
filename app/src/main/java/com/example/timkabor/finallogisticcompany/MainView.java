package com.example.timkabor.finallogisticcompany;

import com.arellomobile.mvp.MvpView;

public interface MainView extends MvpView, SignatureDeliveryNotifiable {
    void updateInfoAboutOrder();
}
