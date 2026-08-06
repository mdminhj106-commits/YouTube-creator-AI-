package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Language
import com.example.data.VideoType
import com.example.data.YtPresets
import com.example.ui.YtSeoViewModel
import com.example.ui.components.HeaderBanner
import com.example.ui.theme.YtRedPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: YtSeoViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputState by viewModel.inputState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()
    val isBn = inputState.language == Language.BENGALI

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
    ) {
        HeaderBanner(
            title = if (isBn) "টিউব এসইও ও অডিট AI" else "YT SEO & Video Audit AI",
            subtitle = if (isBn) "অটোমেটিক এসইও টাইটেল, ডেসক্রিপশন, হ্যাশট্যাগ ও ভিডিও ভুল ডিটেকশন" else "AI Title, Description, Hashtags & Video Defect Audit"
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            
            // Format Selector (Shorts vs Long Video)
            Text(
                text = if (isBn) "১. ভিডিও এর ফরম্যাট সিলেক্ট করুন:" else "1. Select Video Format:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VideoType.values().forEach { type ->
                    val isSelected = inputState.videoType == type
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.updateVideoType(type) }
                            .testTag("format_card_${type.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) YtRedPrimary else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) null else CardDefaults.outlinedCardBorder()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isBn) type.displayNameBn else type.displayNameEn,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Language Selector
            Text(
                text = if (isBn) "২. ভাষা নির্বাচন করুন (Language):" else "2. Select Language:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Language.values().forEach { lang ->
                    val isSelected = inputState.language == lang
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.updateLanguage(lang) }
                            .testTag("language_card_${lang.name}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = if (isSelected) YtRedPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) lang.displayNameBn else lang.displayNameEn,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Presets
            Text(
                text = if (isBn) "💡 তৈরি টেমপ্লেট দিয়ে চেষ্টা করুন (Presets):" else "💡 Try Sample Presets:",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                YtPresets.presets.forEachIndexed { idx, preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.applyPreset(preset) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("preset_chip_$idx")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Preset",
                                tint = YtRedPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) preset.titleBn else preset.titleEn,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Topic / Title Input
            OutlinedTextField(
                value = inputState.topic,
                onValueChange = { viewModel.updateTopic(it) },
                label = { Text(if (isBn) "ভিডিও এর বিষয় / খসড়া টাইটেল *" else "Video Topic or Draft Title *") },
                placeholder = { Text(if (isBn) "যেমন: সুস্বাদু চিকেন ফ্রাই তৈরি" else "e.g., Best Budget Smartphone 2026") },
                leadingIcon = { Icon(Icons.Default.Title, contentDescription = "Topic", tint = YtRedPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("topic_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YtRedPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Keywords Input
            OutlinedTextField(
                value = inputState.targetKeywords,
                onValueChange = { viewModel.updateTargetKeywords(it) },
                label = { Text(if (isBn) "টার্গেট কিওয়ার্ড (কোমা দিয়ে লিখুন)" else "Target Keywords (comma separated)") },
                placeholder = { Text("chicken recipe, easy cooking, street food") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = "Keywords", tint = YtRedPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("keywords_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YtRedPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description Input
            OutlinedTextField(
                value = inputState.draftDescription,
                onValueChange = { viewModel.updateDraftDescription(it) },
                label = { Text(if (isBn) "খসড়া ডেসক্রিপশন (ঐচ্ছিক)" else "Draft Description (Optional)") },
                placeholder = { Text(if (isBn) "ভিডিও সম্পর্কে সংক্ষিপ্ত বিবরণ লিখুন..." else "Brief description of video content...") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = "Description", tint = YtRedPrimary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("description_input_field"),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YtRedPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error Message
            AnimatedVisibility(visible = errorMessage != null) {
                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // Run Audit Button
            Button(
                onClick = {
                    viewModel.runAudit(isAutoFix = false)
                    onNavigateToResult()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("start_audit_button"),
                enabled = !isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = YtRedPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBn) "এআই ভিডিও বিশ্লেষণ হচ্ছে..." else "Analyzing Video with AI...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Run Audit",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "অটো এসইও ও ভুল ডিটেক্ট করুন" else "Auto SEO & Audit Video Defects",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}
