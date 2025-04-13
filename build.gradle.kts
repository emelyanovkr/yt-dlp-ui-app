import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

group = "org.emlnv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.components:components-resources-desktop:1.7.3")
}

compose.desktop {
    application {
        mainClass = "org.emlnv.app.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "yt-dlp-ui-app"
            packageVersion = "1.0.0"
            windows {
                iconFile.set(project.file("src/main/resources/drawable/app_icon.png"))
                vendor = "starscream 13"
            }

        }
    }
}
