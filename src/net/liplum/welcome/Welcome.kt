package net.liplum.welcome

import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.on
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import net.liplum.Vars

suspend fun addWelcomeWordsModule() {
    Vars.bot.on<MemberJoinEvent> {
        if (member.id == kord.selfId) return@on
        if (member.isBot) {

        }
    }
}