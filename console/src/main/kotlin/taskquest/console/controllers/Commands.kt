package taskquest.console.controllers

import taskquest.utilities.models.TaskList
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
            "addlist" -> AddListCommand(args)
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
    fun execute(lists: MutableList<TaskList>)
}

// Setup to only take a title, priority and difficulty from command line
// Accessing lists[0] just for testing purposes
class AddCommand(private val args: List<String>) : Command {
    override fun execute(tasks: MutableList<Task>) {

        // mandatory parameter for adding a task (title)
        print("Please add a title: ")
        val title = readLine()!!.trim()

        // optional parameters
        println("The following are optional fields you may add to your task. Enter the number associated with any field you would like to add.")
        println("[0] -> Done   [1] -> Description   [2] -> Due Date   [3] -> Priority   [4] -> Difficulty")
        val validInput = listOf<String>("0", "1", "2", "3", "4")

        var desc : String? = ""
        var dueDate : String? = ""
        var priority : Priority? = null
        var difficulty : Difficulty? = null

        var numInt : Int

        while (true) {

            while (true) {
                print("Enter a number (0 - 4): ")
                val num = readLine()!!.trim()
                if (validInput.contains(num)) {
                    numInt = num.toInt()
                    break
                } else {
                    print("Try again. ")
                }
            }

            when (numInt) {
                0 -> {
                    break
                }
                1 -> {
                    print("Add a description: ")
                    desc = readLine()!!.trim()
                }
                2 -> {
                    print("Enter a due date (YYYY-MM-DD): ")
                    val dueDateInput = readLine()!!.trim()
                    val userDate = dueDateInput.split("-")

                    if (userDate.size != 3) {
                        println("Invalid date entered. The due date was not successfully set.")
                        break
                    }

                    var count = 0
                    var exit = false
                    for (item in userDate) {
                        val dmyInt = item.toIntOrNull()
                        count++
                        if (dmyInt == null) {
                            println("Invalid date entered. The due date was not successfully set.")
                            exit = true
                            break
                        }
                    }

                    if (exit) break else dueDate = dueDateInput

                }
                3 -> {
                    print("Add priority 1 - 3 (where 1 is highest priority, 3 is lowest): ")
                    val priorityNum = readLine()!!.trim()
                    try {
                        priority = Priority.values()[priorityNum.toInt() - 1]
                    } catch (e: RuntimeException) {
                        println("Priority was not successfully set.")
                    }
                }
                4 -> {
                    print("Add difficulty 1 - 3 (where 1 is most difficult, 3 is least): ")
                    val difficultyNum = readLine()!!.trim()
                    try {
                        difficulty = Difficulty.values()[difficultyNum.toInt() - 1]
                    } catch (e: RuntimeException) {
                        println("Difficulty was not successfully set. ")
                    }
                }
            }

        }

    }
}

class DelCommand(private val args: List<String>) : Command {
    override fun execute(lists: MutableList<TaskList>) {
        lists[0].deleteItem(args[1].toInt())
    }
}

class ShowCommand(val args: List<String>) : Command {
    override fun execute(lists: MutableList<TaskList>) {
        println("${lists[0].title}:")

        lists[0].tasks.forEach { println("  [${it.id}] ${it.title} ${if (it.desc == "") "" else "${it.desc} "}" +
                "${if (it.dueDate == "") "" else "${it.dueDate} "}${it.dateCreated} ${it.priority ?: ""} " +
                "${it.difficulty ?: ""} " + if (it.complete) "Complete" else "Incomplete") }
    }
}

class HelpCommand(val args: List<String>) : Command {
    override fun execute(lists: MutableList<TaskList>) {
        println("Usage: todo [add|del|show]")
    }
}

class AddListCommand(private val args: List<String>) : Command {
    override fun execute(lists: MutableList<TaskList>) {
        lists.add(TaskList(lists.size, args[1]))
    }
}