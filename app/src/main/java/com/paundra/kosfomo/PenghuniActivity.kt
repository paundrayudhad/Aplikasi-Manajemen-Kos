package com.paundra.kosfomo

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

class PenghuniActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("penghuni")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_penghuni)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewPenghuni)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Mendapatkan data dari Firebase
        getPenghuniList { penghuniList ->
            recyclerView.adapter = PenghuniAdapter(penghuniList)
        }

        findViewById<Button>(R.id.btnAddPenghuni).setOnClickListener{
            val pindah = Intent(this@PenghuniActivity, AddPenghuniActivity::class.java)
            startActivity(pindah)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainPenghuni)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getPenghuniList(onDataReceived: (List<Penghuni>) -> Unit) {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val penghuniList = mutableListOf<Penghuni>()
                for (data in snapshot.children) {
                    val penghuni = data.getValue(Penghuni::class.java)
                    penghuni?.let { penghuniList.add(it) }
                }
                onDataReceived(penghuniList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tampilkan log error
                error.toException().printStackTrace()
            }
        })
    }
}