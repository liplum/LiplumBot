package net.liplum.util

import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import kotlinx.coroutines.flow.Flow
import kotlin.math.absoluteValue

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

val numberEmojis = listOf(
    Emojis.zero,
    Emojis.one,
    Emojis.two,
    Emojis.three,
    Emojis.four,
    Emojis.five,
    Emojis.six,
    Emojis.seven,
    Emojis.eight,
    Emojis.nine,
)
val number2EmojisCache = HashMap<Int, List<ReactionEmoji.Unicode>>()
fun Int.toEmojis(): List<ReactionEmoji.Unicode> {
    val abs = this.absoluteValue
    val emojis = number2EmojisCache.getOrPut(abs) {
        val res = ArrayList<ReactionEmoji.Unicode>(2)
        var n = abs
        if (n == 0) {
            res.add(0, numberEmojis[0].toReaction())
        } else {
            while (n > 0) {
                res.add(0, numberEmojis[n % 10].toReaction())
                n /= 10
            }
        }
        res
    }
    return emojis
}

fun List<ReactionEmoji.Unicode>.toText() =
    joinToString("") { it.mention }

fun Int.toEmojiText() = toEmojis().toText()
