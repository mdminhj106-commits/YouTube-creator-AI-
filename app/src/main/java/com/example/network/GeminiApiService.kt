package com.example.network

import com.example.BuildConfig
import com.example.data.AuditReport
import com.example.data.HashtagsGroup
import com.example.data.IssueSeverity
import com.example.data.Language
import com.example.data.SeoTitleSuggestion
import com.example.data.VideoAuditIssue
import com.example.data.VideoType
import com.example.data.YtInputData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    suspend fun analyzeAndAuditVideo(
        input: YtInputData,
        isAutoFix: Boolean = false
    ): AuditReport = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        
        val languageInstruction = if (input.language == Language.BENGALI) {
            "Provide all title suggestions, explanations, descriptions, and audit feedback in natural, appealing Bangla (বাংলা) language with accurate English technical SEO terms where appropriate."
        } else {
            "Provide all title suggestions, explanations, descriptions, and audit feedback in crisp, clear English."
        }

        val videoTypeNote = if (input.videoType == VideoType.SHORTS) {
            "This is a YouTube SHORTS (<60 seconds). Focus heavily on viral hooks, fast-paced retention, mobile readability, curiosity triggers, and #Shorts tags."
        } else {
            "This is a YouTube LONG VIDEO (>1 minute). Focus heavily on YouTube search ranking, detailed structured description with timestamps template, high-volume search keywords, CTR titles, and channel subscriber conversion."
        }

        val autoFixNote = if (isAutoFix) {
            "This request is an AUTO-FIX operation. Correct all previously detected mistakes and generate pristine, error-free, highly optimized title, description, and hashtags package."
        } else ""

        val prompt = """
            You are YouTube Studio AI, an expert YouTube SEO Algorithm Specialist, Channel Growth Strategist, and Video Content Auditor.
            
            Perform a thorough YouTube Video SEO optimization and Content Defect Audit for the following video submission:
            
            - Video Format: ${input.videoType.displayNameEn}
            - Primary Language: ${input.language.displayNameEn}
            - Topic / Main Idea: "${input.topic}"
            - Draft Title: "${input.draftTitle.ifBlank { "None provided" }}"
            - Target Keywords: "${input.targetKeywords.ifBlank { "Suggest relevant high-traffic keywords" }}"
            - Draft Description: "${input.draftDescription.ifBlank { "None provided" }}"
            - Target Audience: "${input.targetAudience.ifBlank { "General YouTube Viewers" }}"
            - Script / Key Points Summary: "${input.scriptSummary.ifBlank { "None provided" }}"
            
            $languageInstruction
            $videoTypeNote
            $autoFixNote
            
            You MUST return a JSON object strictly adhering to this structure (no extra text, no markdown wrapping, pure JSON):
            {
              "overallHealthScore": 85,
              "seoTitles": [
                {
                  "title": "Optimized viral title string",
                  "ctrScore": 92,
                  "charCount": 58,
                  "hookType": "Curiosity / Keyword Hook",
                  "reasoning": "Reason why this title ranks high and gets clicks"
                }
              ],
              "seoDescription": "Full structured YouTube video description with intro hook, summary, key points/timestamps placeholder, relevant keywords, and subscriber call-to-action.",
              "hashtags": {
                "viralShortsTags": ["#Shorts", "#YouTubeShorts", "#ViralShorts"],
                "highSearchTags": ["#Keyword1", "#Keyword2"],
                "nicheTags": ["#NicheTag1", "#NicheTag2"]
              },
              "detectedIssues": [
                {
                  "titleBn": "শিরোনামে প্রাইমারি কিওয়ার্ড অনুপস্থিত",
                  "titleEn": "Primary keyword missing in title",
                  "severity": "CRITICAL",
                  "descriptionBn": "আপনার খসড়া শিরোনামটিতে কোনো মূল অনুসন্ধান কিওয়ার্ড নেই, যা ইউটিউব সার্চ র‍্যাঙ্কিং কমাবে।",
                  "descriptionEn": "Your draft title lacks high-volume search keywords, lowering YouTube search discoverability.",
                  "fixInstructionBn": "প্রস্তাবিত এসইও ফ্রেন্ডলি ১ নম্বর শিরোনামটি ব্যবহার করুন।",
                  "fixInstructionEn": "Use the recommended SEO optimized title #1."
                }
              ],
              "quickFixSummaryBn": "শিরোনাম এবং ডেসক্রিপশনে উচ্চ সার্চ ভলিউম কিওয়ার্ড যুক্ত করা হয়েছে এবং হ্যাশট্যাগ নিখুঁত করা হয়েছে।",
              "quickFixSummaryEn": "Added high-volume keywords into title & description and optimized hashtag package."
            }
            
            Provide exactly 5 distinct, high-CTR title variations. Provide 3 to 5 detected video/metadata issues with severity CRITICAL, WARNING, or INFO.
        """.trimIndent()

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("responseMimeType", "application/json")
            })
        }

        val requestBody = jsonRequest.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
        val url = "$BASE_URL?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            val responseText = response.body?.string() ?: throw Exception("Empty response from Gemini API")

            if (!response.isSuccessful) {
                val errObj = try { JSONObject(responseText) } catch (e: Exception) { null }
                val errMsg = errObj?.optJSONObject("error")?.optString("message") ?: "HTTP error ${response.code}"
                throw Exception("Gemini Error: $errMsg")
            }

            parseGeminiJsonResponse(responseText, input.videoType, input.language)
        } catch (e: Exception) {
            e.printStackTrace()
            // Return fallback intelligent audit report if API key is invalid/missing or network failure
            createFallbackAuditReport(input, e.message ?: "Network / API error")
        }
    }

    private fun parseGeminiJsonResponse(
        rawJsonResponse: String,
        videoType: VideoType,
        language: Language
    ): AuditReport {
        val rootObj = JSONObject(rawJsonResponse)
        val candidates = rootObj.optJSONArray("candidates")
        val candidate = candidates?.optJSONObject(0)
        val content = candidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val jsonText = parts?.optJSONObject(0)?.optString("text") ?: ""

        val cleanedJson = jsonText.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val auditObj = JSONObject(cleanedJson)

        val healthScore = auditObj.optInt("overallHealthScore", 80)
        
        // Titles
        val titlesList = mutableListOf<SeoTitleSuggestion>()
        val titlesArray = auditObj.optJSONArray("seoTitles")
        if (titlesArray != null) {
            for (i in 0 until titlesArray.length()) {
                val tObj = titlesArray.optJSONObject(i) ?: continue
                titlesList.add(
                    SeoTitleSuggestion(
                        title = tObj.optString("title", "YouTube Video Title ${i + 1}"),
                        ctrScore = tObj.optInt("ctrScore", 88 + i),
                        charCount = tObj.optInt("charCount", tObj.optString("title").length),
                        hookType = tObj.optString("hookType", "SEO Hook"),
                        reasoning = tObj.optString("reasoning", "Optimized for YouTube search and clicks.")
                    )
                )
            }
        }

        // Description
        val descriptionSeo = auditObj.optString("seoDescription", "YouTube Video Description")

        // Hashtags
        val tagsObj = auditObj.optJSONObject("hashtags")
        val viralShorts = parseJsonStringArray(tagsObj?.optJSONArray("viralShortsTags"))
        val highSearch = parseJsonStringArray(tagsObj?.optJSONArray("highSearchTags"))
        val niche = parseJsonStringArray(tagsObj?.optJSONArray("nicheTags"))

        val hashtagsGroup = HashtagsGroup(
            viralShortsTags = viralShorts,
            highSearchTags = highSearch,
            nicheTags = niche
        )

        // Issues
        val issuesList = mutableListOf<VideoAuditIssue>()
        val issuesArray = auditObj.optJSONArray("detectedIssues")
        if (issuesArray != null) {
            for (i in 0 until issuesArray.length()) {
                val iObj = issuesArray.optJSONObject(i) ?: continue
                val sevStr = iObj.optString("severity", "WARNING").uppercase()
                val severity = try { IssueSeverity.valueOf(sevStr) } catch (e: Exception) { IssueSeverity.WARNING }

                issuesList.add(
                    VideoAuditIssue(
                        titleBn = iObj.optString("titleBn", "ভিডিও ফরম্যাট অডিট"),
                        titleEn = iObj.optString("titleEn", "Video Format Audit"),
                        severity = severity,
                        descriptionBn = iObj.optString("descriptionBn", "ডেসক্রিপশন এবং টাইটেল উন্নত করতে হবে।"),
                        descriptionEn = iObj.optString("descriptionEn", "Title and description need optimization."),
                        fixInstructionBn = iObj.optString("fixInstructionBn", "এসইও পরামর্শ অনুযায়ী সংশোধন করুন।"),
                        fixInstructionEn = iObj.optString("fixInstructionEn", "Apply SEO fixes.")
                    )
                )
            }
        }

        val quickFixBn = auditObj.optString("quickFixSummaryBn", "অটোমেটিক এসইও সংশোধন সম্পন্ন হয়েছে।")
        val quickFixEn = auditObj.optString("quickFixSummaryEn", "Automatic SEO fixes applied successfully.")

        return AuditReport(
            overallHealthScore = healthScore,
            videoType = videoType,
            language = language,
            seoTitles = titlesList,
            seoDescription = descriptionSeo,
            hashtags = hashtagsGroup,
            detectedIssues = issuesList,
            quickFixSummaryBn = quickFixBn,
            quickFixSummaryEn = quickFixEn
        )
    }

    private fun parseJsonStringArray(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val str = array.optString(i)
            if (str.isNotBlank()) list.add(str)
        }
        return list
    }

    private fun createFallbackAuditReport(input: YtInputData, errorMsg: String): AuditReport {
        val isShorts = input.videoType == VideoType.SHORTS
        val isBn = input.language == Language.BENGALI

        val sampleTopic = input.topic.ifBlank { "YouTube Video" }
        
        val fallbackTitles = listOf(
            SeoTitleSuggestion(
                title = if (isBn) "$sampleTopic | সহজ গাইড ২০২৬" else "$sampleTopic | Complete Guide 2026",
                ctrScore = 92,
                charCount = sampleTopic.length + 18,
                hookType = "High CTR Curiosity",
                reasoning = "Combines high search intent with current year relevance."
            ),
            SeoTitleSuggestion(
                title = if (isBn) "কীভাবে $sampleTopic করবেন? (১ মিনিটে শিখুন)" else "How to $sampleTopic in 1 Minute!",
                ctrScore = 89,
                charCount = sampleTopic.length + 22,
                hookType = "Actionable Question",
                reasoning = "Prompts instant clicks with fast value promise."
            ),
            SeoTitleSuggestion(
                title = if (isBn) "$sampleTopic তৈরির সেরা ৩টি উপায়!" else "Top 3 Secrets for $sampleTopic",
                ctrScore = 86,
                charCount = sampleTopic.length + 20,
                hookType = "Listicle Hook",
                reasoning = "Numbers in YouTube titles boost CTR by up to 30%."
            )
        )

        val fallbackDesc = if (isBn) {
            """
                📌 $sampleTopic সম্পর্কে বিস্তারিত আলোচনা ও টিপস।
                
                ভিডিওতে যা যা থাকছে:
                00:00 - মূল ভূমিকা ও গুরুত্বপূর্ণ টিপস
                00:30 - স্টেপ বাই স্টেপ টিউটোরিয়াল
                01:00 - সেরা প্র্যাকটিস ও চ্যানেল সাবস্ক্রাইব
                
                🔔 নতুন এমন আকর্ষণীয় ভিডিও পেতে চ্যানেলটি সাবস্ক্রাইব (Subscribe) করে সাথেই থাকুন!
                
                সম্পর্কিত অনুসন্ধানী শব্দসমূহ (Related Keywords):
                #${sampleTopic.replace(" ", "")} #YouTubeSEO #BanglaTech
            """.trimIndent()
        } else {
            """
                📌 Everything you need to know about $sampleTopic.
                
                Video Highlights & Chapters:
                00:00 - Introduction & Hook
                00:30 - Step by Step Tutorial
                01:00 - Pro Tips & Wrap Up
                
                🔔 Subscribe to the channel for more valuable content!
                
                Related Search Terms:
                #${sampleTopic.replace(" ", "")} #YouTubeSEO #Shorts
            """.trimIndent()
        }

        val fallbackTags = HashtagsGroup(
            viralShortsTags = if (isShorts) listOf("#Shorts", "#YouTubeShorts", "#ViralShorts", "#TrendingShorts") else emptyList(),
            highSearchTags = listOf("#${sampleTopic.replace(" ", "")}", "#YouTubeSEO", "#ViralVideo"),
            nicheTags = listOf("#ContentCreator", "#YouTubeTips", "#SEO2026")
        )

        val fallbackIssues = listOf(
            VideoAuditIssue(
                titleBn = "খসড়া টাইটেল দৈর্ঘ্য পরীক্ষা",
                titleEn = "Draft Title Length Check",
                severity = IssueSeverity.WARNING,
                descriptionBn = "টাইটেল ৬০ অক্ষরের মধ্যে রাখা ইউটিউব মোবাইল স্ক্রিনে প্রদর্শনের জন্য সেরা।",
                descriptionEn = "Keeping titles under 60 characters prevents text truncation on mobile YouTube.",
                fixInstructionBn = "প্রস্তাবিত ১ নম্বর ছোট শিরোনামটি বেছে নিন।",
                fixInstructionEn = "Select recommended short title option #1."
            ),
            VideoAuditIssue(
                titleBn = "ডেসক্রিপশনের প্রথম ২০০ অক্ষর রিটেনশন অডিট",
                titleEn = "Description First 200 Characters Audit",
                severity = IssueSeverity.INFO,
                descriptionBn = "ইউটিউব সার্চের জন্য ডেসক্রিপশনের প্রথম ২ লাইনে প্রধান কিওয়ার্ড থাকা জরুরি।",
                descriptionEn = "Include primary target keywords in the first 2 lines for YouTube search indexing.",
                fixInstructionBn = "প্রস্তাবিত ডেসক্রিপশনটি কপি করে ব্যবহার করুন।",
                fixInstructionEn = "Copy the AI-generated structured description."
            )
        )

        return AuditReport(
            overallHealthScore = 78,
            videoType = input.videoType,
            language = input.language,
            seoTitles = fallbackTitles,
            seoDescription = fallbackDesc,
            hashtags = fallbackTags,
            detectedIssues = fallbackIssues,
            quickFixSummaryBn = "অফলাইন/ফলব্যাক মোডে প্রয়োজনীয় এসইও টাইটেল ও হ্যাশট্যাগ তৈরি করা হয়েছে। (API Note: $errorMsg)",
            quickFixSummaryEn = "Generated fallback SEO package. (API Note: $errorMsg)"
        )
    }
}
