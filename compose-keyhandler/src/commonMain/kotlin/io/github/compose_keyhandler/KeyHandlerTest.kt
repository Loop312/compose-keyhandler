package io.github.compose_keyhandler

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.Key
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun KeyHandlerTest() {
    val keyHandler = remember { setupKeyHandler() }
    KeyHandlerHost(keyHandler) {
        Text(keyHandler.toString())
    }
}

private fun setupKeyHandler(): KeyHandler {
    return KeyHandler {
        onPress {
            key(Key.A, "A was pressed") {
                println("A was pressed")
            }
            key(Key.B, Key.C, "B or C was pressed") {
                println("B or C was pressed")
            }
            combo(Key.D, Key.E, "D and E were pressed") {
                println("D and E were pressed")
            }
        }
        onRelease {
            key(Key.F, "F was released") {
                println("F was released")
            }
            key(Key.G, Key.H, "G or H was released") {
                println("G or H was released")
            }
            combo(Key.I, Key.J, "I and J were released") {
                println("I and J were released")
            }
        }
        onHold {
            key(Key.K, "K is being held") {
                println("K is being held")
            }
            key(Key.L, Key.M, "L or M is being held") {
                println("L or M is being held")
            }
            combo(Key.N, Key.O, "N and O are being held") {
                println("N and O are being held")
            }
        }
        onRepeat {
            key(Key.P, "P is being repeated") {
                println("P is being repeated")
            }
            key(Key.Q, Key.R, "Q or R is being repeated") {
                println("Q or R is being repeated")
            }
            combo(Key.S, Key.T, "S and T are being repeated") {
                println("S and T are being repeated")
            }
        }
    }
}