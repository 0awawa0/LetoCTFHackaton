package ru.sinura.hackaton.repo.retrofit.models

data class ReceptionsModel (
    val status: String,
    val receps: Array<RecepModel>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ReceptionsModel

        if (status != other.status) return false
        if (!receps.contentEquals(other.receps)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + receps.contentHashCode()
        return result
    }
}