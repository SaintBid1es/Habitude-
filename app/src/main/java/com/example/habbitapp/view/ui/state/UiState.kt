package com.example.habbitapp.view.ui.state

/**
 * Единое состояние экрана для UI.
 *
 * - [Loading] — данные ещё не готовы
 * - [Success] — данные получены (список может быть пустым → показываем Empty в UI)
 * - [Error] — ошибка с текстом для пользователя
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
