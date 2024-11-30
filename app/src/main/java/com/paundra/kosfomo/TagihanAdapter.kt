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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TagihanAdapter(
    private val context: Context,
    private val listTagihan: MutableList<ModelTagihan>,
    private val onEditClicked: (ModelTagihan) -> Unit,
    private val onDeleteClicked: (ModelTagihan) -> Unit,
    private val onStatusUpdated: (ModelTagihan) -> Unit, // Tambahkan callback untuk update status
) : RecyclerView.Adapter<TagihanAdapter.TagihanViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagihanViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_tagihan, parent, false)
        return TagihanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagihanViewHolder, position: Int) {
        val tagihan = listTagihan[position]
        holder.bind(tagihan)
    }

    override fun getItemCount(): Int = listTagihan.size

    inner class TagihanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNamaKamar: TextView = itemView.findViewById(R.id.tvNamaKamar)
        private val tvNamaPenghuni: TextView = itemView.findViewById(R.id.tvNamaPenghuni)
        private val tvStatusKamar: TextView = itemView.findViewById(R.id.tvStatusKamar)
        private val tvHargaKamar: TextView = itemView.findViewById(R.id.tvHargaKamar)
        private val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(tagihan: ModelTagihan) {
            // Ambil nama kamar dari database berdasarkan kamarId
            val kamarId = tagihan.kamarId
            if (!kamarId.isNullOrEmpty()) {
                val kamarRef: DatabaseReference = FirebaseDatabase.getInstance("https://fomokos-4320e-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("kamar").child(kamarId)
                kamarRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Ambil data kamar dan konversikan ke ModelKamar
                        val kamar = snapshot.getValue(Kamar::class.java)
                        if (kamar != null && !kamar.namaKamar.isNullOrEmpty()) {
                            tvNamaKamar.text = "Nama Kamar: ${kamar.namaKamar}"
                        } else {
                            tvNamaKamar.text = "Nama Kamar: Tidak Diketahui"
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        tvNamaKamar.text = "Nama Kamar: Gagal Memuat"
                    }
                })
            } else {
                tvNamaKamar.text = "Nama Kamar: Tidak Diketahui"
            }


            tvNamaPenghuni.text = "Nama Penghuni: ${tagihan.namaPenghuni ?: "Tidak Diketahui"}"
            tvStatusKamar.text = "Tanggal Tenggat: ${tagihan.tanggalTenggat ?: "-"}"
            tvHargaKamar.text = "Rp ${tagihan.harga?.toInt() ?: 0}"

            if (tagihan.statusTagihan == "Sudah Bayar") {
                btnEdit.text = "Sudah Bayar"
                btnEdit.setBackgroundColor(android.graphics.Color.parseColor("#3FA34D")) // Green
            } else {
                btnEdit.text = "Belum Bayar"
                btnEdit.setBackgroundColor(android.graphics.Color.parseColor("#FF0000")) // Red
            }

            btnEdit.setOnLongClickListener {
                showStatusUpdateDialog(tagihan)
                true
            }

            btnDelete.setOnClickListener {
                showDeleteConfirmation(tagihan, adapterPosition)
            }
        }


        private fun showStatusUpdateDialog(tagihan: ModelTagihan) {
            val options = arrayOf("Sudah Bayar", "Belum Bayar")
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Ubah Status Pembayaran")
            builder.setItems(options) { dialog, which ->
                tagihan.statusTagihan = if (which == 0) "Sudah Bayar" else "Belum Bayar"
                notifyItemChanged(adapterPosition) // Perbarui tampilan
                onStatusUpdated(tagihan) // Callback untuk memperbarui database
                dialog.dismiss()
            }
            builder.show()
        }
    }
    private fun showDeleteConfirmation(tagihan: ModelTagihan, position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Hapus Data")
            .setMessage("Apakah Anda yakin ingin menghapus data ${tagihan.id}?")
            .setPositiveButton("Ya") { _, _ ->
                onDeleteClicked(tagihan)
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}