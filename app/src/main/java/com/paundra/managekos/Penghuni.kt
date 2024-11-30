package com.paundra.managekos

class Penghuni (
    var id: String? = "",
    var nama: String? = "",
    var nohp: String ?= "",
)
{
    override fun toString(): String {
        return nama ?: "Tidak diketahui" // Tampilkan nama, atau "Tidak diketahui" jika null
    }
}