package net.liplum.cmd

import dev.kord.core.behavior.channel.createEmbed
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction

object HelpCommand {
    init {
        RegisterCommand("help") { raw, args ->
            raw.addReaction(Emojis.ok)
            raw.channel.createEmbed {
                field(name = "Help", inline = true) {
                    ICommand.buildAllHelp()
                }
            }
        }.addDesc("Show this again.").hidden()
    }
}