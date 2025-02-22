package io.github.compose_keyhandler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.*

class KeyHandler {
    //keeps track of pressed keys
    var pressedKeys by mutableStateOf(setOf<Key>())
    //a set of dictionaries/maps, maps key to action
    var keys by mutableStateOf(setOf<Map<Key, () -> Unit>>())

    //add a key and an action
    fun addKey(key: Key, action: () -> Unit) {
        keys += mapOf(key to action)
    }

    //use modifier = "Modifier.onKeyEvent(KeyHandler.listener)"
    val listener = { event: KeyEvent ->
        //when a button is pressed add it to pressedKeys and when it's released remove it
        when (event.type) {
            KeyEventType.KeyDown -> {
                pressedKeys += event.key
            }
            KeyEventType.KeyUp -> {
                pressedKeys -= event.key
            }
        }
        true
    }

    fun activate() {
        //goes through all the maps
        for (maps in keys) {
            //goes through all the keys in the maps
            for ((key, action) in maps) {
                //if the key is pressed, run the action
                if (key in pressedKeys) {
                    action()
                }
            }
        }
    }


    override fun toString(): String {
        return "Pressed Keys: $pressedKeys\n" +
                "Keys: ${keys.joinToString(", ") { it.keys.toString() }}"
    }
}