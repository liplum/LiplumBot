package net.liplum.cmd

import dev.kord.core.behavior.channel.createMessage
import dev.kord.x.emoji.Emojis
import net.liplum.Guilds

object LiplumCommand {
    val warnings = listOf(
        "What do you want to do?",
        "No, you can't.${Emojis.punch}",
        "Stop",
        "Seriously?${Emojis.sweatSmile}",
        "Why should I obey you?${Emojis.`-1`}",
        "I won't endorse you.",
        "I don't want to say that.",
    )

    init {
        RegisterCommand("repeat") { raw, args ->
            if (raw.author?.id == Guilds.User.liplum) {
                raw.delete()
                val order = args.joinToString(" ")
                raw.channel.createMessage {
                    content = order
                    messageReference = raw.messageReference?.data?.id?.value
                }
            } else {
                raw.channel.createMessage {
                    content = warnings.random()
                    messageReference = raw.id
                }
            }
        }.hidden()
    }
}