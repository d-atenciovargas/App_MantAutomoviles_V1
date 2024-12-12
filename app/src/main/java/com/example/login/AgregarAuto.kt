package com.example.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AgregarAuto : AppCompatActivity() {
    private lateinit var dbAutos: BDAutos
    private lateinit var spinnerMarca: Spinner
    private lateinit var spinnerModelo: Spinner
    private lateinit var spinnerAno: Spinner
    private lateinit var spinnerVersion: Spinner
    private lateinit var spinnerTransmision: Spinner
    private lateinit var editTextKilometraje: EditText
    private lateinit var editTextPlacaPatente: EditText
    private lateinit var barraNotificacion: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar_auto)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.titulo_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbAutos = BDAutos(this)

        spinnerMarca = findViewById(R.id.spinner_marca_auto)
        spinnerModelo = findViewById(R.id.spinner_modelo_auto)
        spinnerAno = findViewById(R.id.spinner_ano_auto)
        spinnerVersion = findViewById(R.id.spinner_version_auto)
        spinnerTransmision = findViewById(R.id.spinner_transmision_auto)
        editTextKilometraje = findViewById(R.id.edittext_kilometraje)
        editTextPlacaPatente = findViewById(R.id.edittext_placa_patente)
        barraNotificacion = findViewById(R.id.barra_notificacion)

        // Habilitar el clic en la barra de notificación
        barraNotificacion.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
            finish() // Cierra el activity actual
        }

        // Deshabilitar el EditText de la placa al inicio
        editTextPlacaPatente.isEnabled = false

        setupSpinnerMarcas()
        setupKilometrajeFormatter()
        setupPlacaPatenteFormatter()

        // Inicialmente deshabilitar el EditText de kilometraje
        editTextKilometraje.isEnabled = false
    }

    private fun setupSpinnerMarcas() {
        val marcas = listOf(getString(R.string.selecciona_marca)) + dbAutos.obtenerMarcas()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, marcas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMarca.adapter = adapter

        spinnerMarca.setSelection(0)
        spinnerMarca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    val selectedMarca = parent.getItemAtPosition(position) as String
                    cargarModelos(selectedMarca)
                    spinnerModelo.isEnabled = true
                } else {
                    reiniciarSpinners(spinnerModelo, spinnerAno, spinnerVersion, spinnerTransmision)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarModelos(marca: String) {
        val idMarca = dbAutos.obtenerIdMarca(marca)
        val modelos = listOf(getString(R.string.selecciona_modelo)) + dbAutos.obtenerModelosPorMarca(idMarca)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, modelos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerModelo.adapter = adapter

        spinnerModelo.setSelection(0)
        spinnerModelo.isEnabled = true

        spinnerModelo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    val selectedModelo = parent.getItemAtPosition(position) as String
                    cargarAnios(selectedModelo, idMarca)
                    spinnerAno.isEnabled = true
                } else {
                    reiniciarSpinners(spinnerAno, spinnerVersion, spinnerTransmision)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarAnios(modelo: String, idMarca: Int) {
        val idModelo = dbAutos.obtenerIdModelo(modelo, idMarca)
        val anios = listOf(getString(R.string.selecciona_ano)) + dbAutos.obtenerAniosPorModelo(idModelo)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anios)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAno.adapter = adapter

        spinnerAno.setSelection(0)
        spinnerAno.isEnabled = anios.size > 1

        spinnerAno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    val selectedAnio = parent.getItemAtPosition(position) as Int
                    cargarVersiones(idModelo, selectedAnio)
                    spinnerVersion.isEnabled = true
                } else {
                    reiniciarSpinners(spinnerVersion, spinnerTransmision)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarVersiones(modeloId: Int, anio: Int) {
        val versiones = listOf(getString(R.string.selecciona_version)) + dbAutos.obtenerVersionesPorModeloYAnio(modeloId, anio)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, versiones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVersion.adapter = adapter

        spinnerVersion.setSelection(0)
        spinnerVersion.isEnabled = versiones.size > 1

        spinnerVersion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0) {
                    spinnerTransmision.isEnabled = true
                    cargarTransmisiones()
                } else {
                    spinnerTransmision.isEnabled = false
                    editTextKilometraje.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun cargarTransmisiones() {
        val transmisiones = listOf(getString(R.string.selecciona_transmision), "Transmisión manual", "Transmisión automática")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transmisiones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTransmision.adapter = adapter

        spinnerTransmision.setSelection(0)

        spinnerTransmision.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                editTextKilometraje.isEnabled = position > 0
                if (position > 0 && editTextKilometraje.text.isNotEmpty()) {
                    editTextPlacaPatente.isEnabled = true // Habilitar placa si el kilometraje ya tiene valor
                } else {
                    editTextPlacaPatente.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                editTextKilometraje.isEnabled = false
                editTextPlacaPatente.isEnabled = false // Deshabilitar si no hay selección
            }
        }
    }

    private fun reiniciarSpinners(vararg spinners: Spinner) {
        for (spinner in spinners) {
            spinner.isEnabled = false
            spinner.setSelection(0)
        }
    }

    private fun setupKilometrajeFormatter() {
        editTextKilometraje.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    val cleanString = s.toString().replace(".", "")
                    val parsed = cleanString.toLongOrNull() ?: 0

                    if (parsed <= 999999) {
                        current = formatNumber(parsed)
                        editTextKilometraje.setText(current)
                        editTextKilometraje.setSelection(current.length)

                        // Habilitar el EditText de la patente si el kilometraje tiene valor
                        editTextPlacaPatente.isEnabled = current.isNotEmpty()
                    } else {
                        editTextKilometraje.setText(current)
                        editTextKilometraje.setSelection(current.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun formatNumber(number: Long): String {
        return String.format("%,d", number).replace(",", ".")
    }

    private fun setupPlacaPatenteFormatter() {
        editTextPlacaPatente.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val cleanString = s.toString().replace("·", "").replace(" ", "").toUpperCase()
                val formatted = formatPlacaPatente(cleanString)

                if (formatted != s.toString()) {
                    editTextPlacaPatente.setText(formatted)
                    editTextPlacaPatente.setSelection(formatted.length)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun formatPlacaPatente(input: String): String {
        val validInput = input.filter { it.isLetter() || it.isDigit() }

        return when {
            validInput.length >= 6 && validInput.matches(Regex("[A-Z]{4}\\d{2}")) -> {
                "${validInput.substring(0, 2)}·${validInput.substring(2, 4)}·${validInput.substring(4, 6)}"
            }
            validInput.length >= 6 && validInput.matches(Regex("[A-Z]{2}\\d{4}")) -> {
                "${validInput.substring(0, 2)}·${validInput.substring(2, 4)}·${validInput.substring(4, 6)}"
            }
            validInput.length >= 4 -> {
                if (validInput.length > 4) {
                    "${validInput.substring(0, 2)}·${validInput.substring(2, 4)}·${validInput.substring(4).take(2)}"
                } else {
                    validInput
                }
            }
            validInput.length >= 2 -> {
                "${validInput.substring(0, 2)}·${validInput.substring(2).take(4)}"
            }
            else -> validInput
        }
    }
}
