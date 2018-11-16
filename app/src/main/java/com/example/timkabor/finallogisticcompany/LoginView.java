package com.example.timkabor.finallogisticcompany;

import com.arellomobile.mvp.MvpView;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Java-Ai-BOT on 15.11.2018.
 */

public interface LoginView extends MvpView {
    void onLoginSuccess();

    void onLoginFailed();

    void notify(@NotNull String s);

    void notify(@NotNull int incorrect_login_toast);
}
