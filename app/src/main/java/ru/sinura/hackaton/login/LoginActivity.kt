package ru.sinura.hackaton.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import ru.sinura.hackaton.R
import ru.sinura.hackaton.main.MainActivity
import ru.sinura.hackaton.register.RegisterActivity
import ru.sinura.hackaton.repo.Repo

class LoginActivity: Activity() {

    private val repo = Repo.getInstance()
    private val responseCallback = object: LoginResponse {

        override fun onSuccess(token: String) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivity(intent)
        }

        override fun onError() {
            Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val btRegister = findViewById<Button>(R.id.btRegister)
        btRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        repo.loginResponse = responseCallback

        val btLogin = findViewById<Button>(R.id.btLogin)
        btLogin.setOnClickListener {
            val login = etLogin.text.toString()
            val password = etPassword.text.toString()

            if (login.isEmpty()) {
                etLogin.error = getString(R.string.fieldCantBeEmpty)
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                etPassword.error = getString(R.string.fieldCantBeEmpty)
                return@setOnClickListener
            }

            repo.loginUser(login, password)
        }
    }

    interface LoginResponse {
        fun onSuccess(token: String)
        fun onError()
    }
}