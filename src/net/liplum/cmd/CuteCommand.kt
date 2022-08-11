package net.liplum.cmd

import dev.kord.x.emoji.Emojis
import net.liplum.Vars.Emoji.heihei
import net.liplum.Vars.Emoji.uwu

object UwUCommand {
    var uwuEmoji: String? = null

    init {
        RegisterCommand("uwu") { raw, args ->
            raw.delete()
            raw.channel.createMessage(uwu.mention)
        }.addDesc("UwU")

        RegisterCommand("YOY") { raw, args ->
            raw.delete()
            raw.channel.createMessage("${Emojis.v}${heihei.mention}${Emojis.v}")
        }.addDesc("Cursed.").hidden()
    }
}