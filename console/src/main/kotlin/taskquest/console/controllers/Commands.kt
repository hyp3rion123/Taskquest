package taskquest.console.controllers

import taskquest.utilities.models.Task
import taskquest.utilities.models.add
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority

// Factory pattern
// generate a command based on the arguments passed in
object CommandFactory {
    fun createFromArgs(args: List<String>): Command =
        when (args[0]) {
            "add" -> AddCommand(args)
            "del" -> DelCommand(args)
            "show" -> ShowCommand(args)
            else -> HelpCommand(args)
        }

    fun findFirstCommand(args: List<String>, idx: Int, length: Int) : Int {

        val commands = listOf<String>("add", "del", "show", "help")
        for (i in idx until length) {
            if (commands.contains(args[i])) {
                return i
            }
        }

        return length
    }
}

// Command pattern
// represents all valid commands that can be issued by the user
// any functionality for a given command should be contained in that class
interface Command {
    fun execute(tasks: MutableList<Task>)
}

// Setup to only take a title, priority and difficulty from command line
class AddCommand(private val args: List<String>) : Command {
    private val priority = enumValueOf<Priority>(args[2])
    private val difficulty = enumValueOf<Difficulty>(args[3])
    override fun execute(tasks: MutableList<Task>) {
        tasks.add(args[1], priority, difficulty)
    }
}

class DelCommand(private val args: List<String>) : Command {
    override fun execute(tasks: MutableList<Task>) {
        tasks.removeIf { it.id == args[1].toInt() }
    }
}

class ShowCommand(val args: List<String>) : Command {
    override fun execute(tasks: MutableList<Task>) {
        tasks.forEach { println("[${it.id}] ${it.title} ${if (it.desc == "") "" else "${it.desc} "}" +
                "${if (it.dueDate == "") "" else "${it.dueDate} "}${it.dateCreated} ${it.priority ?: ""} " +
                "${it.difficulty ?: ""} " + if (it.complete) "Complete" else "Incomplete") }
    }
}

class HelpCommand(val args: List<String>) : Command {
    override fun execute(tasks: MutableList<Task>) {
        println("Usage: todo [add|del|show]")
    }
}