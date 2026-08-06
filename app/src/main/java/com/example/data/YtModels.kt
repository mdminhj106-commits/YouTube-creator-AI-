package com.example.data

enum class VideoType(val displayNameBn: String, val displayNameEn: String) {
    SHORTS("ইউটিউব শর্টস (< ৬০ সেকেন্ড)", "YouTube Shorts (< 60s)"),
    LONG_VIDEO("লং ভিডিও (> ১ মিনিট)", "Long Video (> 1 min)")
}

enum class Language(val displayNameBn: String, val displayNameEn: String, val code: String) {
    BENGALI("বাংলা (Bengali)", "Bengali", "bn"),
    ENGLISH("ইংরেজি (English)", "English", "en")
}

enum class IssueSeverity(val labelBn: String, val labelEn: String) {
    CRITICAL("গুরুত্বপূর্ণ সমস্যা", "Critical Error"),
    WARNING("সতর্কতা / উন্নয়ন প্রয়োজন", "Warning / Needs Improvement"),
    INFO("পরামর্শ", "Suggestion")
}

data class YtInputData(
    val topic: String,
    val draftTitle: String = "",
    val targetKeywords: String = "",
    val draftDescription: String = "",
    val videoType: VideoType = VideoType.SHORTS,
    val language: Language = Language.BENGALI,
    val targetAudience: String = "",
    val scriptSummary: String = ""
)

data class SeoTitleSuggestion(
    val title: String,
    val ctrScore: Int, // 1 to 100
    val charCount: Int,
    val hookType: String, // e.g. "Curiosity Hook", "Keyword Packed", "Question Hook"
    val reasoning: String
)

data class HashtagsGroup(
    val viralShortsTags: List<String> = emptyList(),
    val highSearchTags: List<String> = emptyList(),
    val nicheTags: List<String> = emptyList()
)

data class VideoAuditIssue(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titleBn: String,
    val titleEn: String,
    val severity: IssueSeverity,
    val descriptionBn: String,
    val descriptionEn: String,
    val fixInstructionBn: String,
    val fixInstructionEn: String
)

data class AuditReport(
    val overallHealthScore: Int, // 0 - 100
    val videoType: VideoType,
    val language: Language,
    val seoTitles: List<SeoTitleSuggestion>,
    val seoDescription: String,
    val hashtags: HashtagsGroup,
    val detectedIssues: List<VideoAuditIssue>,
    val quickFixSummaryBn: String,
    val quickFixSummaryEn: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class PresetTemplate(
    val titleBn: String,
    val titleEn: String,
    val inputData: YtInputData
)

object YtPresets {
    val presets = listOf(
        PresetTemplate(
            titleBn = "রান্নার আইটেম (শর্টস)",
            titleEn = "Bengali Cooking Recipe (Shorts)",
            inputData = YtInputData(
                topic = "১০ মিনিটে সুস্বাদু চিকেন ফ্রাই রেসিপি",
                draftTitle = "চিকেন ফ্রাই তৈরি করার নিয়ম",
                targetKeywords = "chicken fry recipe, bangladeshi street food, easy recipe, chicken shorts",
                draftDescription = "সহজে বাড়িতে বানিয়ে ফেলুন ক্রিসপি চিকেন ফ্রাই। একদম রেস্টুরেন্ট স্টাইল স্বাদ!",
                videoType = VideoType.SHORTS,
                language = Language.BENGALI,
                targetAudience = "Food lovers, Bangladeshi home cooks"
            )
        ),
        PresetTemplate(
            titleBn = "টেক রিভিউ (লং ভিডিও)",
            titleEn = "Tech & Smartphone Review (Long Video)",
            inputData = YtInputData(
                topic = "কম বাজেটের সেরা স্মার্টফোন ২০২৬",
                draftTitle = "Best Smartphone under 15000 BDT in 2026",
                targetKeywords = "best budget phone, smartphone review, bangla tech review, top phones 2026",
                draftDescription = "২০২৬ সালে ১৫,০০০ টাকার মধ্যে সেরা ৩টি মোবাইল ফোন। ক্যামেরা, ব্যাটারি এবং গেমিং পারফরম্যান্সের বিস্তারিত রিভিউ!",
                videoType = VideoType.LONG_VIDEO,
                language = Language.BENGALI,
                targetAudience = "Tech enthusiasts, Smartphone buyers"
            )
        ),
        PresetTemplate(
            titleBn = "গেমিং হাইলাইট (শর্টস)",
            titleEn = "Gaming Clutch Highlight (Shorts)",
            inputData = YtInputData(
                topic = "PUBG / Free Fire 1v4 Solo vs Squad Clutch",
                draftTitle = "Free Fire 1v4 Epic Clutch Moments",
                targetKeywords = "free fire clutch, pubg mobile shorts, gaming shorts, 1v4 clutch bangla",
                draftDescription = "Crazy 1v4 squad wipe moment! Watch till the end for the unexpected grenade trick!",
                videoType = VideoType.SHORTS,
                language = Language.BENGALI,
                targetAudience = "Gamers, Free Fire & PUBG fans"
            )
        ),
        PresetTemplate(
            titleBn = "অনলাইন ইনকাম গাইড (লং ভিডিও)",
            titleEn = "Online Freelancing Guide (Long Video)",
            inputData = YtInputData(
                topic = "ছাত্রছাত্রীদের জন্য অনলাইন থেকে আয় করার সহজ উপায়",
                draftTitle = "How to start freelancing for beginners in Bangla",
                targetKeywords = "online income bangla, freelancing for students, make money online, digital skills",
                draftDescription = "কোন অভিজ্ঞতা ছাড়াই ছাত্র অবস্থায় ফ্রিল্যান্সিং শুরু করার সম্পূর্ণ গাইডলাইন। স্টেপ বাই স্টেপ টিউটোরিয়াল!",
                videoType = VideoType.LONG_VIDEO,
                language = Language.BENGALI,
                targetAudience = "Students, Freelancers, Job seekers"
            )
        )
    )
}
