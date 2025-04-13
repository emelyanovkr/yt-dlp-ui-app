package org.emlnv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.emlnv.settings.AppSettings
import org.emlnv.utils.buildYtDlpCommand
import org.emlnv.utils.isYtDlpInstalled
import java.io.BufferedReader
import javax.swing.JFileChooser
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun BottomStatusBar(statusMessage: String, modifier: Modifier = Modifier) {
    BottomAppBar(modifier = modifier.height(32.dp)) {
        Text(
            text = statusMessage,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun VideoUrlInput(
    videoUrl: String,
    attemptedDownload: Boolean,
    onVideoUrlChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = videoUrl,
            onValueChange = onVideoUrlChange,
            label = { Text("Video URL") },
            modifier = Modifier.fillMaxWidth()
        )
        if (attemptedDownload && videoUrl.isBlank()) {
            Text("Please enter a video URL", color = MaterialTheme.colors.error)
        }
    }
}

@Composable
fun DownloadFolderInput(
    downloadFolder: String,
    onDownloadFolderChange: (String) -> Unit,
    rememberFolder: Boolean,
    onRememberFolderChange: (Boolean) -> Unit
) {
    Column {
        OutlinedTextField(
            value = downloadFolder,
            onValueChange = onDownloadFolderChange,
            label = { Text("Download Folder") },
            trailingIcon = {
                IconButton(onClick = {
                    val chooser = JFileChooser().apply {
                        fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    }
                    val result = chooser.showOpenDialog(null)
                    if (result == JFileChooser.APPROVE_OPTION) {
                        onDownloadFolderChange(chooser.selectedFile.absolutePath)
                    }
                }) {
                    Icon(
                        painter = painterResource("drawable/folder.png"),
                        contentDescription = "Choose folder",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberFolder,
                onCheckedChange = onRememberFolderChange
            )
            Text("Remember folder")
        }
    }
}

@Composable
fun ProxySection(
    useProxy: Boolean,
    onUseProxyChange: (Boolean) -> Unit,
    proxyIp: String,
    onProxyIpChange: (String) -> Unit,
    proxyPort: String,
    onProxyPortChange: (String) -> Unit
) {
    Column {
        ProxyToggleUI(useProxy = useProxy, onProxyToggle = onUseProxyChange)
        if (useProxy) {
            OutlinedTextField(
                value = proxyIp,
                onValueChange = onProxyIpChange,
                label = { Text("Proxy IP") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = proxyPort,
                onValueChange = onProxyPortChange,
                label = { Text("Proxy Port") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ExtraArgsInput(
    extraArgs: String,
    onExtraArgsChange: (String) -> Unit
) {
    OutlinedTextField(
        value = extraArgs,
        onValueChange = onExtraArgsChange,
        label = { Text("Extra Arguments") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun DownloadControls(
    downloadEnabled: Boolean,
    isDownloading: Boolean,
    onStart: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onStart,
            enabled = downloadEnabled && !isDownloading
        ) {
            Text("Download Video")
        }
        if (isDownloading) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Cancel")
            }
        }
    }
}


@Composable
fun AppContent() {
    var videoUrl by remember { mutableStateOf("") }
    var useProxy by remember { mutableStateOf(AppSettings.useProxy) }
    var proxyIp by remember { mutableStateOf(AppSettings.proxyIp) }
    var proxyPort by remember { mutableStateOf(AppSettings.proxyPort) }
    var downloadFolder by remember { mutableStateOf(AppSettings.downloadFolder) }
    var extraArgs by remember { mutableStateOf(AppSettings.extraArgs) }
    var attemptedDownload by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Checking for yt-dlp...") }
    var showInstallInfo by remember { mutableStateOf(false) }
    var rememberFolder by remember { mutableStateOf(AppSettings.rememberFolder) }
    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var outputLines by remember { mutableStateOf(listOf<String>()) }
    var latestLine by remember { mutableStateOf("") }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (isYtDlpInstalled()) {
            statusMessage = "yt-dlp is installed. Ready to work!"
            showInstallInfo = false
        } else {
            statusMessage = "yt-dlp is not installed!"
            showInstallInfo = true
        }
    }

    LaunchedEffect(useProxy) { AppSettings.useProxy = useProxy }
    LaunchedEffect(proxyIp) { AppSettings.proxyIp = proxyIp }
    LaunchedEffect(proxyPort) { AppSettings.proxyPort = proxyPort }
    LaunchedEffect(rememberFolder, downloadFolder) {
        if (rememberFolder) AppSettings.downloadFolder = downloadFolder else AppSettings.downloadFolder = ""
    }
    LaunchedEffect(extraArgs) { AppSettings.extraArgs = extraArgs }
    LaunchedEffect(rememberFolder) { AppSettings.rememberFolder = rememberFolder }

    val downloadEnabled = videoUrl.isNotBlank() && (!useProxy || (proxyIp.isNotBlank() && proxyPort.isNotBlank()))

    fun cancelDownload() {
        downloadJob?.cancel()
        isDownloading = false
        outputLines = outputLines + "Download cancelled."
        latestLine = "Download cancelled."
    }

    fun startDownload() {
        attemptedDownload = false
        isDownloading = true
        outputLines = emptyList()
        val command = buildYtDlpCommand(useProxy, proxyIp, proxyPort, downloadFolder, videoUrl, extraArgs)
        println("Executing: $command")
        downloadJob = coroutineScope.launch(Dispatchers.IO) {
            try {
                val process = ProcessBuilder(command.split(" "))
                    .redirectErrorStream(true)
                    .start()

                val reader: BufferedReader = process.inputStream.bufferedReader()
                reader.forEachLine { line ->
                    val regex = Regex("""(\d{1,3}\.\d+)%""")
                    regex.find(line)?.groupValues?.get(1)?.toFloatOrNull()?.let {
                        progress = it / 100f
                    }
                    outputLines = outputLines + line
                    latestLine = line
                }
                process.waitFor()
            } catch (e: CancellationException) {
                outputLines = outputLines + "Process cancelled."
                latestLine = "Process cancelled."
            } finally {
                isDownloading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        VideoUrlInput(
            videoUrl = videoUrl,
            attemptedDownload = attemptedDownload,
            onVideoUrlChange = { videoUrl = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DownloadFolderInput(
            downloadFolder = downloadFolder,
            onDownloadFolderChange = { downloadFolder = it },
            rememberFolder = rememberFolder,
            onRememberFolderChange = { rememberFolder = it }
        )

        ExtraArgsInput(
            extraArgs = extraArgs,
            onExtraArgsChange = { extraArgs = it }
        )

        ProxySection(
            useProxy = useProxy,
            onUseProxyChange = { useProxy = it },
            proxyIp = proxyIp,
            onProxyIpChange = { proxyIp = it },
            proxyPort = proxyPort,
            onProxyPortChange = { proxyPort = it }
        )

        Spacer(modifier = Modifier.height(4.dp))

        DownloadControls(
            downloadEnabled = downloadEnabled,
            isDownloading = isDownloading,
            onStart = {
                if (!downloadEnabled) attemptedDownload = true else startDownload()
            },
            onCancel = { cancelDownload() }
        )

        if (isDownloading) {
            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())
            if (latestLine.isNotBlank()) {
                Text(latestLine, color = Color.Gray, style = MaterialTheme.typography.caption)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        SelectionContainer {
            Text("Command: ${buildYtDlpCommand(useProxy, proxyIp, proxyPort, downloadFolder, videoUrl, extraArgs)}")
        }

        Spacer(modifier = Modifier.weight(1f))
        BottomStatusBar(statusMessage = statusMessage, modifier = Modifier.align(Alignment.Start))

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
                    .align(Alignment.End)
                    .padding(bottom = 8.dp)
            ) {
                Text("Install yt-dlp")
            }
        }
    }
}