package com.example.timkabor.finallogisticcompany.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.models.Constants
import com.example.timkabor.finallogisticcompany.R

class LogoActivity : AppCompatActivity() {

    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logo)

        initHandler()
    }

    fun checkToken(): Boolean {
        return App.getAuthToken() == null
    }

    fun initHandler() {
        handler = Handler()
        handler.postDelayed({
            var intent: Intent? = null
            intent = if (checkToken())
                Intent(this@LogoActivity, LoginActivity::class.java)
            else
                Intent(this@LogoActivity, OrderListActivity::class.java)

            startActivity(intent)
            finish()
        }, Constants.LOGO_DELAY)
    }
}