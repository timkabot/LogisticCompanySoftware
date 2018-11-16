package com.example.timkabor.finallogisticcompany;

import com.example.timkabor.finallogisticcompany.network.Api;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Java-Ai-BOT on 23.10.2018.
 */

@Module
public class NetModule {
    @Provides
    @Singleton
    Api provideRetrofitService() {
        return Api.Factory.create();
    }
}
