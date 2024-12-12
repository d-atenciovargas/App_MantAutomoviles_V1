package com.example.login

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BDAutos(context: Context) : SQLiteOpenHelper(context, "BD_Autos.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        // Puedes agregar la creación de las tablas aquí si lo necesitas
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Manejo de actualizaciones de base de datos
    }

    // Obtener marcas
    fun obtenerMarcas(): List<String> {
        val listaMarcas = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT nombre FROM Marcas", null)

        if (cursor.moveToFirst()) {
            do {
                listaMarcas.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaMarcas
    }

    // Obtener el ID de una marca por nombre
    fun obtenerIdMarca(nombreMarca: String): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM Marcas WHERE nombre = ?", arrayOf(nombreMarca))
        var id = -1

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return id
    }

    // Obtener modelos por marca
    fun obtenerModelosPorMarca(marcaId: Int): List<String> {
        val listaModelos = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT modelo FROM modelos WHERE marca_id = ?", arrayOf(marcaId.toString()))

        if (cursor.moveToFirst()) {
            do {
                listaModelos.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaModelos
    }

    // Obtener el ID de un modelo por nombre y marca
    fun obtenerIdModelo(nombreModelo: String, marcaId: Int): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT id FROM modelos WHERE modelo = ? AND marca_id = ?", arrayOf(nombreModelo, marcaId.toString()))
        var id = -1

        if (cursor.moveToFirst()) {
            id = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return id
    }

    // Obtener años por modelo
    fun obtenerAniosPorModelo(modeloId: Int): List<Int> {
        val listaAnios = mutableListOf<Int>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT año FROM años WHERE modelo_id = ?", arrayOf(modeloId.toString()))

        if (cursor.moveToFirst()) {
            do {
                listaAnios.add(cursor.getInt(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaAnios
    }

    // Obtener versiones por modelo y año
    fun obtenerVersionesPorModeloYAnio(modeloId: Int, anio: Int): List<String> {
        val listaVersiones = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT version FROM versiones WHERE modelo_id = ? AND año = ?", arrayOf(modeloId.toString(), anio.toString()))

        if (cursor.moveToFirst()) {
            do {
                listaVersiones.add(cursor.getString(0))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listaVersiones
    }
}
