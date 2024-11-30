package com.paundra.managekos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class KamarAdapter(
    private val context: Context,
    private var kamarList: MutableList<Kamar>,
    private val database: FirebaseDatabase
) : RecyclerView.Adapter<KamarAdapter.KamarViewHolder>() {

    class KamarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaKamarTextView: TextView = itemView.findViewById(R.id.tvNamaKamar)
        val penghuniTextView: TextView = itemView.findViewById(R.id.tvKontakPenghuni)
        val statusTextView: TextView = itemView.findViewById(R.id.tvStatusKamar)
        val hargaTextView: TextView = itemView.findViewById(R.id.tvHargaKamar)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KamarViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_kamar, parent, false)
        return KamarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: KamarViewHolder, position: Int) {
        val kamar = kamarList[position]
        holder.namaKamarTextView.text = kamar.namaKamar
        if (kamar.penghuni == "Kosong") {
            holder.penghuniTextView.text = "Belum Digunakan"
        } else {
            holder.penghuniTextView.text = kamar.penghuni
        }
        holder.statusTextView.text = kamar.status
        holder.hargaTextView.text = "Rp. "+kamar.harga.toString()
        holder.btnEdit.setOnClickListener {
            showEditDialog(kamar, position)
        }
        holder.btnDelete.setOnClickListener {
            showDeleteConfirmation(kamar, position)
        }
    }

    override fun getItemCount(): Int {
        return kamarList.size
    }

    private fun showEditDialog(kamar: Kamar, position: Int) {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_kamar, null)

        val etNamaKamar = dialogView.findViewById<EditText>(R.id.etEditNamaKamar)
        val spPenghuniKamar = dialogView.findViewById<Spinner>(R.id.spEditPenghuniKamar)
        val spStatusKamar = dialogView.findViewById<Spinner>(R.id.spEditStatusKamar)
        val etHargaKamar = dialogView.findViewById<EditText>(R.id.etEditHargaKamar)

        val adapterStatus = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            context.resources.getStringArray(R.array.status)
        )
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spStatusKamar.adapter = adapterStatus

        // Isi form dengan data yang ada
        etNamaKamar.setText(kamar.namaKamar)
        spStatusKamar.setSelection(
            context.resources.getStringArray(R.array.status).indexOf(kamar.status)
        )
        etHargaKamar.setText(kamar.harga.toString())

        // Load penghuni data from Firebase
        loadPenghuniData(spPenghuniKamar, kamar.penghuni)

        dialog.setView(dialogView)
            .setTitle("Edit Data Penghuni")
            .setPositiveButton("Simpan") { _, _ ->
                // Pastikan ID tetap sama saat update
                if (kamar.idKamar!!.isNotEmpty()) {
                    val updatedKamar = Kamar(
                        idKamar = kamar.idKamar,
                        namaKamar = etNamaKamar.text.toString(),
                        penghuni = spPenghuniKamar.selectedItem.toString(),
                        status = spStatusKamar.selectedItem.toString(),
                        harga = etHargaKamar.text.toString()
                    )
                    updateKamar(updatedKamar, position)
                } else {
                    Toast.makeText(context, "ID Penghuni tidak ditemukan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }


    private fun updateKamar(kamar: Kamar, position: Int) {
        val myRef = database.getReference("kamar")

        // Update menggunakan ID yang ada
        myRef.child(kamar.idKamar.toString()).setValue(kamar)
            .addOnSuccessListener {
                // Update local list
                kamarList[position] = kamar
                notifyItemChanged(position)
                Toast.makeText(context, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
            }
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

    private fun loadPenghuniData(spPenghuniKamar: Spinner, selectedPenghuni: String?) {
        val penghuniList = mutableListOf<String>()
        val penghuniRef = database.getReference("penghuni")

        penghuniRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { dataSnapshot ->
                val penghuniName = dataSnapshot.child("nama").getValue(String::class.java)
                penghuniName?.let { penghuniList.add(it) }
            }

            val adapterPenghuni =
                ArrayAdapter(context, android.R.layout.simple_spinner_item, penghuniList)
            adapterPenghuni.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spPenghuniKamar.adapter = adapterPenghuni

            selectedPenghuni?.let {
                val position = penghuniList.indexOf(it)
                if (position >= 0) spPenghuniKamar.setSelection(position)
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Gagal memuat data penghuni", Toast.LENGTH_SHORT).show()
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
