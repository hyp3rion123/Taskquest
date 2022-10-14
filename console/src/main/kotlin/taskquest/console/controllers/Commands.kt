package taskquest.console.controllers

import taskquest.utilities.models.Item
import taskquest.utilities.models.add

// Factory pattern
// generate a command based on the arguments passed in
object CommandFactory {
    fun createFromArgs(args: Array<String>): Command = if (args.isEmpty()) {
        HelpCommand(args)
    } else {
        when (args[0]) {
            "add" -> AddCommand(args)
            "del" -> DelCommand(args)
            "show" -> ShowCommand(args)
            else -> HelpCommand(args)
        }
    }
}

// Command pattern
// represents all valid commands that can be issued by the user
// any functionality for a given command should be contained in that class
interface Command {
    fun execute(items: MutableList<Item>)
}

class AddCommand(val args: Array<String>) : Command {
    override fun execute(items: MutableList<Item>) {
        items.add(args[1])
    }
}

class DelCommand(val args: Array<String>) : Command {
    override fun execute(items: MutableList<Item>) {
        items.removeIf { it.id == args[1].toInt() }
    }
}

class ShowCommand(val args: Array<String>) : Command {
    override fun execute(items: MutableList<Item>) {
        items.forEach { println("[${it.id}] ${it.text}") }
    }
}

class HelpCommand(val args: Array<String>) : Command {
    override fun execute(items: MutableList<Item>) {
        println("Usage: todo [add|del|show]")
    }
}