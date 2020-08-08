package ru.sinura.hackaton.repo.retrofit.models

import com.squareup.moshi.Json
import retrofit2.http.Field

data class NewsModel (
    @field:Json("status") val status: String,
    @field:Json("News") val data: Array<Array<String>>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewsModel

        if (status != other.status) return false
        if (!data.contentDeepEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + data.contentDeepHashCode()
        return result
    }
}