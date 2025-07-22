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

@Composable
@Preview
fun KeyHandlerTest() {
    //KeyHandler(false) will not consume key events
    val keyHandler = KeyHandler()

    //continuous execution
    keyHandler.addKey(Key.A) {
        println("A is being pressed")
        //or any other action you want to do
    }

    keyHandler.addMultipleKeys(setOf(Key.B, Key.C)) {
        println("B or C is being pressed")
        //or any other action you want to do
    }

    //one-time execution
    keyHandler.addSingleActionKey(Key.D) {
        println("D was pressed")
        //or any other action you want to do
    }

    keyHandler.addMultipleSingleActionKeys(setOf(Key.E, Key.F)) {
        println("E or F was pressed")
        //or any other action you want to do
    }

    //on release execution
    keyHandler.addReleaseKey(Key.G) {
        println("G was released")
        //or any other action you want to do
    }

    keyHandler.addMultipleReleaseKeys(setOf(Key.H, Key.I)) {
        println("H or I was released")
        //or any other action you want to do
    }

    //continuous combination execution
    keyHandler.addCombination(setOf(Key.J, Key.K)) {
        println("J and K are being pressed")
        //or any other action you want to do
    }

    //one-time combination execution
    keyHandler.addSingleActionCombination(setOf(Key.L, Key.M)) {
        println("L and M were pressed")
        //or any other action you want to do
    }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }, Modifier.onKeyEvent(keyHandler.listen)) {
                Text("Click me!")
            }
            Text(keyHandler.toString())
        }
    }
}