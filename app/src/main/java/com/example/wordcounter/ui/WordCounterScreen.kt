package com.example.wordcounter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wordcounter.R
import com.example.wordcounter.ui.components.HistoryItemCard
import com.example.wordcounter.ui.components.MetricGrid
import com.example.wordcounter.ui.viewmodel.WordCounterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCounterScreen(
    viewModel: WordCounterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.snackbarMessageResId) {
        val messageResId = uiState.snackbarMessageResId
        if (messageResId != null) {
            val message = context.getString(messageResId)
            snackbarHostState.showSnackbar(message)
            viewModel.onSnackbarDismissed()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Text Input Section
            item {
                Column {
                    OutlinedTextField(
                        value = uiState.inputText,
                        onValueChange = { viewModel.onInputTextChanged(it) },
                        label = { Text(stringResource(R.string.input_label)) },
                        placeholder = { Text(stringResource(R.string.input_placeholder)) },
                        isError = uiState.isInputError,
                        supportingText = {
                            if (uiState.isInputError) {
                                Text(
                                    text = stringResource(R.string.error_empty_input),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        minLines = 4,
                        maxLines = 8,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.calculateMetrics() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.btn_calculate))
                        }

                        OutlinedButton(
                            onClick = { viewModel.generateTestSentence() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.btn_generate_test_sentence))
                        }
                    }
                }
            }

            // Results Section
            if (uiState.currentMetrics != null) {
                item {
                    Column {
                        Text(
                            text = stringResource(R.string.section_results_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        MetricGrid(metrics = uiState.currentMetrics!!)
                    }
                }
            }

            // History Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.section_history_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (uiState.sessionHistory.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearHistory() }) {
                            Text(
                                text = stringResource(R.string.btn_clear_history),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // History Items or Empty Placeholder
            if (uiState.sessionHistory.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.history_empty_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.sessionHistory,
                    key = { _, entry -> entry.id }
                ) { index, entry ->
                    HistoryItemCard(
                        entry = entry,
                        index = uiState.sessionHistory.size - index
                    )
                }
            }
        }
    }
}
