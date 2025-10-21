# CHANGELOG

## v1.0.1

### ✨ New Features

- **`KeyHandlerHost`** composable now supports a `contentAlignment` parameter to align the content it wraps.
- **`KeyHandlerHost`** composable now supports a `propagateMinConstraints` parameter to determine whether incoming minimum constraints from the parent are passed to its content

## v1.0.0

This release marks a complete internal and external refactor of the key handling mechanism. The new architecture is more expressive, thread-safe, and designed specifically for Compose Multiplatform.

### 💥 Breaking Changes

- **Refactored Key Mapping API:** All single action and continuous action properties (`keys`, `singleActionKeys`, `combinations`, etc.) have been removed. Key mappings are now grouped by a single **`Trigger`** type.
- **New DSL Initialization:** Key mapping is now done via a Type-Safe Builder (DSL) structure using functions like `onPress { key(...) }` and `onRelease { combo(...) } }`. The old `addKey`/`addCombination` methods are deprecated/removed.
- **Continuous Handling Method Changed:** The old `loopBasedHandling: Boolean` flag and `handleInLoop()` method have been removed. Continuous actions are now handled using two distinct, modern triggers:
    * **`ON_REPEAT`**: Uses the system's native key repeat events (handled in `listen`).
    * **`ON_HOLD`**: Uses a dedicated, cancellable coroutine loop (handled via `KeyHandlerHost`).
- **Required Composable Wrapper:** To use `ON_HOLD` actions safely, all usage must now be wrapped in the **`KeyHandlerHost`** composable, which manages focus and the background coroutine lifecycle.

### ✨ New Features

- **Type-Safe DSL for Key Mapping:** Introduced a clean, keyword-like API for defining key and combination actions: `onPress`, `onRelease`, `onHold`, and `onRepeat`.
- **Composable Lifecycle Management:** Introduced **`KeyHandlerHost`** to manage focus and safely launch the continuous `ON_HOLD` coroutine loop. This ensures thread safety and prevents resource leaks when the component leaves the composition.
- **Hold Trigger Frequency:** Added `holdTriggerFrequency: Int` to control the execution rate ($\text{Hz}$) of `ON_HOLD` actions.
- **Flexible Combo Definition:** The `combo` function in the DSL can accept up to 4 keys for a combination (e.g., `combo(Key.Ctrl, Key.Shift, Key.A, Key.B) { ... }`).
- **Improved `toString()` Output:** The `KeyHandler.toString()` method now displays actions clearly grouped by their `Trigger` type.

### 🐛 Bug Fixes and Improvements
* **Multiplatform Safety:** The continuous loop for held keys is now a suspending function (`startHoldLoop`) and is tied to the Compose Coroutine Scope, making the library reliably safe for Kotlin Multiplatform (KMP) usage.


---

## v0.7.0 (start of changelog)

# Usage

*MUST USE WITH COMPOSE MULTIPLATFORM*
### build.gradle.kts

```kotlin
kotlin {
    //...
    sourceSets {
        //...
        commonMain.dependencies {
            //...
            //Format: groupId:artifactId:version
            //groupId = io.github.loop312
            //artifactId = compose-keyhandler
            //version = 0.7.0 (choose latest version instead)
            implementation("io.github.loop312:compose-keyhandler:0.7.0")
        }
    }
}
```

```kotlin
implementation("io.github.loop312:compose-keyhandler:0.7.0") 
```

### Common Main
```kotlin
/*initializer:
KeyHandler (
    loopBasedHandling -> this is for truly continuous execution rather than relying on systems input delay, 
    requires use of KeyHandler.handleInLoop(), Default: false
    
    consume -> this is for whether to consume the key event or not, Default: true
) {
    initialize keys here
}
*/
//-----------------------------------------------------------------------------------
//import io.github.loop312.compose_keyhandler.KeyHandler
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.onKeyEvent
//...

@Composable
fun main() {
    //if initializing outside a composable, don't use remember {}
    val keyHandler = remember {
        KeyHandler(loopBasedHandling = false, consume = true) {
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
    //or Modifier.onPreviewKeyEvent(keyHandler.listen)
    Box(Modifier.onKeyEvent(keyHandler.listen)) {
        //...
    }
    
    //optional, for continuous execution (set loopBasedHandling = true)
    LaunchedEffect(Unit) {
        while (true) {
            //if you want to automatically handle it with every frame (can use withFrameMillis too)
            //or just use a delay
            withFrameNanos {
                keyHandler.handleInLoop()
            }
        }
    }
}
```

### NOTE: Make sure object with modifier `onKeyEvent` or `onPreviewKeyEvent` is in focus