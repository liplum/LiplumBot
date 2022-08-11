package net.liplum.cmd

import dev.kord.core.behavior.channel.createEmbed

object HelpCommand {
    init {
        RegisterCommand("help") { raw, args ->
            raw.channel.createEmbed {
                field(name = "Help", inline = true) {
                    ICommand.buildAllHelp()
                }
            }
        }.addDesc("Show this again.").hidden()
    }
}