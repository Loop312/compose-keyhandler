package io.github.compose_keyhandler

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KeyHandler",
    ) {
        App()
    }
}