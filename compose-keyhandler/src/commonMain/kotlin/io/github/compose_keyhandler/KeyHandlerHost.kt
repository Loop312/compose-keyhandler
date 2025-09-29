package io.github.compose_keyhandler

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.onKeyEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun KeyHandlerHost(
    keyHandler: KeyHandler,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    DisposableEffect(keyHandler) {
        var job: Job? = null
        if (keyHandler.onHoldKeys.isNotEmpty() || keyHandler.onHoldCombos.isNotEmpty()) {
            job = scope.launch {
                keyHandler.startHoldLoop()
            }
        }
        onDispose {
            job?.cancel()
        }
    }

    Box(modifier = modifier
        .fillMaxSize()
        .onKeyEvent(keyHandler.listen)
        .focusRequester(focusRequester)
        .focusable()
    ) {
        content()
    }
}