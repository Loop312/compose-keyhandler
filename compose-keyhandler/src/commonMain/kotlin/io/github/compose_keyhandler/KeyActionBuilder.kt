package io.github.compose_keyhandler

import androidx.compose.ui.input.key.Key

class KeyActionBuilder (
    private val keyHandler: KeyHandler,
    private val trigger: Trigger
) {
    fun key(key: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addKey(key, trigger, description, action)
    }

    fun key(key1: Key, key2: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addKey(key1, trigger, description, action)
        keyHandler.addKey(key2, trigger, description, action)
    }

    fun key(key1: Key, key2: Key, key3: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addKey(key1, trigger, description, action)
        keyHandler.addKey(key2, trigger, description, action)
        keyHandler.addKey(key3, trigger, description, action)
    }

    fun combo(key1: Key, key2: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addCombo(setOf(key1, key2), trigger, description, action)
    }

    fun combo(key1: Key, key2: Key, key3: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addCombo(setOf(key1, key2, key3), trigger, description, action)
    }

    fun combo(key1: Key, key2: Key, key3: Key, key4: Key, description: String = "No description", action: () -> Unit) {
        keyHandler.addCombo(setOf(key1, key2, key3, key4), trigger, description, action)
    }
}