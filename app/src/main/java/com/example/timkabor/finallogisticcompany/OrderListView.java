package com.example.timkabor.finallogisticcompany;

import com.arellomobile.mvp.MvpView;
import com.example.timkabor.finallogisticcompany.models.DispatchOrder;

import java.util.List;

public interface OrderListView extends MvpView, SignatureDeliveryNotifiable {
    void goToLoginActivity();

    void updateOrders(List<DispatchOrder> orders);

    void showOrdersFromCache();

    void clearList();
}
