package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YtAmberSecondary
import com.example.ui.theme.YtGreenSuccess
import com.example.ui.theme.YtRedPrimary

@Composable
fun GuideScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "📚 ইউটিউব অ্যালগোরিদম ও এসইও মাস্টার গাইড",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "ইউটিউব শর্টস ও লং ভিডিও দ্রুত ভাইরাল ও সার্চ র‍্যাঙ্ক করার গোপন টিপস",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Guide Card 1: Shorts Formula
        GuideTipCard(
            icon = Icons.Default.PlayCircle,
            title = "📱 ইউটিউব শর্টস (Shorts) ভাইরাল করার ৫ টিপস",
            color = YtRedPrimary,
            tips = listOf(
                "প্রথম ৩ সেকেন্ডের হুক (Hook): দর্শকরা স্ক্রোল করে চলে যাওয়ার আগেই প্রথম ৩ সেকেন্ডে চমকপ্রদ প্রশ্ন বা দৃশ্য দেখান।",
                "লুপিং টেকনিক (Looping): ভিডিওর শেষ লাইন এমনভাবে শেষ করুন যাতে আবার প্রথম লাইনের সাথে মিলে যায়, এতে ভিউ রিটেনশন ১০০%+ হয়।",
                "অডিও ও ট্রপিক মিউজিক: ট্রেন্ডিং সাউন্ড ও ব্যাকগ্রাউন্ড ট্র‍্যাক ব্যবহার করলে অ্যালগোরিদম ভিডিও পুশ করে।",
                "৩-৫ টি নিখুঁত হ্যাশট্যাগ: ডেসক্রিপশনে অবশ্যই #Shorts, #YouTubeShorts এবং নিশের ২টি হ্যাশট্যাগ ব্যবহার করুন।",
                "বুলস আই টেক্সট অন স্ক্রিন: মোবাইল স্ক্রিনে সহজে পড়া যায় এমন বোল্ড টেক্সট ও ক্যাপশন যোগ করুন।"
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Guide Card 2: Long Video Ranking
        GuideTipCard(
            icon = Icons.Default.TrendingUp,
            title = "🚀 লং ভিডিও (Long Video) সার্চ র‍্যাঙ্কিং ফর্মুলা",
            color = YtAmberSecondary,
            tips = listOf(
                "৬০ অক্ষরের টাইটেল: টাইটেলের প্রথম ৫০-৬০ অক্ষরের মধ্যে প্রধান টার্গেট কিওয়ার্ড রাখুন যাতে মোবাইলে কেটে না যায়।",
                "প্রথম ২০০ অক্ষরের এসইও ডেসক্রিপশন: ইউটিউব অনুসন্ধানের জন্য ডেসক্রিপশনের প্রথম ২ লাইনে কিওয়ার্ড যুক্ত করুন।",
                "উচ্চ সি-টি-আর থাম্বনেইল (High CTR Thumbnail): উজ্জ্বল হাই-কন্ট্রাস্ট কালার, ফেস এক্সপ্রেশন এবং ৩ শব্দের বেশি টেক্সট ছাড়া থাম্বনেইল বানান।",
                "টাইমস্ট্যাম্প ও চ্যাপ্টার (Timestamps): ডেসক্রিপশনে 00:00 ফরম্যাটে চ্যাপ্টার দিলে গুগল ও ইউটিউব সার্চে দ্রুত আসে।",
                "অডিয়েন্স এনগেজমেন্ট প্রম্পট: ভিডিওর ৩-৪ মিনিটের মাথায় প্রশ্ন জিজ্ঞাসা করে কমেন্ট করতে উৎসাহিত করুন।"
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Guide Card 3: Copyright & Policy Check
        GuideTipCard(
            icon = Icons.Default.Shield,
            title = "🛡️ কপিরাইট ও কম্যুনিটি গাইডলাইন সতর্কতা",
            color = YtGreenSuccess,
            tips = listOf(
                "কপিরাইট ফ্রি অডিও: সর্বদা YouTube Audio Library বা নিজস্ব অরিজিনাল সাউন্ড ব্যবহার করুন।",
                "মিছলিডিং মেটাডেটা এড়ান: থাম্বনেইল বা টাইটেলে যা নেই, কেবল ক্লিকবাইটের জন্য মিথ্যা তথ্য দিলে চ্যানেল পেনাল্টি পেতে পারে।",
                "রি-ইউজড কন্টেন্ট (Reused Content): অন্যের ভিডিও হুবহু কপি না করে নিজের ভয়েসওভার বা কমেন্ট্রি যোগ করুন।"
            )
        )
    }
}

@Composable
fun GuideTipCard(
    icon: ImageVector,
    title: String,
    color: Color,
    tips: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = "Icon", tint = color, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            tips.forEachIndexed { idx, tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = color)
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp, lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
