package net.liplum.cmd

import dev.kord.core.entity.Message
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import net.liplum.cmd.ICommand.Companion.registerSelf
import java.net.URLEncoder

object GoogleCommand : ICommand {
    override val keyword: Keyword = "google"
    val conversation = listOf(
        "How lazy you are.",
        "Here you are.",
        "This for you.",
        "Click this.",
        "Check this.",
        "Google it yourself next time.",
    )
    init {
        registerSelf()
    }

    const val head = "https://www.google.com/search?q="
    fun genReply() = conversation.random()
    override suspend fun execute(raw: Message, args: List<String>) {
        raw.addReaction(Emojis.ok)
        val full = args.joinToString(" ")
        val query = withContext(Dispatchers.IO) {
            URLEncoder.encode(full, "UTF-8")
        }
        val reply = raw.channel.createMessage(genReply())
        raw.channel.createMessage("$head$query")
        delay(5000)
        reply.delete()
    }

    override fun buildHelp() = "<phrase..> -- google it."
}