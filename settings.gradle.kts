pluginManagement {
    repositories {
        exclusiveContent {
            forRepository { maven("https://maven.architectury.dev") }
            filter {
                includeGroup("architectury-plugin")
                includeGroupByRegex("dev\\.architectury.*")
                includeGroup("com.mojang")
            }
        }
        exclusiveContent {
            forRepository { maven("https://maven.fabricmc.net") }
            filter {
                includeGroup("net.fabricmc")
                includeGroup("fabric-loom")
            }
        }
        exclusiveContent {
            forRepository { maven("https://maven.minecraftforge.net") }
            filter {
                includeGroupByRegex("net\\.minecraftforge.*")
                includeGroup("de.oceanlabs.mcp")
            }
        }
        gradlePluginPortal()
    }
}

include("common")

val enabledPlatforms: String by settings
for (enabledPlatform in enabledPlatforms.split(",")) {
    include(enabledPlatform)
}

val modId: String by settings
rootProject.name = modId
