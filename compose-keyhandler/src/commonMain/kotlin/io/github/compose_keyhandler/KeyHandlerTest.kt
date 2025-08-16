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
    val keyHandler = remember { setupKeyHandlerForTesting(false) }

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

@Composable
@Preview
fun KeyHandlerTest2() { //loop based handling
    val keyHandler = remember { setupKeyHandlerForTesting(true) }

    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }, Modifier.onKeyEvent(keyHandler.listen)) {
                Text("Click me!")
            }
            Text(keyHandler.toString())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                keyHandler.handleInLoop()
            }
        }
    }
}

private fun setupKeyHandlerForTesting(loopBased: Boolean): KeyHandler {
    return KeyHandler(loopBased) {
        //continuous execution
        addKey(key = Key.A, description = "Prints A is being pressed") {
            println("A is being pressed")
            //or any other action you want to do
        }

        //println(getDescription(Key.A))

        addMultipleKeys(keySet = setOf(Key.B, Key.C), description = "Prints B or C is being pressed") {
            println("B or C is being pressed")
            //or any other action you want to do
        }

        //one-time execution
        addSingleActionKey(Key.D, "Prints D was pressed") {
            println("D was pressed")
            //or any other action you want to do
        }

        addMultipleSingleActionKeys(setOf(Key.E, Key.F), "Prints E or F was pressed") {
            println("E or F was pressed")
            //or any other action you want to do
        }

        //on release execution
        addReleaseKey(Key.G, "Prints G was released") {
            println("G was released")
            //or any other action you want to do
        }

        addMultipleReleaseKeys(setOf(Key.H, Key.I), "Prints H or I was released") {
            println("H or I was released")
            //or any other action you want to do
        }

        //continuous combination execution
        addCombination(setOf(Key.J, Key.K), "Prints J and K are being pressed") {
            println("J and K are being pressed")
            //or any other action you want to do
        }

        //println(getDescription(setOf(Key.J, Key.K)))

        //one-time combination execution
        addSingleActionCombination(setOf(Key.L, Key.M), "Prints L and M were pressed") {
            println("L and M were pressed")
            //or any other action you want to do
        }
    }
}