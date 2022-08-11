package net.liplum.member

import dev.kord.common.entity.Snowflake
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.on
import net.liplum.Vars

suspend fun addMemberModule() {
    Vars.bot.on<MemberLeaveEvent> {
        /*kord.getChannel(Vars.Chanel.bot)
        kord.rest.channel.getChannel(Vars.Chanel.bot)*/
    }
}