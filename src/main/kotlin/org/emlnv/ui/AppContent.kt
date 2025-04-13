package org.emlnv.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.emlnv.utils.buildYtDlpCommand
import org.emlnv.utils.isYtDlpInstalled

@Composable
fun AppContent() {
    var statusMessage by remember { mutableStateOf("Checking for yt-dlp...") }
    var showInstallInfo by remember { mutableStateOf(false) }
    var videoUrl by remember { mutableStateOf("") }
    var useProxy by remember { mutableStateOf(false) }
    var proxyIp by remember { mutableStateOf("127.0.0.1") }
    var proxyPort by remember { mutableStateOf("8080") }
    var downloadFolder by remember { mutableStateOf("") }
    var extraArgs by remember { mutableStateOf("") }
    var attemptedDownload by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (isYtDlpInstalled()) {
            statusMessage = "yt-dlp is installed. Ready to work!"
            showInstallInfo = false
        } else {
            statusMessage = "yt-dlp is not installed!"
            showInstallInfo = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Status: $statusMessage") })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, start = 16.dp, end = 16.dp)
        ) {
            // Поле ввода URL видео
            OutlinedTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Video URL") },
                modifier = Modifier.fillMaxWidth()
            )
            // Сообщение об ошибке показывается только после попытки скачивания
            if (attemptedDownload && videoUrl.isBlank()) {
                Text("Please enter a video URL", color = MaterialTheme.colors.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Поле для ввода папки для скачивания
            OutlinedTextField(
                value = downloadFolder,
                onValueChange = { downloadFolder = it },
                label = { Text("Download Folder") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Поле для дополнительных аргументов
            OutlinedTextField(
                value = extraArgs,
                onValueChange = { extraArgs = it },
                label = { Text("Extra Arguments") },
                modifier = Modifier.fillMaxWidth()
            )

            // Переключатель proxy, центрированный относительно надписи
            ProxyToggleUI(useProxy = useProxy, onProxyToggle = { useProxy = it })

            // Если proxy включён, показываем поля для ввода IP и порта
            if (useProxy) {
                OutlinedTextField(
                    value = proxyIp,
                    onValueChange = { proxyIp = it },
                    label = { Text("Proxy IP") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = proxyPort,
                    onValueChange = { proxyPort = it },
                    label = { Text("Proxy Port") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val downloadEnabled = videoUrl.isNotBlank() && (!useProxy || (proxyIp.isNotBlank() && proxyPort.isNotBlank()))
            Button(
                onClick = {
                    if (!downloadEnabled) {
                        attemptedDownload = true
                    } else {
                        attemptedDownload = false
                        println("Download video from URL: $videoUrl with proxy: $useProxy and extra args: $extraArgs")
                    }
                },
                enabled = downloadEnabled
            ) {
                Text("Download Video")
            }


            Spacer(modifier = Modifier.height(4.dp))

            // Отладочный вывод итоговой команды с небольшим смещением влево
            Text(
                text = "Command: " + buildYtDlpCommand(
                    useProxy = useProxy,
                    proxyIp = proxyIp,
                    proxyPort = proxyPort,
                    downloadFolder = downloadFolder,
                    videoUrl = videoUrl,
                    extraArgs = extraArgs
                ),
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        // Кнопка установки yt-dlp, расположенная в правом нижнем углу, если утилита не установлена
        if (showInstallInfo) {
            Button(
                onClick = {
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().browse(
                            java.net.URI("https://github.com/yt-dlp/yt-dlp?tab=readme-ov-file#installation")
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("Install yt-dlp")
            }
        }
    }
}
