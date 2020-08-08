package ru.sinura.hackaton.repo.retrofit.models

data class RecepModel (
    val id_reception: Int,
    val id_hospital: Int,
    val fio: String,
    val date: Long,
    val num_cabinet: Int,
    val reserved: Boolean
)