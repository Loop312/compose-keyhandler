package io.github.compose_keyhandler

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import org.jetbrains.compose.ui.tooling.preview.Preview


val lol = KeyHandler()
@Composable
@Preview
fun App() {
    lol.addKey(Key.Enter) { println("lol")}
    lol.addKey(Key.A) { println("lol")}
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }, Modifier.onKeyEvent(lol.listen)) {
                Text("Click me!")
            }
            lol.activate()
            println(lol.toString())
            Text(lol.toString())
        }
    }
}