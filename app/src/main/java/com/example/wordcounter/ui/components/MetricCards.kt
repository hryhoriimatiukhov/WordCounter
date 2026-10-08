package com.example.wordcounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordcounter.R
import com.example.wordcounter.model.TextMetrics
import com.example.wordcounter.ui.theme.MetricNumbersColor
import com.example.wordcounter.ui.theme.MetricPunctuationColor
import com.example.wordcounter.ui.theme.MetricSentencesColor
import com.example.wordcounter.ui.theme.MetricWordsColor

@Composable
fun MetricGrid(
    metrics: TextMetrics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.metric_sentences),
                count = metrics.sentences,
                accentColor = MetricSentencesColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.metric_words),
                count = metrics.words,
                accentColor = MetricWordsColor,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.metric_punctuation),
                count = metrics.punctuationMarks,
                accentColor = MetricPunctuationColor,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = stringResource(R.string.metric_numbers),
                count = metrics.numbers,
                accentColor = MetricNumbersColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    count: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
