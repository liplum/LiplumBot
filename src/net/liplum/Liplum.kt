package net.liplum

import dev.kord.core.Kord
import net.liplum.cmd.addCommandSystem
import net.liplum.util.Token

suspend fun main() {
    val token = Token.load()
    val kord = Kord(token)
    Vars.bot = kord
    addCommandSystem()
    //addCuteReaction()
    kord.login {
    }
}
