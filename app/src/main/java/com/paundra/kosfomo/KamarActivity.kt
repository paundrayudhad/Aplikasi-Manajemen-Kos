package com.paundra.kosfomo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KamarActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("kamar")
    private lateinit var kamarAdapter: KamarAdapter
    private lateinit var tvEmptyData: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kamar)

        // Inisialisasi variabel di sini
        tvEmptyData = findViewById(R.id.tvEmptyData2)
        recyclerView = findViewById(R.id.recyclerViewKamar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        kamarAdapter = KamarAdapter(this, mutableListOf(), database)
        recyclerView.adapter = kamarAdapter

        getKamarList() // Memanggil fungsi dengan nama yang benar

        findViewById<Button>(R.id.btnAddKamar).setOnClickListener {
            val pindah = Intent(this, ActivityAddKamar::class.java)
            startActivity(pindah)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getKamarList() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val kamarList = mutableListOf<Kamar>()

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val id = data.key ?: ""
                        val namaKamar = data.child("namaKamar").getValue(String::class.java) ?: ""
                        val penghuni = data.child("penghuni").getValue(String::class.java) ?: ""
                        val status = data.child("status").getValue(String::class.java) ?: ""
                        val harga = data.child("harga").getValue(String::class.java) ?: ""

                        val kamar = Kamar(
                            idKamar = id,
                            namaKamar = namaKamar,
                            penghuni = penghuni,
                            status = status,
                            harga = harga
                        )
                        kamarList.add(kamar)
                    }
                }

                // Cek apakah kamarList kosong
                if (kamarList.isEmpty()) {
                    tvEmptyData.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    tvEmptyData.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    kamarAdapter.updateList(kamarList) // Update data di adapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}

