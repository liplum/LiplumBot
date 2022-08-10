package net.liplum.cmd

import kotlinx.coroutines.flow.firstOrNull
import net.liplum.cmd.ICommand.Companion.registerSelf

object UwUCommand {
    var uwuEmoji: String? = null

    init {
        Command("uwu") { raw, args ->
            raw.delete()
            val uwu = if (uwuEmoji == null) {
                val mention = raw.getGuild().emojis.firstOrNull {
                    it.name == "uwu"
                }?.mention
                uwuEmoji = mention
                mention
            } else {
                uwuEmoji
            }
            if (uwu != null) {
                raw.channel.createMessage(uwu)
            }
        }.registerSelf()
    }
}