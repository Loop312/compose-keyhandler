package io.github.compose_keyhandler

data class KeyAction(
    val description: String,
    val action: () -> Unit
)