package net.liplum.cmd

import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import net.liplum.Vars

suspend fun addCommandSystem() {
    GoogleCommand
    CyberIOCommand
    UwUCommand
    LiplumCommand
    HelpCommand
    NoteCommand.apply {
        loadNotes()
    }
    Vars.bot.on<MessageCreateEvent> {
        val content = message.content
        if (content.startsWith("!")) {
            val msg = content.removePrefix("!")
            val fullArgs = msg.split(" ")
            if (fullArgs.isEmpty()) return@on
            val keyword = fullArgs[0]
            val cmd = ICommand.match(keyword)
            if (cmd != null) {
                val args = fullArgs.slice(1 until fullArgs.size)
                cmd.execute(message, args)
            }
        }
    }
}
