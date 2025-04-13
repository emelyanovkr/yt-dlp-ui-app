package org.emlnv.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.emlnv.ui.AppContent

/*
прогресс бар скачивания видео
дизайн
*/

@Composable
@Preview
fun AppPreview() {
    AppContent()
}

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1024.dp, 768.dp))
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "yt-dlp-downloader",
        icon = painterResource("drawable/app_icon.png")
    ) {
        AppContent()
    }
}