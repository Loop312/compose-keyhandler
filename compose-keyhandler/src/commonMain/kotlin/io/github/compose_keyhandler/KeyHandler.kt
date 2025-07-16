package io.github.compose_keyhandler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.*
import kotlin.collections.mutableMapOf

class KeyHandler (consume: Boolean = true) {
    //keeps track of pressed keys
    var pressedKeys by mutableStateOf(setOf<Key>())

    //single key actions
    var keys by mutableStateOf(mutableMapOf<Key, () -> Unit>())
    var singleActionKeys by mutableStateOf(mutableMapOf<Key, () -> Unit>())
    var releaseKeys by mutableStateOf(mutableMapOf<Key, () -> Unit>())

    //combinations actions
    var combinations by mutableStateOf(mutableMapOf<Set<Key>, () -> Unit>())
    var singleActionCombinations by mutableStateOf(mutableMapOf<Set<Key>, () -> Unit>())

    //add/replace functions
    fun addKey(key: Key, action: () -> Unit) {
        keys[key] = action
    }
    fun addSingleActionKey(key: Key, action: () -> Unit) {
        singleActionKeys[key] = action
    }
    fun addReleaseKey(key: Key, action: () -> Unit) {
        releaseKeys[key] = action
    }
    fun addCombination(combination: Set<Key>, action: () -> Unit) {
        combinations[combination] = action
    }
    fun addSingleActionCombination(combination: Set<Key>, action: () -> Unit) {
        singleActionCombinations[combination] = action
    }

    //use modifier = "Modifier.onKeyEvent(KeyHandler.listen)"
    val listen = { event: KeyEvent ->
        val previousPressedKeys = pressedKeys
        when (event.type) {
            KeyEventType.KeyDown -> {
                if (event.key !in previousPressedKeys) {
                    pressedKeys = previousPressedKeys + event.key
                }

                //single key actions
                if (event.key in singleActionKeys && event.key !in previousPressedKeys) {
                    singleActionKeys[event.key]?.invoke()
                }

                //continuous key actions
                if (event.key in keys) {
                    keys[event.key]?.invoke()
                }

                //continuous combination actions
                combinations.forEach { (combination, action) ->
                    if (pressedKeys.containsAll(combination)) {
                        action()
                    }
                }

                //single combination actions
                singleActionCombinations.forEach { (combination, action) ->
                    val isCurrentlyPressed = pressedKeys.containsAll(combination)
                    val wasPreviouslyPressed = previousPressedKeys.containsAll(combination)
                    if (isCurrentlyPressed && !wasPreviouslyPressed) {
                        action()
                    }
                }
            }
            KeyEventType.KeyUp -> {
                pressedKeys -= event.key
                //release key actions
                if (event.key in releaseKeys) {
                    releaseKeys[event.key]?.invoke()
                }
            }
        }
        consume
    }

    override fun toString(): String {
        return "--------------------------\n" +
                "Pressed Keys: $pressedKeys\n" +
                "Keys: ${keys.keys}\n" +
                "Single Action Keys: ${singleActionKeys.keys}\n" +
                "Release Keys: ${releaseKeys.keys}\n" +
                "Combinations: ${combinations.keys}\n" +
                "Single Action Combinations: ${singleActionCombinations.keys}\n" +
                "--------------------------"
    }
}