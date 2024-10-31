package com.paundra.kosfomo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.paundra.kosfomo.R

class ActivityAddKamar : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var etNamaKamar: EditText
    private lateinit var etHarga: EditText
    private lateinit var spStatus: Spinner
    private lateinit var btTambahKamar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_kamar)


        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Bind views
        etNamaKamar = findViewById(R.id.etNamaKamar)
        etHarga = findViewById(R.id.etHarga)
        spStatus = findViewById(R.id.spStatus)
        btTambahKamar = findViewById(R.id.btTambahKamar)

        // Isi data spinner
        val statusList = listOf("Tersedia", "Tidak Tersedia")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStatus.adapter = adapter

        // Button click listener
        btTambahKamar.setOnClickListener {
            saveKamarData()
        }
    }

    private fun saveKamarData() {
        // Get values from the input fields
        val namaKamar = etNamaKamar.text.toString()
        val penghuni = "Kosong"
        val harga = etHarga.text.toString()
        val status = spStatus.selectedItem.toString()

        // Validation for empty fields
        if (namaKamar.isEmpty() || harga.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val kamarId = database.child("kamar").push().key ?: return

        // Create a data object
        val kamar = Kamar(kamarId, namaKamar, penghuni, harga.toString(), status)

        // Write data to Firebase Realtime Database
        database.child("kamar").child(kamarId).setValue(kamar)
            .addOnSuccessListener {
                Toast.makeText(this, "Data kamar berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                etNamaKamar.text.clear()
                etHarga.text.clear()
                spStatus.setSelection(0)
            finish()// Reset spinner
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menambahkan data kamar", Toast.LENGTH_SHORT).show()
            }
    }
}
