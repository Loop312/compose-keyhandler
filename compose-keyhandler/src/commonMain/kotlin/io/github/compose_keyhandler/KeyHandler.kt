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
    var keys by mutableStateOf(mutableMapOf<Key, KeyAction>())
    var singleActionKeys by mutableStateOf(mutableMapOf<Key, KeyAction>())
    var releaseKeys by mutableStateOf(mutableMapOf<Key, KeyAction>())

    //combinations actions
    var combinations by mutableStateOf(mutableMapOf<Set<Key>, KeyAction>())
    var singleActionCombinations by mutableStateOf(mutableMapOf<Set<Key>, KeyAction>())

    //add/replace functions
    fun addKey(key: Key, description: String = "No description", action: () -> Unit) {
        keys[key] = KeyAction(description, action)
    }
    fun addMultipleKeys(keySet: Set<Key>, description: String = "No description", action: () -> Unit) {
        keySet.forEach { key -> keys[key] = KeyAction(description, action) }
    }
    fun addSingleActionKey(key: Key, description: String = "No description", action: () -> Unit) {
        singleActionKeys[key] = KeyAction(description, action)
    }
    fun addMultipleSingleActionKeys(keySet: Set<Key>, description: String = "No description", action: () -> Unit) {
        keySet.forEach { key -> singleActionKeys[key] = KeyAction(description, action) }
    }
    fun addReleaseKey(key: Key, description: String = "No description", action: () -> Unit) {
        releaseKeys[key] = KeyAction(description, action)
    }
    fun addMultipleReleaseKeys(keySet: Set<Key>, description: String = "No description", action: () -> Unit) {
        keySet.forEach { key -> releaseKeys[key] = KeyAction(description, action) }
    }
    fun addCombination(combination: Set<Key>, description: String = "No description", action: () -> Unit) {
        combinations[combination] = KeyAction(description, action)
    }
    fun addSingleActionCombination(combination: Set<Key>, description: String = "No description", action: () -> Unit) {
        singleActionCombinations[combination] = KeyAction(description, action)
    }

    fun getDescription(key: Key): String? {
        return keys[key]?.description
    }

    fun getDescription(combination: Set<Key>): String? {
        return combinations[combination]?.description
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
                    singleActionKeys[event.key]?.action?.invoke()
                }

                //continuous key actions
                if (event.key in keys) {
                    keys[event.key]?.action?.invoke()
                }

                //continuous combination actions
                combinations.forEach { (combination, keyAction) ->
                    if (pressedKeys.containsAll(combination)) {
                        keyAction.action.invoke()
                    }
                }

                //single combination actions
                singleActionCombinations.forEach { (combination, keyAction) ->
                    val isCurrentlyPressed = pressedKeys.containsAll(combination)
                    val wasPreviouslyPressed = previousPressedKeys.containsAll(combination)
                    if (isCurrentlyPressed && !wasPreviouslyPressed) {
                        keyAction.action.invoke()
                    }
                }
            }
            KeyEventType.KeyUp -> {
                pressedKeys -= event.key
                //release key actions
                if (event.key in releaseKeys) {
                    releaseKeys[event.key]?.action?.invoke()
                }
            }
        }
        consume
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("--------------------------\n")
        sb.append("Pressed Keys: $pressedKeys\n")

        sb.append("Keys (continuous): {\n")
        keys.forEach { (key, keyAction) -> sb.append("  $key: ${keyAction.description}\n") }
        sb.append("}\n")

        sb.append("Single Action Keys: {\n")
        singleActionKeys.forEach { (key, keyAction) -> sb.append("  $key: ${keyAction.description}\n") }
        sb.append("}\n")

        sb.append("Release Keys: {\n")
        releaseKeys.forEach { (key, keyAction) -> sb.append("  $key: ${keyAction.description}\n") }
        sb.append("}\n")

        sb.append("Combinations (continuous): {\n")
        combinations.forEach { (combo, keyAction) -> sb.append("  ${combo.joinToString("+")}: ${keyAction.description}\n") }
        sb.append("}\n")

        sb.append("Single Action Combinations: {\n")
        singleActionCombinations.forEach { (combo, keyAction) -> sb.append("  ${combo.joinToString("+")}: ${keyAction.description}\n") }
        sb.append("}\n")

        // If releaseCombinations are added back:
        // sb.append("Release Combinations: {\n")
        // releaseCombinations.forEach { (combo, keyAction) -> sb.append("  ${combo.joinToString("+")}: ${keyAction.description}\n") }
        // sb.append("}\n")

        sb.append("--------------------------")
        return sb.toString()
    }
}

data class KeyAction(
    val description: String,
    val action: () -> Unit
)