package net.liplum.cmd

import dev.kord.core.entity.Message
import net.liplum.cmd.ICommand.Companion.registerSelf
import net.liplum.util.plusAssign

typealias Keyword = String

interface ICommand {
    val keyword: Keyword
    suspend fun execute(raw: Message, args: List<String>)
    fun buildHelp(): String = "No description."

    companion object {
        val allCommands = HashMap<Keyword, ICommand>()
        fun ICommand.registerSelf() = apply {
            allCommands[keyword.lowercase()] = this
        }

        fun match(keyword: Keyword): ICommand? =
            allCommands[keyword.lowercase()]

        fun buildAllHelp(): String {
            val s = StringBuilder()
            for ((keyword, cmd) in allCommands) {
                s += "!${keyword}:"
                s += cmd.buildHelp()
                s += "\n"
            }
            return s.toString()
        }
    }
}

class Command(
    override val keyword: Keyword,
    val executable: suspend (raw: Message, args: List<String>) -> Unit,
) : ICommand {
    var help = ""
    override suspend fun execute(raw: Message, args: List<String>) {
        executable(raw, args)
    }

    fun addHelp(help: String): Command = apply { this.help = help }
}

class TreeCommand(
    override val keyword: Keyword,
) : ICommand {
    val subCommands = HashMap<Keyword, ICommand>()

    init {
        registerSelf()
    }

    override suspend fun execute(raw: Message, args: List<String>) {
        if (args.isEmpty()) return
        val sub = args[0]
        subCommands[sub]?.execute(raw, args.slice(1 until args.size))
    }

    companion object {
        operator fun invoke(keyword: Keyword, config: TreeCommandSpec.() -> Unit) =
            TreeCommand(keyword).TreeCommandSpec().apply(config)
    }

    inner class TreeCommandSpec {
        operator fun ICommand.unaryPlus() {
            subCommands[this.keyword] = this
        }
    }
}