package com.example.data.local

import com.example.data.AuditReport
import com.example.data.HashtagsGroup
import com.example.data.Language
import com.example.data.SeoTitleSuggestion
import com.example.data.VideoAuditIssue
import com.example.data.VideoType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuditRepository(private val auditDao: AuditDao) {
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    val allSavedAudits: Flow<List<AuditReport>> = auditDao.getAllAudits().map { entities ->
        entities.map { entity -> entity.toAuditReport(moshi) }
    }

    suspend fun saveAudit(report: AuditReport): Long {
        val entity = report.toEntity(moshi)
        return auditDao.insertAudit(entity)
    }

    suspend fun deleteAudit(id: Long) {
        auditDao.deleteAuditById(id)
    }

    suspend fun clearAll() {
        auditDao.clearAll()
    }

    private fun AuditReport.toEntity(moshi: Moshi): SavedAuditEntity {
        val titleListAdapter = moshi.adapter<List<SeoTitleSuggestion>>(
            Types.newParameterizedType(List::class.java, SeoTitleSuggestion::class.java)
        )
        val hashtagsAdapter = moshi.adapter(HashtagsGroup::class.java)
        val issuesAdapter = moshi.adapter<List<VideoAuditIssue>>(
            Types.newParameterizedType(List::class.java, VideoAuditIssue::class.java)
        )

        val topic = seoTitles.firstOrNull()?.title ?: "YouTube Video SEO"

        return SavedAuditEntity(
            topic = topic,
            videoType = videoType.name,
            language = language.name,
            overallHealthScore = overallHealthScore,
            titlesJson = titleListAdapter.toJson(seoTitles),
            descriptionSeo = seoDescription,
            hashtagsJson = hashtagsAdapter.toJson(hashtags),
            issuesJson = issuesAdapter.toJson(detectedIssues),
            quickFixSummaryBn = quickFixSummaryBn,
            quickFixSummaryEn = quickFixSummaryEn,
            timestamp = timestamp
        )
    }

    private fun SavedAuditEntity.toAuditReport(moshi: Moshi): AuditReport {
        val titleListAdapter = moshi.adapter<List<SeoTitleSuggestion>>(
            Types.newParameterizedType(List::class.java, SeoTitleSuggestion::class.java)
        )
        val hashtagsAdapter = moshi.adapter(HashtagsGroup::class.java)
        val issuesAdapter = moshi.adapter<List<VideoAuditIssue>>(
            Types.newParameterizedType(List::class.java, VideoAuditIssue::class.java)
        )

        val vType = try { VideoType.valueOf(videoType) } catch (e: Exception) { VideoType.SHORTS }
        val lang = try { Language.valueOf(language) } catch (e: Exception) { Language.BENGALI }

        val titles = try { titleListAdapter.fromJson(titlesJson) ?: emptyList() } catch (e: Exception) { emptyList() }
        val tags = try { hashtagsAdapter.fromJson(hashtagsJson) ?: HashtagsGroup() } catch (e: Exception) { HashtagsGroup() }
        val issues = try { issuesAdapter.fromJson(issuesJson) ?: emptyList() } catch (e: Exception) { emptyList() }

        return AuditReport(
            overallHealthScore = overallHealthScore,
            videoType = vType,
            language = lang,
            seoTitles = titles,
            seoDescription = descriptionSeo,
            hashtags = tags,
            detectedIssues = issues,
            quickFixSummaryBn = quickFixSummaryBn,
            quickFixSummaryEn = quickFixSummaryEn,
            timestamp = timestamp
        )
    }
}
