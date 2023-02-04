package net.liplum.cmd

object CyberIOCommand {
    const val releaseUrl = "https://github.com/liplum/CyberIO/releases/download/v5.1/CyberIO-5.1.jar"
    const val previewUrl = "https://nightly.link/liplum/CyberIO/workflows/Push/master/I-am-CyberIO-Unzip-me.zip"

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