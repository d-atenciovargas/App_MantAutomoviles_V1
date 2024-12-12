package com.example.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class codigoVerificacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_codigo_verificacion)
        intent.getStringExtra("verification_code")
    }
}