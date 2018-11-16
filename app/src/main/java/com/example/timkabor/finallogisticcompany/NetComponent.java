package com.example.timkabor.finallogisticcompany;

import com.example.timkabor.finallogisticcompany.presenters.LoginActivityPresenter;
import com.example.timkabor.finallogisticcompany.presenters.MapActivityPresenter;
import com.example.timkabor.finallogisticcompany.presenters.OrderListActivityPresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Java-Ai-BOT on 23.10.2018.
 */


@Singleton
@Component(modules = {NetModule.class})
public interface NetComponent {
    void inject(OrderListActivityPresenter presenter);

    void inject(MapActivityPresenter presenter);

    void inject(LoginActivityPresenter presenter);
}


