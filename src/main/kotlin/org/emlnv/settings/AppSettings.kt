package org.emlnv.settings

import java.util.prefs.Preferences

object AppSettings {
    private val prefs: Preferences = Preferences.userRoot().node("org.emlnv.ytdlpui")

    var downloadFolder: String
        get() = prefs.get("downloadFolder", "")
        set(value) = prefs.put("downloadFolder", value)

    var rememberFolder: Boolean
        get() = prefs.getBoolean("rememberFolder", false)
        set(value) = prefs.putBoolean("rememberFolder", value)

    var useProxy: Boolean
        get() = prefs.getBoolean("useProxy", false)
        set(value) = prefs.putBoolean("useProxy", value)

    var proxyIp: String
        get() = prefs.get("proxyIp", "127.0.0.1")
        set(value) = prefs.put("proxyIp", value)

    var proxyPort: String
        get() = prefs.get("proxyPort", "8080")
        set(value) = prefs.put("proxyPort", value)

    var extraArgs: String
        get() = prefs.get("extraArgs", "")
        set(value) = prefs.put("extraArgs", value)
}