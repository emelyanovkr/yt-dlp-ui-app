package org.emlnv.utils


import java.util.concurrent.TimeUnit

const val YT_INSTALLED = "yt-dlp is installed. Ready to work!"
const val YT_NOT_INSTALLED = "yt-dlp is not installed, press the button below to see installation page"

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
    val proxyArg = if (useProxy) "--proxy https://$proxyIp:$proxyPort" else ""
    val downloadOption = if (downloadFolder.isNotBlank()) "-o \"$downloadFolder/%(title)s.%(ext)s\"" else ""
    val extraArg = extraArgs.ifBlank { "" }
    return "$baseCommand $proxyArg $downloadOption $extraArg $videoUrl".trim()
}

