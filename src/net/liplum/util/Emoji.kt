package net.liplum.util

import dev.kord.core.entity.GuildEmoji
import kotlinx.coroutines.flow.Flow

val emojiCache = HashMap<String, GuildEmoji>()
suspend fun Flow<GuildEmoji>.findByName(name: String): GuildEmoji {
    val emoji = emojiCache[name]
    if (emoji != null) return emoji
    this.collect {
        it.name?.let { name ->
            emojiCache[name] = it
        }
    }
    return emojiCache[name]!!
}

