package net.liplum.cmd

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createMessage
import kotlinx.coroutines.delay
import net.liplum.Vars
import net.liplum.note.Note

object NoteCommand {
    val id2Notes = HashMap<Snowflake, ArrayList<Note>>()

    init {
        RegisterTreeCommand("note") {
            +Command("add") { raw, args ->
                val note = args.joinToString(" ")
                val authorID = raw.author?.id
                if (authorID != null) {
                    addNote(authorID, note)
                    val tip = raw.channel.createMessage {
                        content = "Your note is added."
                        messageReference = raw.id
                    }
                    delay(5000)
                    tip.delete()
                }
            }
            +Command("list") { raw, args ->
                val authorID = raw.author?.id
                if (authorID != null) {
                    val notes = id2Notes.getOrPut(authorID, ::ArrayList)
                    if (notes.isEmpty()) {
                        raw.channel.createMessage {
                            content = "You have no note."
                        }
                    } else {
                        val allNotes = notes.joinToString("\n") { it.content }
                        raw.channel.createMessage {
                            content = allNotes
                        }
                    }
                    delay(5000)
                    raw.delete()
                }
            }
        }
    }

    fun addNote(form: Snowflake, content: String) {
        val timeStamp = System.currentTimeMillis()
        val notes = id2Notes.getOrPut(form, ::ArrayList)
        if (notes.size >= Vars.maxNote) {
            val earliest = notes.minByOrNull { it.timeStamp }
            if (earliest != null) notes.remove(earliest)
        }
        notes.add(Note(content, timeStamp))
        notes.distinctBy { it.content }
    }
}