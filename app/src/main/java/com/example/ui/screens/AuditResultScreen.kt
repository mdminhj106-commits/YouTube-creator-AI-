package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.ui.YtSeoViewModel
import com.example.ui.components.AuditIssueCard
import com.example.ui.components.HashtagSection
import com.example.ui.components.SeoTitleCard
import com.example.ui.theme.YtAmberSecondary
import com.example.ui.theme.YtGreenSuccess
import com.example.ui.theme.YtRedPrimary

@Composable
fun AuditResultScreen(
    viewModel: YtSeoViewModel,
    onNavigateToGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val report by viewModel.auditReportState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isAutoFixing by viewModel.isAutoFixing.collectAsState()
    val isSavedCurrent by viewModel.isSavedCurrent.collectAsState()

    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = YtRedPrimary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "ইউটিউব এসইও ও কন্টেন্ট অডিট করা হচ্ছে...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Gemini AI দ্বারা টাইটেল, ডেসক্রিপশন ও ভুল বিশ্লেষণ হচ্ছে",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    if (report == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "No Data",
                    tint = YtAmberSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "কোনো অডিট রিপোর্ট পাওয়া যায়নি!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToGenerator,
                    colors = ButtonDefaults.buttonColors(containerColor = YtRedPrimary)
                ) {
                    Text("নতুন ভিডিও অডিট শুরু করুন")
                }
            }
        }
        return
    }

    val currReport = report!!
    val isBn = currReport.language == Language.BENGALI

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Health Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Health Gauge Ring
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (currReport.overallHealthScore >= 80) YtGreenSuccess.copy(alpha = 0.15f)
                                    else YtAmberSecondary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${currReport.overallHealthScore}%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = if (currReport.overallHealthScore >= 80) YtGreenSuccess else YtAmberSecondary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isBn) "ইউটিউব এসইও স্কোর" else "YouTube SEO Score",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = if (currReport.overallHealthScore >= 80)
                                    if (isBn) "উৎকৃষ্ট স্বাস্থ্য - আপলোডের জন্য উপযুক্ত" else "Excellent SEO Health"
                                else
                                    if (isBn) "উন্নয়ন প্রয়োজন - ভুলগুলো ঠিক করুন" else "Needs Improvement",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Save Button
                    IconButton(
                        onClick = { viewModel.saveCurrentAudit() },
                        modifier = Modifier.testTag("save_audit_button")
                    ) {
                        Icon(
                            imageVector = if (isSavedCurrent) Icons.Default.BookmarkAdded else Icons.Default.Bookmark,
                            contentDescription = "Save",
                            tint = if (isSavedCurrent) YtGreenSuccess else YtRedPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Auto-Fix All Errors Primary Button
                Button(
                    onClick = { viewModel.runAudit(isAutoFix = true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("auto_fix_all_button"),
                    enabled = !isAutoFixing,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YtGreenSuccess,
                        contentColor = Color.White
                    )
                ) {
                    if (isAutoFixing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "ভুল সংশোধন করা হচ্ছে..." else "Auto-fixing all issues...",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoFixHigh,
                            contentDescription = "Auto Fix",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "ভুল সংশোধন করুন (Auto-Fix All Errors)" else "Auto-Fix All Errors",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                // Quick Fix summary note
                if (currReport.quickFixSummaryBn.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✨ ${if (isBn) currReport.quickFixSummaryBn else currReport.quickFixSummaryEn}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = YtGreenSuccess
                    )
                }
            }
        }

        // Tabs
        val tabs = listOf(
            if (isBn) "টাইটেল (${currReport.seoTitles.size})" else "Titles (${currReport.seoTitles.size})",
            if (isBn) "ভুল ও অডিট (${currReport.detectedIssues.size})" else "Issues (${currReport.detectedIssues.size})",
            if (isBn) "ডেসক্রিপশন" else "Description",
            if (isBn) "হ্যাশট্যাগ" else "Hashtags"
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = YtRedPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        )
                    },
                    modifier = Modifier.testTag("result_tab_$index")
                )
            }
        }

        // Tab Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // SEO Titles Tab
                    Text(
                        text = if (isBn) "🔥 উচ্চ সি-টি-আর এসইও টাইটেল তালিকা:" else "🔥 High-CTR SEO Title Options:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    currReport.seoTitles.forEachIndexed { idx, titleSuggestion ->
                        SeoTitleCard(suggestion = titleSuggestion, index = idx + 1)
                    }
                }

                1 -> {
                    // Audit Issues & Defects Tab
                    Text(
                        text = if (isBn) "⚠️ শনাক্তকৃত ভিডিও সমস্যা ও সংশোধন গাইড:" else "⚠️ Detected Video Defects & Fix Steps:",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (currReport.detectedIssues.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = YtGreenSuccess.copy(alpha = 0.1f))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "OK", tint = YtGreenSuccess)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isBn) "দারুণ! আপনার ভিডিও কন্টেন্টে কোনো বড় ভুল পাওয়া যায়নি।" else "Great! No major video defects found.",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    } else {
                        currReport.detectedIssues.forEach { issue ->
                            AuditIssueCard(issue = issue, language = currReport.language)
                        }
                    }
                }

                2 -> {
                    // SEO Description Tab
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "📝 সম্পূর্ণ এসইও ডেসক্রিপশন:" else "📝 Formatted SEO Description:",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("YouTube Description", currReport.seoDescription)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "ডেসক্রিপশন কপি হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = YtRedPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("copy_description_button")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("কপি করুন", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = currReport.seoDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                3 -> {
                    // Hashtags Tab
                    HashtagSection(hashtags = currReport.hashtags)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bottom Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToGenerator,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "New", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "নতুন অডিট" else "New Audit")
                }

                Button(
                    onClick = {
                        viewModel.saveCurrentAudit()
                        Toast.makeText(context, "অডিট সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = YtRedPrimary)
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "সেভ করুন" else "Save Audit")
                }
            }
        }
    }
}
