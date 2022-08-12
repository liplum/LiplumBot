package net.liplum.self

import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.liplum.FileSystem
import net.liplum.Guilds
import net.liplum.Vars
import net.liplum.util.plusAssign
import net.liplum.util.toEmojiText

@Serializable
data class ToDo(
    val content: String,
    val timeStamp: Long = System.currentTimeMillis(),
)

object ToDoList {
    val all = ArrayList<ToDo>()
    val data = FileSystem.data.resolve("MyToDo.json")
    suspend fun loadTodo() {
        withContext(Dispatchers.IO) {
            if (data.isFile) {
                val json = data.readText()
                all.clear()
                try {
                    all += Json.decodeFromString<List<ToDo>>(json)
                } catch (_: Exception) {
                }
            }
        }
    }

    suspend fun saveToDo() {
        withContext(Dispatchers.IO) {
            if (!data.isDirectory) {
                data.writeText(Json.encodeToString(all))
            }
        }
    }
    @Suppress("ControlFlowWithEmptyBody")
    suspend fun addToDoModule() {
        Vars.bot.on<MessageCreateEvent> {
            val userID = message.author?.id
            if (userID != Guilds.User.liplum && userID != kord.selfId) return@on
            val content = message.content
            if (content.length > Vars.maxToDoCommandCount) return@on
            val lowercase = content.lowercase().trim()
            if (lowercase == "my todo") {
                message.addReaction(Emojis.ok)
                message.channel.displayToDo()
            } else if (lowercase == "todo it" || lowercase == "todo this") {
                val todo = message.referencedMessage?.content
                if (todo != null && todo.isNotBlank()) {
                    all.add(ToDo(todo))
                    saveToDo()
                    message.addReaction(Emojis.ok)
                    message.channel.displayToDo()
                } else {
                    message.addReaction(Emojis.x)
                }
            } else if (tryFinishToDo(lowercase, "done")) {
                message.addReaction(Emojis.ok)
                message.channel.displayToDo()
            } else if (tryFinishToDo(lowercase, "finish")) {
                message.addReaction(Emojis.ok)
                message.channel.displayToDo()
            } else if (tryFinishToDo(lowercase, "finished")) {
                message.addReaction(Emojis.ok)
                message.channel.displayToDo()
            } else if (lowercase.length > 5 && lowercase.startsWith("todo:")) {
                val todo = content.substring(5)
                if (todo.isNotBlank()) {
                    all.add(ToDo(todo))
                    saveToDo()
                    message.addReaction(Emojis.ok)
                    message.channel.displayToDo()
                } else {
                    message.addReaction(Emojis.x)
                }
            }
        }
    }

    private suspend fun tryFinishToDo(full: String, cmd: String): Boolean {
        var finished = false
        if (all.isNotEmpty() && full.startsWith(cmd)) {
            val indexFull = full.removePrefix(cmd).trim()
            if (indexFull == "all") {
                if (finishAll())
                    finished = true
            } else {
                val indices = indexFull.split(",")
                for (indexStr in indices) {
                    indexStr.trim().toIntOrNull()?.let {
                        if (it in all.indices) {
                            if (finishToDo(it)) {
                                finished = true
                            }
                        }
                    }
                }
            }
        }
        return finished
    }

    private suspend fun finishToDo(index: Int): Boolean {
        all.removeAt(index)
        saveToDo()
        return true
    }

    private suspend fun finishAll(): Boolean {
        all.clear()
        saveToDo()
        return true
    }

    private suspend fun MessageChannelBehavior.displayToDo() {
        createEmbed {
            field {
                name = "Liplum's TODOs"
                inline = true
                value = if (all.isEmpty()) "You have no TODO."
                else printToDo()
            }
        }
    }

    private fun printToDo(): String {
        val s = StringBuilder()
        for ((i, todo) in all.withIndex()) {
            s += i.toEmojiText()
            s += " "
            s += todo.content
            if (i < all.size) s += "\n"
        }
        return s.toString()
    }
}
