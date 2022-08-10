package net.liplum.reaction

import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import net.liplum.Vars
import net.liplum.util.isSentBySelf

suspend fun addCuteReaction() {
    Vars.bot.on<MessageCreateEvent> {
        if (message.isSentBySelf) return@on
        if (kord.selfId in message.mentionedUserIds) {
        }
    }
}