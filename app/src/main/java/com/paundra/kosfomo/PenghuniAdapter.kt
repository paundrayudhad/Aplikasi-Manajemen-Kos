package com.paundra.kosfomo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PenghuniAdapter(private val penghuniList: List<Penghuni>) : RecyclerView.Adapter<PenghuniAdapter.PenghuniViewHolder>() {

    class PenghuniViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaPenghuni: TextView = itemView.findViewById(R.id.tvNamaPenghuni)
        val tvKontakPenghuni: TextView = itemView.findViewById(R.id.tvKontakPenghuni)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PenghuniViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_penghuni, parent, false)
        return PenghuniViewHolder(view)
    }

    override fun onBindViewHolder(holder: PenghuniViewHolder, position: Int) {
        val penghuni = penghuniList[position]
        holder.tvNamaPenghuni.text = penghuni.nama
        holder.tvKontakPenghuni.text = penghuni.nohp
    }

    override fun getItemCount(): Int {
        return penghuniList.size
    }
}