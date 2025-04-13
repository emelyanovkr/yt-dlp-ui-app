package org.emlnv.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import org.emlnv.utils.YT_INSTALLED
import org.emlnv.utils.YT_NOT_INSTALLED
import org.emlnv.utils.isYtDlpInstalled
import java.awt.Desktop
import java.net.URI

@Composable
fun MainScreen() {
    var message by remember { mutableStateOf("Checking for yt-dlp...") }
    var showInstallInfo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isYtDlpInstalled()) {
            message = YT_INSTALLED
        } else {
            message = YT_NOT_INSTALLED
            showInstallInfo = true
        }
    }

    MaterialTheme {
        Column {
            Button(onClick = {
                if (isYtDlpInstalled()) {
                    message = YT_INSTALLED
                    showInstallInfo = false
                } else {
                    message = YT_NOT_INSTALLED
                    showInstallInfo = true
                }
            }) {
                Text(message)
            }

            if (showInstallInfo) {
                Button(onClick = {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(
                            URI("https://github.com/yt-dlp/yt-dlp?tab=readme-ov-file#installation")
                        )
                    }
                }) {
                    Text("Install yt-dlp")
                }
            }
        }
    }
}