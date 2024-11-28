package com.paundra.kosfomo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PenghuniActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("penghuni")
    private lateinit var penghuniAdapter: PenghuniAdapter
    private lateinit var tvEmptyPenghuni: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penghuni)

        // Inisialisasi RecyclerView dan TextView
        tvEmptyPenghuni = findViewById(R.id.tvEmptyPenghuni)
        recyclerView = findViewById(R.id.recyclerViewPenghuni)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set adapter untuk RecyclerView
        penghuniAdapter = PenghuniAdapter(this, mutableListOf(), database)
        recyclerView.adapter = penghuniAdapter

        // Panggil fungsi untuk mengambil data penghuni
        getPenghuniList()

        // Tombol untuk menambah penghuni
        findViewById<Button>(R.id.btnAddPenghuni).setOnClickListener {
            val pindah = Intent(this@PenghuniActivity, AddPenghuniActivity::class.java)
            startActivity(pindah)
        }
    }

    private fun getPenghuniList() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val penghuniList = mutableListOf<Penghuni>()

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        // Ambil ID dari Firebase
                        val id = data.key ?: ""
                        val nama = data.child("nama").getValue(String::class.java) ?: ""
                        val nohp = data.child("nohp").getValue(String::class.java) ?: ""

                        // Buat objek Penghuni dengan ID yang benar
                        val penghuni = Penghuni(id = id, nama = nama, nohp = nohp)
                        penghuniList.add(penghuni)
                    }
                }

                // Cek apakah penghuniList kosong
                if (penghuniList.isEmpty()) {
                    tvEmptyPenghuni.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvEmptyPenghuni.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    penghuniAdapter.updateList(penghuniList) // Update data di adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}
