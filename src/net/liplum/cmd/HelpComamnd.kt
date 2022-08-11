package net.liplum.cmd

import dev.kord.core.behavior.channel.createMessage
import net.liplum.cmd.ICommand.Companion.registerSelf

object HelpCommand {
    init {
        Command("help") { raw, args ->
            raw.delete()
            raw.channel.createMessage {
                content = ICommand.buildAllHelp()
            }
        }.registerSelf()
    }
}