package com.example.habbitapp.view.ui.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habbitapp.R

/**
 * Рисует один из вариантов: загрузка, ошибка с retry, пусто или основной контент.
 */
@Composable
fun <T> ScreenStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null,
    emptyMessage: String = stringResource(R.string.state_empty_habits),
    isEmpty: (T) -> Boolean = { value ->
        when (value) {
            is Collection<*> -> value.isEmpty()
            else -> false
        }
    },
    content: @Composable (T) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            UiState.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.state_loading),
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            is UiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        text = state.message.ifBlank {
                            stringResource(R.string.state_error_generic)
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                    )
                    if (onRetry != null) {
                        Button(onClick = onRetry) {
                            Text(stringResource(R.string.action_retry))
                        }
                    }
                }
            }

            is UiState.Success -> {
                if (isEmpty(state.data)) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    content(state.data)
                }
            }
        }
    }
}
