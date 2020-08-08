package ru.sinura.hackaton.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import ru.sinura.hackaton.R
import ru.sinura.hackaton.login.LoginActivity
import ru.sinura.hackaton.repo.Repo

class RegisterActivity: Activity() {

    private val repo = Repo.getInstance()

    private val responseCallback = object: RegisterResponse {
        override fun onError() {
            runOnUiThread {
                Toast.makeText(this@RegisterActivity, "Error", Toast.LENGTH_LONG).show()
            }
        }

        override fun onSuccess() {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etMedCard = findViewById<EditText>(R.id.etMedCard)
        val etBirthDate = findViewById<EditText>(R.id.etBirth)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassport = findViewById<EditText>(R.id.etPassport)
        val etCity = findViewById<EditText>(R.id.etCity)
        val etStreet = findViewById<EditText>(R.id.etStreet)

        val btRegister = findViewById<Button>(R.id.btRegister)
        val cbAgreement = findViewById<CheckBox>(R.id.cbAgreement)

        repo.registerResponse = responseCallback

        cbAgreement.setOnCheckedChangeListener { _, isChecked ->
            btRegister.isEnabled = isChecked
        }

        btRegister.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val passwordConfirmation = etPasswordConfirmation.text.toString()
            val medCard = etMedCard.text.toString()
            val birth = etBirthDate.text.toString()
            val phone = etPhone.text.toString()
            val passport = etPassport.text.toString()
            val city = etCity.text.toString()
            val street = etStreet.text.toString()

            if (password != passwordConfirmation) {
                etPassword.error = getString(R.string.passwordsDontMatch)
                etPasswordConfirmation.error = getString(R.string.passwordsDontMatch)
                return@setOnClickListener
            }

            repo.doRegister(
                firstName = if (firstName.isEmpty()) "123" else firstName,
                lastName = if (lastName.isEmpty()) "123" else lastName,
                email = if (email.isEmpty()) "123" else email,
                password = if (password.isEmpty()) "123" else password,
                medCard = if (medCard.isEmpty()) "123" else medCard,
                birth = if (birth.isEmpty()) "123" else birth,
                phone = if (phone.isEmpty()) "123" else phone,
                passport = if (passport.isEmpty()) "123" else passport,
                city = if (city.isEmpty()) "123" else city,
                street = if (street.isEmpty()) "123" else street
            )
        }
    }

    interface RegisterResponse {
        fun onSuccess()
        fun onError()
    }
}