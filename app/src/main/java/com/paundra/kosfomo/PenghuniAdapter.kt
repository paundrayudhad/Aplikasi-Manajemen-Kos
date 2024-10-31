package com.paundra.kosfomo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class PenghuniAdapter(
    private val context: Context,
    private var penghuniList: MutableList<Penghuni>,
    private val database: FirebaseDatabase
) : RecyclerView.Adapter<PenghuniAdapter.PenghuniViewHolder>() {

    class PenghuniViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPenghuni: TextView = itemView.findViewById(R.id.tvNamaPenghuni)
        val tvKontakPenghuni: TextView = itemView.findViewById(R.id.tvKontakPenghuni)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PenghuniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_penghuni, parent, false)
        return PenghuniViewHolder(view)
    }

    override fun onBindViewHolder(holder: PenghuniViewHolder, position: Int) {
        val penghuni = penghuniList[position]

        // Tampilkan data
        holder.tvNamaPenghuni.text = penghuni.nama
        holder.tvKontakPenghuni.text = penghuni.nohp

        // Set click listener untuk tombol edit
        holder.btnEdit.setOnClickListener {
            showEditDialog(penghuni, position)
        }
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmation(penghuni, position)
        }
    }

    override fun getItemCount(): Int = penghuniList.size

    private fun showEditDialog(penghuni: Penghuni, position: Int) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_penghuni, null)

        val etNama = dialogView.findViewById<EditText>(R.id.etEditNama)
        val etKontak = dialogView.findViewById<EditText>(R.id.etEditNohp)

        // Isi form dengan data yang ada
        etNama.setText(penghuni.nama)
        etKontak.setText(penghuni.nohp)

        dialog.setView(dialogView)
            .setTitle("Edit Data Penghuni")
            .setPositiveButton("Simpan") { _, _ ->
                // Pastikan ID tetap sama saat update
                if (penghuni.id!!.isNotEmpty()) {
                    val updatedPenghuni = Penghuni(
                        id = penghuni.id,
                        nama = etNama.text.toString(),
                        nohp = etKontak.text.toString()
                    )
                    updatePenghuni(updatedPenghuni, position)
                } else {
                    Toast.makeText(context, "ID Penghuni tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updatePenghuni(penghuni: Penghuni, position: Int) {
        val myRef = database.getReference("penghuni")

        // Update menggunakan ID yang ada
        myRef.child(penghuni.id.toString()).setValue(penghuni)
            .addOnSuccessListener {
                // Update local list
                penghuniList[position] = penghuni
                notifyItemChanged(position)
                Toast.makeText(context, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showDeleteConfirmation(penghuni: Penghuni, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus data ${penghuni.nama}?")
            .setPositiveButton("Ya") { _, _ ->
                deletePenghuni(penghuni, position)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
    private fun deletePenghuni(penghuni: Penghuni, position: Int) {
        val myRef = database.getReference("penghuni")

        myRef.child(penghuni.id.toString()).removeValue()
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
        val myRef = database.getReference("penghuni")
        myRef.get().addOnSuccessListener { snapshot ->
            penghuniList.clear()
            for (dataSnapshot in snapshot.children) {
                val penghuni = dataSnapshot.getValue(Penghuni::class.java)
                penghuni?.let { penghuniList.add(it) }
            }
            notifyDataSetChanged()
        }
    }


    fun updateList(newList: List<Penghuni>) {
        penghuniList.clear()
        penghuniList.addAll(newList)
        notifyDataSetChanged()
    }
}