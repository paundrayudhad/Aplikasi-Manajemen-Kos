package com.paundra.kosfomo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class KamarAdapter(
    private val context: Context,
    private var kamarList: MutableList<Kamar>,
    private val database: FirebaseDatabase) : RecyclerView.Adapter<KamarAdapter.KamarViewHolder>() {

    class KamarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKamarTextView: TextView = itemView.findViewById(R.id.tvNamaKamar)
        val penghuniTextView: TextView = itemView.findViewById(R.id.tvKontakPenghuni)
        val statusTextView: TextView = itemView.findViewById(R.id.tvStatusKamar)
        val hargaTextView: TextView = itemView.findViewById(R.id.tvHargaKamar)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KamarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_kamar, parent, false)
        return KamarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KamarViewHolder, position: Int) {
        val kamar = kamarList[position]
        holder.namaKamarTextView.text = kamar.namaKamar
        holder.penghuniTextView.text = kamar.penghuni
        holder.statusTextView.text = kamar.status
        holder.hargaTextView.text = kamar.harga.toString()
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmation(kamar, position)
        }
    }

    override fun getItemCount(): Int {
        return kamarList.size
    }

    private fun showDeleteConfirmation(kamar: Kamar, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus data ${kamar.namaKamar}?")
            .setPositiveButton("Ya") { _, _ ->
                deleteKamar(kamar, position)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    private fun deleteKamar(kamar: Kamar, position: Int) {
        val myRef = database.getReference("kamar")

        myRef.child(kamar.idKamar.toString()).removeValue()
            .addOnSuccessListener {
                // Refresh data after deletion to avoid data inconsistencies
                refreshDataFromFirebase()
                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun refreshDataFromFirebase() {
        val myRef = database.getReference("kamar")
        myRef.get().addOnSuccessListener { snapshot ->
            kamarList.clear()
            for (dataSnapshot in snapshot.children) {
                val kamar = dataSnapshot.getValue(Kamar::class.java)
                kamar?.let { kamarList.add(it) }
            }
            notifyDataSetChanged()
        }
    }

    fun updateList(newList: List<Kamar>) {
        kamarList.clear()
        kamarList.addAll(newList)
        notifyDataSetChanged()
    }
}
