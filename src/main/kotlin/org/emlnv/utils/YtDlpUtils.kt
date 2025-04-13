package org.emlnv.utils

import java.util.concurrent.TimeUnit

fun isYtDlpInstalled(): Boolean {
    return try {
        val process = ProcessBuilder("yt-dlp", "--version")
            .redirectErrorStream(true)
            .start()
        if (process.waitFor(5, TimeUnit.SECONDS)) {
            process.exitValue() == 0
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}

fun buildYtDlpCommand(
    useProxy: Boolean,
    proxyIp: String,
    proxyPort: String,
    downloadFolder: String,
    videoUrl: String,
    extraArgs: String
): String {
    val baseCommand = "yt-dlp"
    val proxyArg = if (useProxy) "--proxy $proxyIp:$proxyPort" else ""
    val downloadOption = if (downloadFolder.isNotBlank()) "-o \"$downloadFolder\\%(title)s.%(ext)s\"" else ""
    val extraArg = extraArgs.ifBlank { "" }
    return listOf(baseCommand, videoUrl, proxyArg, downloadOption, extraArg)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
}

