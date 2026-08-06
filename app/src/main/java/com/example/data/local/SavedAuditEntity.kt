package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "saved_audits")
data class SavedAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topic: String,
    val videoType: String,
    val language: String,
    val overallHealthScore: Int,
    val titlesJson: String,
    val descriptionSeo: String,
    val hashtagsJson: String,
    val issuesJson: String,
    val quickFixSummaryBn: String,
    val quickFixSummaryEn: String,
    val timestamp: Long = System.currentTimeMillis()
)

class AuditConverters {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value) ?: emptyList()
    }
}
