package net.liplum

import dev.kord.core.Kord
import net.liplum.cmd.addCommandSystem
import net.liplum.reaction.addCuteReaction
import net.liplum.self.ToDoList
import net.liplum.self.ToDoList.addToDoModule
import net.liplum.self.addChatModule
import net.liplum.util.Token

suspend fun main() {
    val token = Token.load()
    val kord = Kord(token)
    Vars.bot = kord
    Vars.setup()
    addCommandSystem()
    addCuteReaction()
    ToDoList.loadTodo()
    addToDoModule()
    addChatModule()
    kord.login {
    }
}
