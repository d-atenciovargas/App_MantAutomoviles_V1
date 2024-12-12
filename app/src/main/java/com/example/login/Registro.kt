package com.example.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_registro.finalizar
import kotlinx.android.synthetic.main.activity_registro.regresar

class Registro : AppCompatActivity() {
    private lateinit var nombres: TextInputEditText
    private lateinit var apellidos: TextInputEditText
    private lateinit var fechaNacimiento: TextInputEditText
    private lateinit var telefono: TextInputEditText
    private lateinit var registroEmail: TextInputEditText
    private lateinit var registroContraseña: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializar los EditText
        nombres = findViewById(R.id.nombres)
        apellidos = findViewById(R.id.apellidos)
        fechaNacimiento = findViewById(R.id.fechaNacimiento)
        telefono = findViewById(R.id.telefono)
        registroEmail = findViewById(R.id.registroEmail)
        registroContraseña = findViewById(R.id.registroContraseña)

        // Formatear la fecha de nacimiento
        fechaNacimiento.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                isFormatting = true
                val input = s.toString().replace("/", "")
                val formattedInput = StringBuilder()

                // Agregar los caracteres y los separadores
                for (i in input.indices) {
                    formattedInput.append(input[i])
                    if ((i == 1 || i == 3) && i < 8) {
                        formattedInput.append("/")
                    }
                }

                // Limitar a 10 caracteres (dd/mm/yyyy)
                if (formattedInput.length > 10) {
                    formattedInput.setLength(10)
                }

                fechaNacimiento.setText(formattedInput)
                fechaNacimiento.setSelection(formattedInput.length) // Colocar el cursor al final
                isFormatting = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Configurar OnClickListener para el botón "Regresar"
        regresar.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar OnClickListener para el botón "Finalizar"
        finalizar.setOnClickListener {
            val email = registroEmail.text.toString()
            val password = registroContraseña.text.toString()
            val name = nombres.text.toString()
            val lastname = apellidos.text.toString()
            val birthdate = fechaNacimiento.text.toString()
            val phone = telefono.text.toString().replace("+56 ", "").trim()

            if (!isValidDate(birthdate)) {
                Toast.makeText(this, "Fecha de nacimiento inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPhone(phone)) {
                Toast.makeText(this, "Debes ingresar 9 dígitos en el número de teléfono", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = UserManager(this)

            // Verificar si el correo ya existe
            if (db.emailExists(email)) {
                Toast.makeText(this, "El correo electrónico ya se encuentra en uso", Toast.LENGTH_SHORT).show()
            } else {
                // Intentar registrar usuario
                val id = db.addUser(email, password, name, lastname, birthdate, phone)

                if (id != -1L) {
                    Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configuración inicial para el teléfono
        telefono.setText("+56 ")
        telefono.addTextChangedListener(object : TextWatcher {
            private var isFormatting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isFormatting) return

                val input = s.toString().replace("+56 ", "").replace(" ", "")
                if (input.length > 9) {
                    isFormatting = true
                    telefono.setText("+56 ${input.substring(0, 9)}")
                    telefono.text?.let { telefono.setSelection(it.length) }
                    isFormatting = false
                } else {
                    isFormatting = true
                    telefono.setText("+56 $input")
                    telefono.text?.let { telefono.setSelection(it.length) }
                    isFormatting = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val parts = date.split("/")
            if (parts.size != 3) return false
            val day = parts[0].toIntOrNull() ?: return false
            val month = parts[1].toIntOrNull() ?: return false
            val year = parts[2].toIntOrNull() ?: return false
            parts[0].length == 2 && parts[1].length == 2 && parts[2].length == 4 && month in 1..12 && day in 1..31
        } catch (e: Exception) {
            false
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.length == 9 // Asegura que el número tenga exactamente 9 dígitos
    }
}
