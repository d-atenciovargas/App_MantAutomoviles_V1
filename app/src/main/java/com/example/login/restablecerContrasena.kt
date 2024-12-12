package com.example.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_restablecer_contrasena.Email
import kotlinx.android.synthetic.main.activity_restablecer_contrasena.RestContrasena
import kotlinx.android.synthetic.main.activity_restablecer_contrasena.regresar2

class restablecerContrasena : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restablecer_contrasena)

        // Configurar listener para el botón "Restablecer Contraseña"
        RestContrasena.setOnClickListener {
            // Obtener el correo electrónico ingresado
            val email = Email.text.toString()

            // Validar el correo electrónico aquí si es necesario
            if (email.isNotEmpty() && isValidEmail(email)) {
                // Aquí puedes implementar la lógica para restablecer la contraseña
                // Por ejemplo, puedes enviar un correo electrónico con un enlace para restablecer la contraseña
                Toast.makeText(this, "Se ha enviado un correo electrónico para restablecer la contraseña", Toast.LENGTH_SHORT).show()

                // Iniciar la actividad de código de verificación
                val intent = Intent(this, codigoVerificacion::class.java)
                startActivity(intent)
                finish() // Esto asegura que la actividad actual (restablecerContrasena) sea cerrada después de ir al código de verificación
            } else {
                // Mostrar un mensaje de error si el correo electrónico es inválido o está vacío
                Toast.makeText(this, "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show()
            }
        }


        regresar2.setOnClickListener {
            // Iniciar MainActivity (pantalla de inicio de sesión)
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Esto asegura que la actividad actual (Registro) sea cerrada después de ir al MainActivity
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
