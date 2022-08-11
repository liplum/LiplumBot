package net.liplum.cmd

import dev.kord.core.entity.Message
import net.liplum.cmd.ICommand.Companion.registerSelf
import net.liplum.util.plusAssign

typealias Keyword = String

interface ICommand {
    val keyword: Keyword
    val isHidden: Boolean get() = false
    suspend fun execute(raw: Message, args: List<String>)
    fun buildHelp(): String = "No description."

    companion object {
        val allCommands = HashMap<Keyword, ICommand>()
        fun <T : ICommand> T.registerSelf() = apply {
            allCommands[keyword.lowercase()] = this
        }

        fun match(keyword: Keyword): ICommand? =
            allCommands[keyword.lowercase()]

        fun buildAllHelp(): String {
            val s = StringBuilder()
            for ((keyword, cmd) in allCommands) {
                if (cmd.isHidden) continue
                s += "**!${keyword}** "
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
    var description = "No description."
    override var isHidden = false
    override suspend fun execute(raw: Message, args: List<String>) {
        executable(raw, args)
    }

    override fun buildHelp() = description
    fun addDesc(description: String) = apply { this.description = description }
    fun hidden() = apply { isHidden = true }
}

fun RegisterCommand(
    keyword: Keyword,
    executable: suspend (raw: Message, args: List<String>) -> Unit,
) = Command(keyword, executable).registerSelf()

class TreeCommand(
    override val keyword: Keyword,
) : ICommand {
    override var isHidden = false
    val subCommands = HashMap<Keyword, ICommand>()
    var description = "No description."
    fun addDesc(description: String) = apply { this.description = description }
    override suspend fun execute(raw: Message, args: List<String>) {
        if (args.isEmpty()) return
        val sub = args[0]
        subCommands[sub]?.execute(raw, args.slice(1 until args.size))
    }

    fun hidden() = apply { isHidden = true }
    override fun buildHelp(): String {
        val s = StringBuilder()
        s += description
        return s.toString()
    }

    companion object {
        operator fun invoke(keyword: Keyword, config: TreeCommandSpec.() -> Unit) =
            TreeCommand(keyword).TreeCommandSpec().apply(config)
    }

    inner class TreeCommandSpec {
        operator fun ICommand.unaryPlus() {
            subCommands[this.keyword] = this
        }

        fun register() = registerSelf()
    }
}

object RegisterTreeCommand {
    operator fun invoke(keyword: Keyword, config: TreeCommand.TreeCommandSpec.() -> Unit) =
        TreeCommand(keyword).TreeCommandSpec().apply(config).register()
}