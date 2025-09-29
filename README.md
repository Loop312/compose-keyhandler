![Maven Central Version](https://img.shields.io/maven-central/v/io.github.loop312/compose-keyhandler)


## 1. About the Project: Compose KeyHandler 🚀

 **Compose KeyHandler** is a modern, lifecycle-aware library for handling keyboard input in **Jetpack Compose/Compose Multiplatform**. It provides a clean, Type-Safe Builder (DSL) to define single-key presses, complex key combinations, and continuous actions without manually tracking key states or managing coroutine loops.

### Core Features:

* **DSL-Driven Configuration:** Map keys and combos using expressive blocks like `onPress { ... }` and `onHold { ... }`.
* **Lifecycle-Aware Holds:** Use the **`KeyHandlerHost`** composable to safely manage continuous **`ON_HOLD`** actions, preventing resource leaks and ensuring KMP stability.
* **Four Distinct Triggers:** Clearly separate logic for different input needs:
  * **`onPress`**: Action runs once when the key/combo is pressed.
  * **`onRelease`**: Action runs once when the key/combo is released.
  * **`onRepeat`**: Action runs for every system-generated repeat event (good for text input).
  * **`onHold`**: Action runs at a fixed, configurable frequency (e.g., 60 FPS) while the key/combo is down (perfect for game movement).
* **Automatic Focus Management:** The `KeyHandlerHost` automatically requests focus for the content it wraps to ensure key events are received.

-----



## 2. Getting Started: Installation

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
            //version = 1.0.0 (choose latest version instead)
            implementation("io.github.loop312:compose-keyhandler:1.0.0")
        }
    }
}
```

```kotlin
implementation("io.github.loop312:compose-keyhandler:1.0.0") 
```

-----

## 3. Getting Started: Usage

### Common Main
```kotlin
//import...
//import androidx.compose.ui.input.key.Key
//import io.github.loop312.compose_keyhandler.KeyHandler
//import io.github.loop312.compose_keyhandler.KeyHandlerHost


@Composable
@Preview
fun KeyHandlerTest() {
    //use remember to prevent reinitialization in compose multiplatform
    val keyHandler = remember { setupKeyHandler() }
    //KeyHandlerHost is a composable that hosts the key handler
    KeyHandlerHost(keyHandler) {
        Text(keyHandler.toString())
    }
}

private fun setupKeyHandler(): KeyHandler {
    return KeyHandler {
        //will only activate when the key is initially pressed
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
        //will only activate when the key is released
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
        //will activate while the key is held (change holdTriggerFrequency to change how often it activates per second)
        //(default 60 times per second)
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
        //will activate/fire at the rate of the system's input
        //like holding down a key while on a search bar
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
```

-----

## 4. Configuration

#### The Difference Between `onPress`, `onRelease`, `onHold` and `onRepeat`

 | Trigger         | Behavior                                                         | Ideal Use Case                                                                                   |
 |:----------------|:-----------------------------------------------------------------|:-------------------------------------------------------------------------------------------------|
 | **`onPress`**   | Action runs once when the key/combo is pressed.                  | **Simple Key Presses**, like opening a menu or triggering an action.                             |
 | **`onRelease`** | Action runs once when the key/combo is released.                 | **Simple Key Releases**, like closing a menu or triggering an action.                            |
 | **`onHold`**    | Action runs at a **fixed rate** (set by `holdTriggerFrequency`). | **Game Movement**, physics updates, continuous non-UI state changes.                             |
 | **`onRepeat`**  | Action runs for every **system key repeat event**.               | **Native UI behavior**, like moving a cursor in a `TextField` or using the arrow keys to scroll. |

-----

## 5. CHANGELOG

[CHANGELOG](CHANGELOG.md)
