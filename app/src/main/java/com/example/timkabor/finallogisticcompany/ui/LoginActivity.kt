package com.example.timkabor.finallogisticcompany.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.DaggerNetComponent
import com.example.timkabor.finallogisticcompany.LoginView
import com.example.timkabor.finallogisticcompany.models.LoginBody
import com.example.timkabor.finallogisticcompany.presenters.LoginActivityPresenter
import com.example.timkabor.finallogisticcompany.R


class LoginActivity : MvpAppCompatActivity(), LoginView {
    override fun notify(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun notify(incorrect_login_toast: Int) {
        Toast.makeText(this, incorrect_login_toast, Toast.LENGTH_SHORT).show()
    }

    private lateinit var loginText: EditText
    private lateinit var _passwordText: EditText
    private lateinit var _loginButton: Button
    @InjectPresenter
    lateinit var presenter: LoginActivityPresenter


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        presenter = LoginActivityPresenter()
        DaggerNetComponent.create().inject(presenter)
        loginText = findViewById(R.id.input_login)
        _passwordText = findViewById((R.id.input_password))
        _loginButton = findViewById(R.id.btn_login)
        _loginButton.setOnClickListener { onLogin() }
    }

    @SuppressLint("CheckResult")
    private fun onLogin() {
        val login: String = loginText.text.toString()
        val password: String = _passwordText.text.toString()

        val loginInfo = LoginBody()
        loginInfo.username = login
        loginInfo.password = password
        if (validate()) {
            presenter.getAuthToken(loginInfo)
        }
    }

    override fun onLoginSuccess() {
        val authToken = App.getAuthToken()
        if (authToken == null) {
            notify("Authorization token not received!")
            return
        }
        App.saveAuthToken(authToken)
        val mapToken = presenter.getMapToken(authToken)
        if (mapToken == null) {
            notify("Map token is null! Try again.")
            return
        }
        App.saveMapToken(mapToken)
        _loginButton.isEnabled = true
        val i = Intent(applicationContext, OrderListActivity::class.java)

        startActivity(i)
        finish()
    }

    override fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()

        _loginButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var valid = true

        val email = loginText.text.toString()
        val password = _passwordText.text.toString()

        if (email.isEmpty()) {
            loginText.error = "enter a valid username"
            valid = false
        } else {
            loginText.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText.error = null
        }

        return valid
    }
}

