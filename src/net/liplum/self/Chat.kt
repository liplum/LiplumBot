package net.liplum.self

import dev.kord.core.entity.channel.TextChannel
import kotlinx.coroutines.*
import net.liplum.FileSystem
import net.liplum.Guilds
import net.liplum.Vars

val data = FileSystem.temp.resolve("chat")
suspend fun addChatModule() {
    if (!data.exists()) withContext(Dispatchers.IO) {
        data.createNewFile()
    }
    CoroutineScope(Dispatchers.IO).launch {
        while (true) {
            val raw = data.readText()
            if ('\n' !in raw) delay(1000)
            val text = raw.trim()
            if (text.isNotBlank()) {
                data.writeText("")
                val channel = Vars.bot.getChannelOf<TextChannel>(Guilds.Channel.bot)
                channel?.createMessage(text)
            }
        }
    }
}
