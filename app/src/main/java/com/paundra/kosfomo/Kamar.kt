package com.paundra.kosfomo

class Kamar(
   var idKamar: String? = "",
   var namaKamar: String? = "",
   var penghuni: String? = "",
   var status: String? = "",
   var harga: String? = ""
)
{
   override fun toString(): String {
      return namaKamar ?: "Tidak diketahui" // Tampilkan nama, atau "Tidak diketahui" jika null
   }
}