![Maven Central Version](https://img.shields.io/maven-central/v/io.github.loop312/compose-keyhandler)


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