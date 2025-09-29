package io.github.compose_keyhandler

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.input.key.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

class KeyHandler (
    val consume: Boolean = true,
    val holdTriggerFrequency: Int = 60,
    init: KeyHandler.() -> Unit = {}
) {
    //keeps track of pressed keys
    private var pressedKeys by mutableStateOf(setOf<Key>())

    //single key actions
    private var onPressKeys = mutableStateMapOf<Key, KeyAction>()
    private var onReleaseKeys = mutableStateMapOf<Key, KeyAction>()
    internal var onHoldKeys = mutableStateMapOf<Key, KeyAction>()
    private var onRepeatKeys = mutableStateMapOf<Key, KeyAction>()

    //combinations actions
    private var onPressCombos = mutableStateMapOf<Set<Key>, KeyAction>()
    private var onReleaseCombos = mutableStateMapOf<Set<Key>, KeyAction>()
    internal var onHoldCombos = mutableStateMapOf<Set<Key>, KeyAction>()
    private var onRepeatCombos = mutableStateMapOf<Set<Key>, KeyAction>()


    init {
        this.init()
    }

    internal fun addKey(key: Key, trigger: Trigger, description: String = "No description", action: () -> Unit) {
        when (trigger) {
            Trigger.ON_PRESS -> onPressKeys[key] = KeyAction(description, action)
            Trigger.ON_RELEASE -> onReleaseKeys[key] = KeyAction(description, action)
            Trigger.ON_HOLD -> onHoldKeys[key] = KeyAction(description, action)
            Trigger.ON_REPEAT -> onRepeatKeys[key] = KeyAction(description, action)
        }
    }

    internal fun addCombo(keySet: Set<Key>, trigger: Trigger, description: String, action: () -> Unit){
        when (trigger) {
            Trigger.ON_PRESS -> onPressCombos[keySet] = KeyAction(description, action)
            Trigger.ON_RELEASE -> onReleaseCombos[keySet] = KeyAction(description, action)
            Trigger.ON_HOLD -> onHoldCombos[keySet] = KeyAction(description, action)
            Trigger.ON_REPEAT -> onRepeatCombos[keySet] = KeyAction(description, action)
        }
    }
    //key description
    fun getDescription(key: Key, trigger: Trigger): String? {
        return when (trigger) {
            Trigger.ON_PRESS -> onPressKeys[key]?.description
            Trigger.ON_RELEASE -> onReleaseKeys[key]?.description
            Trigger.ON_HOLD -> onHoldKeys[key]?.description
            Trigger.ON_REPEAT -> onRepeatKeys[key]?.description
        }
    }
    //combo description
    fun getDescription(combination: Set<Key>, trigger: Trigger): String? {
        return when (trigger) {
            Trigger.ON_PRESS -> onPressCombos[combination]?.description
            Trigger.ON_RELEASE -> onReleaseCombos[combination]?.description
            Trigger.ON_HOLD -> onHoldCombos[combination]?.description
            Trigger.ON_REPEAT -> onRepeatCombos[combination]?.description
        }
    }

    fun onPress(builder: KeyActionBuilder.() -> Unit) {
        KeyActionBuilder(this, Trigger.ON_PRESS).apply(builder)
    }
    fun onRelease(builder: KeyActionBuilder.() -> Unit) {
        KeyActionBuilder(this, Trigger.ON_RELEASE).apply(builder)
    }
    fun onHold(builder: KeyActionBuilder.() -> Unit) {
        KeyActionBuilder(this, Trigger.ON_HOLD).apply(builder)
    }
    fun onRepeat(builder: KeyActionBuilder.() -> Unit) {
        KeyActionBuilder(this, Trigger.ON_REPEAT).apply(builder)
    }

    //use modifier = "Modifier.onKeyEvent(KeyHandler.listen)"
    val listen = { event: KeyEvent ->
        val previousPressedKeys = pressedKeys
        when (event.type) {
            KeyEventType.KeyDown -> {
                //add key to pressedKeys if not already pressed
                if (event.key !in previousPressedKeys) {
                    pressedKeys = previousPressedKeys + event.key
                }

                //single key actions (onPress)
                if (event.key in onPressKeys && event.key !in previousPressedKeys) {
                    onPressKeys[event.key]?.action?.invoke()
                }

                //continuous key actions (onRepeat)
                if (event.key in onRepeatKeys) {
                    onRepeatKeys[event.key]?.action?.invoke()
                }

                //single combination actions (onPress)
                onPressCombos.forEach { (combination, keyAction) ->
                    val isCurrentlyPressed = pressedKeys.containsAll(combination)
                    val wasPreviouslyPressed = previousPressedKeys.containsAll(combination)
                    if (isCurrentlyPressed && !wasPreviouslyPressed) {
                        keyAction.action.invoke()
                    }
                }

                //continuous combination actions (onRepeat)
                onRepeatCombos.forEach { (combination, keyAction) ->
                    if (pressedKeys.containsAll(combination)) {
                        keyAction.action.invoke()
                    }
                }
            }

            KeyEventType.KeyUp -> {
                pressedKeys -= event.key
                //release key actions (onRelease)
                if (event.key in onReleaseKeys) {
                    onReleaseKeys[event.key]?.action?.invoke()
                }

                //single combination actions (onRelease)
                onReleaseCombos.forEach { (combination, keyAction) ->
                    val isCurrentlyPressed = pressedKeys.containsAll(combination)
                    val wasPreviouslyPressed = previousPressedKeys.containsAll(combination)
                    if (!isCurrentlyPressed && wasPreviouslyPressed) {
                        keyAction.action.invoke()
                    }
                }
            }
        }
        consume
    }

    suspend fun startHoldLoop() {
        val delay = if (holdTriggerFrequency > 0) 1000L / holdTriggerFrequency else withFrameMillis { it }
        while (currentCoroutineContext().isActive) {
            onHoldKeys.forEach { (key, keyAction) ->
                if (key in pressedKeys) {
                    keyAction.action.invoke()
                }
            }
            onHoldCombos.forEach { (combination, keyAction) ->
                if (pressedKeys.containsAll(combination)) {
                    keyAction.action.invoke()
                }
            }
            delay(delay)
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("-------------------------------\n")
        sb.append("Hold Trigger Frequency: $holdTriggerFrequency\n")
        sb.append("Consume: $consume\n")
        sb.append("Pressed Keys: $pressedKeys\n")

        if (onPressKeys.isNotEmpty() || onPressCombos.isNotEmpty()) {
            sb.append("OnPress: \n")
            sb.append("  Keys: \n")
            onPressKeys.forEach { (key, keyAction) ->
                sb.append("    $key: ${keyAction.description}\n")
            }
            sb.append("  Combinations: \n")
            onPressCombos.forEach { (combination, keyAction) ->
                sb.append("    ${combination.joinToString(" + ")}: ${keyAction.description}\n")
            }
        }

        if (onReleaseKeys.isNotEmpty() || onReleaseCombos.isNotEmpty()) {
            sb.append("\nOnRelease: \n")
            sb.append("  Keys: \n")
            onReleaseKeys.forEach { (key, keyAction) ->
                sb.append("    $key: ${keyAction.description}\n")
            }
            sb.append("  Combinations: \n")
            onReleaseCombos.forEach { (combination, keyAction) ->
                sb.append("    ${combination.joinToString(" + ")}: ${keyAction.description}\n")
            }
        }

        if (onHoldKeys.isNotEmpty() || onHoldCombos.isNotEmpty()) {
            sb.append("\nOnHold: \n")
            sb.append("  Keys: \n")
            onHoldKeys.forEach { (key, keyAction) ->
                sb.append("    $key: ${keyAction.description}\n")
            }
            sb.append("  Combinations: \n")
            onHoldCombos.forEach { (combination, keyAction) ->
                sb.append("    ${combination.joinToString(" + ")}: ${keyAction.description}\n")
            }
        }

        if (onRepeatKeys.isNotEmpty() || onRepeatCombos.isNotEmpty()) {
            sb.append("\nOnRepeat: \n")
            sb.append("    Keys: \n")
            onRepeatKeys.forEach { (key, keyAction) ->
                sb.append("    $key: ${keyAction.description}\n")
            }
            sb.append("  Combinations: \n")
            onRepeatCombos.forEach { (combination, keyAction) ->
                sb.append("    ${combination.joinToString(" + ")}: ${keyAction.description}\n")
            }
        }

        sb.append("-------------------------------")
        return sb.toString()
    }
}