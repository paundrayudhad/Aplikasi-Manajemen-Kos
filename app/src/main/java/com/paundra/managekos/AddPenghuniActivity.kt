package com.paundra.managekos

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase

class AddPenghuniActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("penghuni")
    lateinit var etNamaPenghuni : EditText
    lateinit var etNoHpPenghuni : EditText
    lateinit var btTambahPenghuni : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_penghuni)

        etNamaPenghuni = findViewById(R.id.etNama)
        etNoHpPenghuni = findViewById(R.id.etNoHP)
        btTambahPenghuni = findViewById(R.id.btTambahPenghuni)

        btTambahPenghuni.setOnClickListener{
            val nama = etNamaPenghuni.text.toString()
            val nohp = etNoHpPenghuni.text.toString()
            if(nama.isEmpty() || nohp.isEmpty()){
                Toast.makeText(this, "Harap isi semua isian", Toast.LENGTH_SHORT).show()
            } else {
                val penghuniBaru = Penghuni(null, nama, nohp)
                val newPeople = myRef.push()
                val idPenghuni = newPeople.key.toString()

                penghuniBaru.id = idPenghuni
                newPeople.setValue(penghuniBaru)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("SimpanData", "Gagal menyimpan pilihan: ${it.message}")
                        Toast.makeText(this, "Terjadi kesalahan saat menyimpan pilihan.", Toast.LENGTH_SHORT).show()
                    }
            }

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}