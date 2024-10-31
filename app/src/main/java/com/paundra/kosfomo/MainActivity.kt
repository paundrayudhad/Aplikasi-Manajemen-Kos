package com.paundra.kosfomo

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var penghuniRef: DatabaseReference
    private lateinit var kamarRef: DatabaseReference
    private lateinit var tagihanRef: DatabaseReference
    lateinit var cvManagepenghuni : CardView
    lateinit var cvManageKamar : CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        database = FirebaseDatabase.getInstance()
        penghuniRef = database.getReference("penghuni")
        kamarRef = database.getReference("kamar")
        tagihanRef = database.getReference("tagihan")

        cvManagepenghuni = findViewById(R.id.cvManagePenghuni)
        cvManagepenghuni.setOnClickListener{
            val keManagePenghuni = Intent(this@MainActivity, PenghuniActivity::class.java)
            startActivity(keManagePenghuni)
        }
        cvManageKamar = findViewById(R.id.cvManageKamar)
        cvManageKamar.setOnClickListener{
            val keMenuKamar = Intent(this@MainActivity, KamarActivity::class.java)
            startActivity(keMenuKamar)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}