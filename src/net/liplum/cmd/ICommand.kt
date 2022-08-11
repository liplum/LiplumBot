package net.liplum.cmd

import dev.kord.core.entity.Message
import net.liplum.cmd.ICommand.Companion.registerSelf
import net.liplum.util.plusAssign

typealias Keyword = String
typealias Executable = suspend (raw: Message, args: List<String>) -> Unit

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
    val executable: Executable,
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
    executable: Executable,
) = Command(keyword, executable).registerSelf()

class TreeCommand(
    override val keyword: Keyword,
) : ICommand {
    override var isHidden = false
    val subCommands = LinkedHashMap<Keyword, ICommand>()
    var description = "No description."
    fun addDesc(description: String) = apply { this.description = description }
    var selfCommand: Executable = { _, _ -> }
    override suspend fun execute(raw: Message, args: List<String>) {
        if (args.isEmpty()) {
            selfCommand(raw, args)
            return
        }
        val sub = args[0]
        subCommands[sub]?.execute(raw, args.slice(1 until args.size))
    }

    fun hidden() = apply { isHidden = true }
    override fun buildHelp(): String {
        val s = StringBuilder()
        s += description
        s += "\n"
        var i = 0
        for ((keyword, cmd) in subCommands) {
            if (!cmd.isHidden) {
                s += "-->**${keyword}** ${cmd.buildHelp()}"
            }
            if (i < subCommands.size - 1)
                s += "\n"
            i++
        }
        return s.toString()
    }

    fun whenSelf(executable: Executable) = apply {
        selfCommand = executable
    }

    fun mapSelf(subName: String) = apply {
        selfCommand = subCommands[subName]!!::execute
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