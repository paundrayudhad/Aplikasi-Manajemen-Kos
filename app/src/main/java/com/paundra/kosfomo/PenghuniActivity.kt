// PenghuniActivity.kt
package com.paundra.kosfomo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_penghuni)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPenghuni)
        recyclerView.layoutManager = LinearLayoutManager(this)

        penghuniAdapter = PenghuniAdapter(this, mutableListOf(), database)
        recyclerView.adapter = penghuniAdapter

        getPenghuniList()

        findViewById<Button>(R.id.btnAddPenghuni).setOnClickListener {
            val pindah = Intent(this@PenghuniActivity, AddPenghuniActivity::class.java)
            startActivity(pindah)
        }
    }

    private fun getPenghuniList() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val penghuniList = mutableListOf<Penghuni>()
                for (data in snapshot.children) {
                    // Ambil ID dari Firebase
                    val id = data.key ?: ""
                    val nama = data.child("nama").getValue(String::class.java) ?: ""
                    val nohp = data.child("nohp").getValue(String::class.java) ?: ""

                    // Buat objek Penghuni dengan ID yang benar
                    val penghuni = Penghuni(id = id, nama = nama, nohp = nohp)
                    penghuniList.add(penghuni)
                }
                penghuniAdapter.updateList(penghuniList)
            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })
    }
}