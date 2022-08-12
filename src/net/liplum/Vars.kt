package net.liplum

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.channel.GuildChannel
import net.liplum.util.findByName

object Vars {
    lateinit var bot: Kord
    const val maxNote = 10
    const val maxToDoCommandCount = 100
    lateinit var plumStar: Guild

    object Emoji {
        lateinit var uwu: GuildEmoji
        lateinit var heihei: GuildEmoji
    }

    object Channel {
        lateinit var bot: GuildChannel
    }

    suspend fun setup() {
        plumStar = bot.getGuild(Guilds.plumStar)!!
        // Channel
        Channel.bot = plumStar.getChannel(Guilds.Channel.bot)
        // Emoji
        Emoji.uwu = plumStar.emojis.findByName("uwu")
        Emoji.heihei = plumStar.emojis.findByName("heihei")
    }
}

object Guilds {
    val plumStar = Snowflake(937228972041842718)

    object User {
        val liplum = Snowflake(740093617955274764)
    }

    object Channel {
        val bot = Snowflake(1006643683153678506)
    }

    object Emoji {
        val uwu = Snowflake(937228972041842718)
    }
}