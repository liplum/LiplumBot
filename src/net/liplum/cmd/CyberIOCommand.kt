package net.liplum.cmd

object CyberIOCommand {
    const val releaseUrl = "https://github.com/liplum/CyberIO/releases/download/v4.1/CyberIO-4.1.jar"
    const val previewUrl = "https://nightly.link/liplum/CyberIO/workflows/Push/v5/CyberIO-Unzip-This.zip"

    init {
        RegisterTreeCommand("cyberio") {
            +Command("release") { raw, args ->
                raw.delete()
                raw.channel.createMessage(releaseUrl)
            }.addDesc("the latest release.")
            +Command("preview") { raw, args ->
                raw.delete()
                raw.channel.createMessage(previewUrl)
            }.addDesc("the latest developing preview.")
        }.whenSelf { raw, args ->

        }.addDesc("fetch Cyber IO download url.")
    }
}