package net.liplum.cmd

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.User
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.addReaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.liplum.FileSystem
import net.liplum.Vars
import net.liplum.util.plusAssign
import net.liplum.util.toEmojiText

@Serializable
data class Note(
    val content: String,
    val timeStamp: Long = System.currentTimeMillis(),
)

object NoteCommand {
    val data = FileSystem.data.resolve("Notes.json")
    val id2Notes = HashMap<Snowflake, ArrayList<Note>>()

    init {
        RegisterTreeCommand("note") {
            +Command("add") { raw, args ->
                val author = raw.author ?: return@Command
                val content = args.joinToString(" ")
                if (content.isBlank()) {
                    raw.channel.createMessage {
                        this.content = "You can't add an empty note."
                        messageReference = raw.id
                    }
                    return@Command
                }
                addNote(author.id, content)
                raw.addReaction(Emojis.ok)
                raw.channel.showAllNotes(author)
            }.addDesc("<content> -- add a note.")
            +Command("list") { raw, args ->
                val author = raw.author ?: return@Command
                raw.addReaction(Emojis.ok)
                raw.channel.showAllNotes(author)
            }.addDesc("list your all notes.")
            +Command("it") { raw, args ->
                val author = raw.author ?: return@Command
                val referenced = raw.referencedMessage
                if (referenced == null) {
                    raw.channel.createMessage {
                        content = "That message was deleted."
                        messageReference = raw.id
                    }
                    return@Command
                }
                val content = referenced.content
                if (content.isBlank()) {
                    raw.channel.createMessage {
                        this.content = "You can't add an empty note."
                        messageReference = raw.id
                    }
                    return@Command
                }
                addNote(author.id, content)
                raw.addReaction(Emojis.ok)
                raw.channel.showAllNotes(author)
            }.addDesc("add the message you referenced..")
        }.mapSelf("list")
            .addDesc("manage your notes.")
    }

    suspend fun MessageChannelBehavior.showAllNotes(user: User) {
        val all = id2Notes.getOrPut(user.id, ::ArrayList)
        createEmbed {
            field("@${user.username}'s notes [${all.size}/${Vars.maxNote}]", inline = true) {
                if (all.isEmpty()) "You have no note."
                else all.printNotes()
            }
        }
    }

    suspend fun loadNotes() {
        withContext(Dispatchers.IO) {
            if (data.isFile) {
                id2Notes.clear()
                try {
                    id2Notes += Json.decodeFromString<Map<Snowflake, ArrayList<Note>>>(data.readText())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun saveNotes() {
        withContext(Dispatchers.IO) {
            if (!data.isDirectory) {
                data.writeText(Json.encodeToString(id2Notes))
            }
        }
    }

    fun List<Note>.printNotes(): String {
        val s = StringBuilder()
        for ((i, note) in this.withIndex()) {
            s += i.toEmojiText()
            s += " "
            s += note.content
            if (i < this.size) s += "\n"
        }
        return s.toString()
    }

    suspend fun addNote(form: Snowflake, content: String): Pair<ArrayList<Note>, Note> {
        val timeStamp = System.currentTimeMillis()
        val notes = id2Notes.getOrPut(form, ::ArrayList)
        if (notes.size >= Vars.maxNote) {
            val earliest = notes.minByOrNull { it.timeStamp }
            if (earliest != null) notes.remove(earliest)
        }
        val note = Note(content, timeStamp)
        notes.add(note)
        notes.distinctBy { it.content }
        saveNotes()
        return Pair(notes, note)
    }
}