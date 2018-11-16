package com.example.timkabor.finallogisticcompany.ui

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.test.ActivityInstrumentationTestCase2
import com.example.timkabor.finallogisticcompany.R
import org.junit.FixMethodOrder
import org.junit.runners.MethodSorters


/**
 * Created by Java-Ai-BOT on 07.11.2018.
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LoginActivityTest : ActivityInstrumentationTestCase2<LoginActivity>(LoginActivity::class.java) {

    private var loginActivity: Activity? = null


    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        loginActivity = getActivity()
        setActivityInitialTouchMode(true)
    }

    fun test_a_loginIncorrectPassword() {
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.input_login))
                .perform(ViewActions.replaceText("abc1"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
                .perform(ViewActions.replaceText("roo123t"), ViewActions.closeSoftKeyboard())
        Espresso.onView((ViewMatchers.withId(R.id.btn_login))).perform(ViewActions.click())
        Thread.sleep(1000)
        assert(!activity.isDestroyed)
    }


    fun test_b_loginCorrectPassword() {
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.input_login))
                .perform(ViewActions.replaceText("root"), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.input_password))
                .perform(ViewActions.replaceText("root"), ViewActions.closeSoftKeyboard())
        Espresso.onView((ViewMatchers.withId(R.id.btn_login))).perform(ViewActions.click())
        Thread.sleep(1000)
        assert(activity.isDestroyed)
    }


}