package com.paundra.kosfomo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class TagihanActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TagihanAdapter
    private val listTagihan = mutableListOf<ModelTagihan>()

    // Firebase reference
    private val database = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef: DatabaseReference = database.getReference("tagihan")
    private val kamarRef: DatabaseReference = database.getReference("kamar")
    private val penghuniRef: DatabaseReference = database.getReference("penghuni")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tagihan)

        recyclerView = findViewById(R.id.rvTagihan)
        adapter = TagihanAdapter(
            context = this,
            listTagihan = listTagihan,
            onEditClicked = { tagihan ->
                Toast.makeText(this, "Edit: ${tagihan.id}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClicked = { tagihan ->
                deleteTagihan(tagihan.id ?: "")
            },
            onStatusUpdated = { updatedTagihan ->
                updateStatusInFirebase(updatedTagihan)
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchTagihan()
    }

    // Fetch data from Firebase
    private fun fetchTagihan() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listTagihan.clear() // Clear current list
                for (data in snapshot.children) {
                    val tagihan = data.getValue(ModelTagihan::class.java)
                    tagihan?.let { listTagihan.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TagihanActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Delete item from Firebase
    private fun deleteTagihan(id: String) {
        myRef.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Tagihan berhasil dihapus", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Update status in Firebase
    private fun updateStatusInFirebase(tagihan: ModelTagihan) {
        val tagihanRef = myRef.child(tagihan.id ?: return)
        tagihanRef.child("statusTagihan").setValue(tagihan.statusTagihan)
            .addOnSuccessListener {
                Toast.makeText(this, "Status pembayaran diperbarui", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui status: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
