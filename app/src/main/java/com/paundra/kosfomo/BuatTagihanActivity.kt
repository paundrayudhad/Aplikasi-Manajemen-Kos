package com.paundra.kosfomo

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.*
import java.util.*

class BuatTagihanActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private lateinit var spIdPenghuni: Spinner
    private lateinit var spIdkamar: Spinner
    private lateinit var etHarga: EditText
    private lateinit var etTanggal: EditText
    private lateinit var btnTambahTagihan: Button

    private val penghuniList: MutableList<Penghuni> = mutableListOf()
    private val kamarList: MutableList<Kamar> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_buat_tagihan)

        // Inisialisasi View
        spIdPenghuni = findViewById(R.id.spIdPenghuni)
        spIdkamar = findViewById(R.id.spIdKamar)
        etHarga = findViewById(R.id.etHarga)
        etTanggal = findViewById(R.id.etTanggal)
        btnTambahTagihan = findViewById(R.id.btnTambahTagihan)


        // Inisialisasi Spinner dengan Adapter
        initSpinnerAdapter()

        // Fetch data dari Firebase
        fetchPenghuniData()
        fetchKamarData()

        // Handle input tanggal
        etTanggal.setOnClickListener { showDatePicker() }

        // Isi harga otomatis saat kamar dipilih
        spIdkamar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedKamar = kamarList[position]
                etHarga.setText(selectedKamar.harga.toString()) // Isi harga kamar ke EditText
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                etHarga.setText("") // Kosongkan harga jika tidak ada yang dipilih
            }
        }

        // Handle tombol tambah tagihan
        btnTambahTagihan.setOnClickListener { tambahTagihan() }

        // Atur padding sesuai inset
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Inisialisasi Spinner dengan Adapter
    private fun initSpinnerAdapter() {
        val penghuniAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, penghuniList)
        penghuniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spIdPenghuni.adapter = penghuniAdapter

        val kamarAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kamarList)
        kamarAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spIdkamar.adapter = kamarAdapter
    }

    // Menampilkan DatePickerDialog
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            etTanggal.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    // Ambil data penghuni dari Firebase
    private fun fetchPenghuniData() {
        val penghuniRef = database.getReference("penghuni")
        penghuniRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                penghuniList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val penghuni = childSnapshot.getValue(Penghuni::class.java)
                    penghuni?.let {
                        penghuniList.add(it)
                    }
                }
                (spIdPenghuni.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    // Ambil data kamar dari Firebase
    private fun fetchKamarData() {
        val kamarRef = database.getReference("kamar")
        kamarRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                kamarList.clear()
                for (childSnapshot in dataSnapshot.children) {
                    val kamar = childSnapshot.getValue(Kamar::class.java)
                    kamar?.let { kamarList.add(it) }
                }
                (spIdkamar.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error: ${error.message}")
            }
        })
    }

    // Tambah Tagihan ke Firebase
    private fun tambahTagihan() {
        val penghuni = spIdPenghuni.selectedItem as? Penghuni
        val kamar = spIdkamar.selectedItem as? Kamar
        val harga = etHarga.text.toString().toDoubleOrNull()
        val tanggal = etTanggal.text.toString()
        val status = "Belum Dibayar"

        if (penghuni == null || kamar == null || harga == null || tanggal.isBlank()) {
            showToast("Pastikan semua input sudah terisi dengan benar!")
            return
        }

        val tagihanId = database.getReference("tagihan").push().key ?: return
        val tagihan = ModelTagihan(tagihanId, penghuni.id, kamar.idKamar, harga, tanggal, status)

        database.getReference("tagihan").child(tagihanId).setValue(tagihan)
            .addOnSuccessListener {
                showToast("Tagihan berhasil ditambahkan!")
                finish()
            }
            .addOnFailureListener { showToast("Gagal menambahkan tagihan.") }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}