package net.liplum.self

import dev.kord.core.any
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.edit
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.modify.embed
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import io.ktor.util.collections.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.liplum.FileSystem
import net.liplum.Guilds
import net.liplum.Vars
import net.liplum.util.plusAssign
import net.liplum.util.toEmoji
import net.liplum.util.toEmojiText

@Serializable
data class ToDo(
    val content: String,
    val timeStamp: Long = System.currentTimeMillis(),
)

object ToDoList {
    val all = ConcurrentList<ToDo>()
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
                data.writeText(Json.encodeToString(all.toList()))
            }
        }
    }

    suspend fun addToDoModule() {
        Vars.bot.on<MessageCreateEvent> {
            val userID = message.author?.id
            if (userID != Guilds.User.liplum && userID != kord.selfId) return@on
            val content = message.content
            if (content.length > Vars.maxToDoCommandCount) return@on
            val lowercase = content.lowercase().trim()
            val done = ArrayList<Int>()
            val snapshot = all.toList()
            if (lowercase == "my todo") {
                message.addReaction(Emojis.ok)
                val listMsg = message.channel.displayToDo(snapshot)
                val count = all.size
                for (i in all.indices)
                    listMsg.addReaction(i.toEmoji())
                delay(Vars.todoListReactionTime)
                listMsg.addReaction(Emojis.lock)
                if (count != all.size) return@on
                var finished = -1
                for (i in all.indices) {
                    val reacted = listMsg.getReactors(i.toEmoji()).any { it.id == userID }
                    if (reacted) {
                        if (tryFinishToDo(i, done)) {
                            finished = i
                            break
                        }
                    }
                }
                if (finished != -1) {
                    saveToDo()
                    listMsg.edit {
                        embeds?.clear()
                        embed {
                            addToDoListEmbed(snapshot, done)
                        }
                    }
                }
            } else if (lowercase == "todo it" || lowercase == "todo this") {
                val todo = message.referencedMessage?.content
                if (todo != null && todo.isNotBlank()) {
                    all.add(ToDo(todo))
                    saveToDo()
                    message.addReaction(Emojis.ok)
                    message.channel.displayToDo(all)
                } else {
                    message.addReaction(Emojis.x)
                }
            } else if (tryFinishToDo(lowercase, "done", done)) {
                message.addReaction(Emojis.ok)
                saveToDo()
                message.channel.displayToDo(snapshot, done)
            } else if (tryFinishToDo(lowercase, "fixed", done)) {
                message.addReaction(Emojis.ok)
                saveToDo()
                message.channel.displayToDo(snapshot, done)
            } else if (tryFinishToDo(lowercase, "resolved", done)) {
                message.addReaction(Emojis.ok)
                saveToDo()
                message.channel.displayToDo(snapshot, done)
            } else if (tryFinishToDo(lowercase, "finished", done)) {
                saveToDo()
                message.addReaction(Emojis.ok)
                message.channel.displayToDo(snapshot, done)
            } else if (lowercase.length > 5 && lowercase.startsWith("todo:")) {
                val todo = content.substring(5)
                if (todo.isNotBlank()) {
                    all.add(ToDo(todo))
                    saveToDo()
                    message.addReaction(Emojis.ok)
                    message.channel.displayToDo(all)
                } else {
                    message.addReaction(Emojis.x)
                }
            }
        }
    }

    private fun tryFinishToDo(
        full: String,
        cmd: String,
        done: MutableList<Int>,
    ): Boolean {
        var finished = false
        if (all.isNotEmpty() && full.startsWith(cmd)) {
            val indexFull = full.removePrefix(cmd).trim()
            if (indexFull == "all") {
                done += all.indices
                all.clear()
                finished = true
            } else {
                val indices = indexFull.split(",")
                for (indexStr in indices) {
                    indexStr.trim().toIntOrNull()?.let {
                        if (it in all.indices) {
                            done += it
                            all.removeAt(it)
                            finished = true
                        }
                    }
                }
            }
        }
        return finished
    }

    private fun tryFinishToDo(
        index: Int,
        done: MutableList<Int>,
    ): Boolean {
        var finished = false
        if (all.isNotEmpty()) {
            done += index
            all.removeAt(index)
            finished = true
        }
        return finished
    }

    private suspend fun MessageChannelBehavior.displayToDo(
        snapshot: List<ToDo>,
        done: List<Int> = emptyList(),
    ) = createEmbed {
        addToDoListEmbed(snapshot, done)
    }

    private fun EmbedBuilder.addToDoListEmbed(
        snapshot: List<ToDo>,
        done: List<Int> = emptyList(),
    ) = field {
        name = "Liplum's TODOs"
        inline = true
        value = if (snapshot.isEmpty()) "You have no TODO."
        else printToDo(snapshot, done)
    }

    private fun printToDo(
        snapshot: List<ToDo>,
        done: List<Int>,
    ): String {
        val s = StringBuilder()
        for ((i, todo) in snapshot.withIndex()) {
            if (i in done) {
                s += "~~"
                s += Emojis.whiteCheckMark.unicode
                s += " "
                s += todo.content
                s += "~~"
            } else {
                s += i.toEmojiText()
                s += " "
                s += todo.content
            }
            if (i < all.size) s += "\n"
        }
        return s.toString()
    }
}
