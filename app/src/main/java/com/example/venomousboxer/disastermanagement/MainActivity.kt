package com.example.venomousboxer.disastermanagement

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    lateinit var button : Button
    lateinit var emailEditText: TextInputEditText
    lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button_log_in)
        emailEditText = findViewById(R.id.inputEditTextEmail)
        passwordEditText = findViewById(R.id.inputEditTextPassword)

        button.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if(email != "abc@gmail.com" || password != "xyzxyz"){
                Toast.makeText(this, "Wrong Email or Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
