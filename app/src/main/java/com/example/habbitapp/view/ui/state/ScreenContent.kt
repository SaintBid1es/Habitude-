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
 * Рисует контент в зависимости от [UiState].
 *
 * @param onRetry вызывается при нажатии «Повторить» в состоянии Error.
 * @param empty проверка «пустых» данных внутри Success (например, пустой список).
 * @param emptyContent UI для пустого Success.
 * @param content UI для непустого Success.
 */
@Composable
fun <T> ScreenContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    empty: (T) -> Boolean = { false },
    emptyContent: @Composable () -> Unit = {},
    content: @Composable (T) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is UiState.Loading -> {
                CircularProgressIndicator()
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
                    Button(onClick = onRetry) {
                        Text(stringResource(R.string.action_retry))
                    }
                }
            }

            is UiState.Success -> {
                if (empty(state.data)) {
                    emptyContent()
                } else {
                    content(state.data)
                }
            }
        }
    }
}
