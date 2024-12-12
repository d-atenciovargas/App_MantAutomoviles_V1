package com.example.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_main.Email
import kotlinx.android.synthetic.main.activity_main.botonIngresar
import kotlinx.android.synthetic.main.activity_main.botonRegistro
import kotlinx.android.synthetic.main.activity_main.contrasena
import kotlinx.android.synthetic.main.activity_main.facebook
import kotlinx.android.synthetic.main.activity_main.google

class Login : AppCompatActivity() {

    private val GOOGLE_SIGN_IN_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instancia de UserManager
        val userManager = UserManager(this)

        // Configurar listener para el botón "Ingresar"
        botonIngresar.setOnClickListener {
            // Obtener el texto ingresado en los campos de texto
            val email = Email.text.toString()
            val password = contrasena.text.toString()

            // Validar el correo electrónico y la contraseña aquí si es necesario
            if (EmailNoValido(email) && ContrasenaNoValida(password)) {
                // Verifica si el usuario existe en la base de datos
                if (userManager.checkUser(email, password)) {
                    // Si las credenciales son válidas, accede a la actividad de menú
                    val intent = Intent(this, Menu::class.java)
                    startActivity(intent)
                } else {
                    // Muestra un mensaje de error si las credenciales son incorrectas
                    Toast.makeText(this, "Correo electrónico o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Correo electrónico o contraseña no válidos", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar listener para el botón "Registro"
        botonRegistro.setOnClickListener {
            // Configurar un Intent para iniciar la actividad deseada cuando se presione el botón "botonRegistro"
            val intent = Intent(this, Registro::class.java)
            startActivity(intent)
        }

        // Configurar listener para el botón de Google
        google.setOnClickListener {
            signInWithGoogle()
        }

        // Configurar listener para el botón de Facebook
        facebook.setOnClickListener {
            // Aquí implementar la lógica para abrir la aplicación de Facebook
            val facebookAppUri = "fb://facewebmodal/f?href=https://www.facebook.com"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookAppUri))

            // Verificar si la aplicación de Facebook está instalada
            if (intent.resolveActivity(packageManager) != null) {
                // Si la aplicación de Facebook está instalada, abrirla
                startActivity(intent)
            } else {
                // Si la aplicación de Facebook no está instalada, abrir el navegador web con la URL de Facebook
                val webUri = "https://www.facebook.com"
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUri))
                startActivity(webIntent)
            }
            // Por ejemplo, puedes usar el SDK de Facebook
            Toast.makeText(this, "Iniciar sesión con Facebook", Toast.LENGTH_SHORT).show()
        }
    }

    private fun EmailNoValido(email: String): Boolean {
        // Implementación de la lógica de validación del correo electrónico
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun ContrasenaNoValida(password: String): Boolean {
        // Implementación de la lógica de validación de la contraseña
        return password.isNotEmpty() && password.length >= 6
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado del inicio de sesión con Google
        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Aquí puedes hacer lo que necesites con la cuenta de Google
                Toast.makeText(this, "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show()
            } catch (e: ApiException) {
                // El inicio de sesión con Google falló
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun forgotPasswordClicked(view: View) {
        // Manejar el clic en "¿Olvidaste tu contraseña?"
        val intent = Intent(this, restablecerContrasena::class.java)
        startActivity(intent)
    }
}

