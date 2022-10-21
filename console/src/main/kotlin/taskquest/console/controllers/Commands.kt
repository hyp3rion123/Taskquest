package taskquest.console.controllers

import taskquest.utilities.models.TaskList
import taskquest.utilities.models.Task
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import taskquest.console.views.currentList

// Factory pattern
// generate a command based on the arguments passed in
object CommandFactory {
    fun createTaskComFromArgs(args: List<String>): TaskCommand =
        when (args[0]) {
            "add" -> AddCommand(args)
            "del" -> DelCommand(args)
            "show" -> ShowCommand(args)
            "edit" -> EditCommand(args)
            "sort" -> SortCommand(args)
            else -> HelpCommand(args)
        }

    fun createTaskListComFromArgs(args: List<String>): TaskListCommand =
        when (args[0]) {
            "listadd" -> AddListCommand(args)
            "listselect" -> SelectListCommand(args)
            "listdel" -> DeleteListCommand(args)
            "listshow" -> ShowListCommand(args)
            "listedit" -> EditListCommand(args)
            else -> HelpCommand(args)
        }

    fun findFirstCommand(args: List<String>, idx: Int, length: Int) : Int {

        val commands = listOf<String>("add", "del", "show", "edit", "sort", "listadd", "listselect", "listdel", "listshow", "listedit", "help")
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
interface TaskCommand {
    fun execute(list: TaskList)
}

class AddCommand(private val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {

        println("You are adding a task to the ${list.title} list.")
        // mandatory parameter for adding a task (title)
        print("Please add a title: ")
        var title = readLine()!!.trim()

        while (title == "") {
            print("Title is mandatory for a task. Please try again: ")
            title = readLine()!!.trim()
        }


        // optional parameters
        println("The following are optional fields you may add to your task. Enter the number associated with any field you would like to add.")
        println("[0] -> Done\n[1] -> Description\n[2] -> Due Date\n[3] -> Priority\n[4] -> Difficulty")
        val validInput = listOf<String>("0", "1", "2", "3", "4")

        var desc = ""
        var dueDate = ""
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
                        continue
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

                    if (!exit) {
                        dueDate = dueDateInput
                    }

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

        list.addItem(title, desc, dueDate, priority, difficulty)
        println("Task $title added successfully.")

    }
}

class DelCommand(private val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {

        if (args.size < 2) {
            println("Please specify a task you would like to delete")
            return
        }

        val taskNum = args[1].toIntOrNull()
        if (taskNum == null) {
            println("Invalid task number entered.")
        } else {
            if (taskNum > list.tasks.size || taskNum <= 0) {
                println("Invalid task number entered.")
            } else {
                if (list.tasks.size == 1) {
                    println("You deleted your only task. Your ${list.title} list is now empty.")
                } else {
                    println("You successfully deleted task ${taskNum}: ${list.tasks[taskNum - 1].title}")
                }
                list.deleteItem(taskNum - 1)
            }
        }
    }
}

class ShowCommand(val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {

        if (list.tasks.size == 0) {
            println("You have no tasks for your ${list.title} list. Create tasks for this list using the add command.")
        } else {
            println("${list.title}:")

            var count = 0
            list.tasks.forEach { println("  - ${++count}. Title: ${it.title}, ${if (it.desc == "") "" else "${it.desc} "}" +
                    "${if (it.dueDate == "") "" else "${it.dueDate} "}${it.dateCreated} ${it.priority ?: ""} " +
                    "${it.difficulty ?: ""} " + if (it.complete) "Complete" else "Incomplete") }
        }

    }
}

class EditCommand(private val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {
        if (args.size < 2) {
            println("Please specify a task you would like to edit")
            return
        }

        val taskNum = args[1].toIntOrNull()
        if (taskNum == null) {
            println("Invalid task number entered.")
        } else {
            if (taskNum > list.tasks.size || taskNum <= 0) {
                println("Invalid task number entered.")
            } else {
                val task : Task = list.tasks[taskNum - 1]
                println("The task being edited in list ${list.title}:")
                println("Title: ${task.title}, ${if (task.desc == "") "" else "${task.desc} "}" +
                        "${if (task.dueDate == "") "" else "${task.dueDate} "}${task.dateCreated} ${task.priority ?: ""} " +
                        "${task.difficulty ?: ""} " + if (task.complete) "Complete" else "Incomplete")
                println("")
                println("Enter the number associated with any field you would like to edit.")
                println("[0] -> Done\n[1] -> Edit Title\n[2] -> Edit Description\n[3] -> Edit Due Date\n[4] -> Edit Priority\n[5] -> Edit Difficulty\n[6] -> Edit Completion Status")
                val validInput = listOf<String>("0", "1", "2", "3", "4", "5", "6")
                var numInt : Int

                while (true) {
                    while (true) {
                        print("Enter a number (0 - 6): ")
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
                            print("Update Title: ")
                            val title = readLine()!!.trim()
                            if (title == "") {
                                println("Title cannot be empty. Title was not updated.")
                            } else {
                                task.title = title
                            }
                        }
                        2 -> {
                            print("Update Description: ")
                            val desc = readLine()!!.trim()
                            task.desc = desc
                        }
                        3 -> {
                            print("Update Due Date (YYYY-MM-DD): ")
                            val dueDateInput = readLine()!!.trim()
                            val userDate = dueDateInput.split("-")

                            if (userDate.size != 3) {
                                println("Invalid date entered. The due date was not successfully updated.")
                                continue
                            }

                            var count = 0
                            var exit = false
                            for (item in userDate) {
                                val dmyInt = item.toIntOrNull()
                                count++
                                if (dmyInt == null) {
                                    println("Invalid date entered. The due date was not successfully updated.")
                                    exit = true
                                    break
                                }
                            }

                            if (!exit) {
                                task.dueDate = dueDateInput
                            }
                        }
                        4 -> {
                            print("Update priority 1 - 3 (where 1 is highest priority, 3 is lowest): ")
                            val priorityNum = readLine()!!.trim()
                            try {
                                task.priority = Priority.values()[priorityNum.toInt() - 1]
                            } catch (e: RuntimeException) {
                                println("Priority was not successfully updated.")
                            }
                        }
                        5 -> {
                            print("Update difficulty 1 - 3 (where 1 is most difficult, 3 is least): ")
                            val difficultyNum = readLine()!!.trim()
                            try {
                                task.difficulty = Difficulty.values()[difficultyNum.toInt() - 1]
                            } catch (e: RuntimeException) {
                                println("Difficulty was not successfully updated.")
                            }
                        }
                        6 -> {
                            print("This task is currently marked as ${if (task.complete) "Complete" else "Incomplete"}. Would you like to change this status (Y/N): ")
                            val change = readLine()!!.trim().lowercase()
                            if (change == "y") {
                                task.complete = !task.complete
                            } else {
                                println("The completion status was not updated.")
                            }
                        }
                    }

                }

                println("Updates for ${task.title} saved successfully.")
            }
        }
    }

}

class SortCommand(private val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {
        if (args.size < 2) {
            println("Please specify a sorting method.")
            return
        }

        val sortMethod = args[1]

        when (sortMethod) {
            "byTitleAsc" -> list.tasks.sortBy { it.title }
            "byTitleDesc" -> list.tasks.sortByDescending { it.title }
            "byDueDateAsc" -> list.tasks.sortBy { it.dueDate }
            "byDueDateDesc" -> list.tasks.sortByDescending { it.dueDate }
            "byDateCreatedAsc" -> list.tasks.sortBy { it.dateCreated }
            "byDateCreatedDesc" -> list.tasks.sortByDescending { it.dateCreated }
            "byPriorityAsc" -> list.tasks.sortByDescending { it.priority }
            "byPriorityDesc" -> list.tasks.sortBy { it.priority }
            "byDifficultyAsc" -> list.tasks.sortByDescending { it.difficulty }
            "byDifficultyDesc" -> list.tasks.sortBy { it.difficulty }
            "byCompletion" -> list.tasks.sortBy { it.complete }
            "default" -> list.tasks.sortBy { it.id }
            else -> println("Sorting method not supported.")
        }

    }

}

interface TaskListCommand {
    fun execute(lists: MutableList<TaskList>)
}

class HelpCommand(val args: List<String>) : TaskCommand, TaskListCommand {
    override fun execute(list: TaskList) {
        println("Usage: todo [add|del|show]")
    }

    override fun execute(lists: MutableList<TaskList>) {
        println("Usage: taskquest command")
        println("Command: ")
        println("\thelp  ->  Usage info")
        println("\tList Commands: ")
        println("\t\tlistshow  ->  Displays all lists along with each lists number, title and description (if exists). Specifies which list is currently active.")
        println("\t\tlistadd  ->  Interactively adds a list.")
        println("\t\tlistselect list_number  ->  Sets the currently active list to list list_number.")
        println("\t\tlistedit list_number  ->  Interactively allows editing of the title and description for list list_number.")
        println("\t\tlistdel list_number  ->  Deletes list list_number.")
        println("")
        println("\tTask Commands: ")
        println("\t\tshow  ->  Displays all tasks in the currently active list as well as each task's number.")
        println("\t\tadd  ->  Interactively adds a task to the currently active list.")
        println("\t\tdel task_number  ->  Deletes task task_number in the currently active list.")
        println("\t\tedit task_number  ->  Interactively allows editing of task task_number in the currently active list.")
        println("\t\tsort sorting_method  ->  Sorts the tasks in the currently active list by sorting_method.")
        println("\t\t     Supported sorting_method: ")
        println("\t\t\tdefault  ->  Sorts the order of the tasks to the order in which they were created.")
        println("\t\t\tbyTitleAsc  ->  Sorts the tasks in lexicographically ascending order by title.")
        println("\t\t\tbyTitleDesc  ->  Sorts the tasks in lexicographically descending order by title.")
        println("\t\t\tbyDueDateAsc  ->  Sorts the tasks by earliest to latest due date.")
        println("\t\t\tbyDueDateDesc  ->  Sorts the tasks by latest to earliest due date.")
        println("\t\t\tbyDateCreatedAsc  ->  Sorts the tasks by earliest to latest date created.")
        println("\t\t\tbyDateCreatedDesc  ->  Sorts the tasks by latest to earliest date created.")
        println("\t\t\tbyPriorityAsc  ->  Sorts the tasks by lowest to highest priority.")
        println("\t\t\tbyPriorityDesc  ->  Sorts the tasks by highest to lowest priority.")
        println("\t\t\tbyDifficultyAsc  ->  Sorts the tasks by easiest to hardest difficulty.")
        println("\t\t\tbyDifficultyDesc  ->  Sorts the tasks by hardest to easiest difficulty.")
        println("\t\t\tbyCompletion  ->  Sorts the tasks so all incomplete tasks appear before complete tasks.")

    }
}

class AddListCommand(private val args: List<String>) : TaskListCommand {
    override fun execute(lists: MutableList<TaskList>) {

        // mandatory parameter for adding a list (title)
        print("Please add a title for this list: ")
        var title = readLine()!!.trim()

        while (title == "") {
            print("Title is mandatory for a list. Please try again: ")
            title = readLine()!!.trim()
        }

        print("Add an optional description for this list (enter return to skip): ")
        val desc = readLine()!!.trim().lowercase()

        lists.add(TaskList(lists.size, title, desc))
        if (currentList == -1) {
            currentList = lists.size - 1
            println("Your currently active list has been changed to the ${title} list.")
        } else {
            println("Your ${title} list has been successfully created.")
        }
    }
}

class SelectListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to select.")
        }

        if (args.size < 2) {
            println("Please specify a list you would like to select")
            return
        }

        if (args[1].toIntOrNull() == null || args[1].toInt() > lists.size || args[1].toInt() <= 0) {
            println("Invalid list number entered.")
        } else {
            currentList = args[1].toInt() - 1
        }
    }
}

class DeleteListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to delete.")
        }

        if (args.size < 2) {
            println("Please specify a list you would like to delete")
            return
        }

        val listNum = args[1].toIntOrNull()
        if (listNum == null) {
            println("Invalid list number entered.")
        } else {
            if (listNum > lists.size || listNum <= 0) {
                println("Invalid list number entered.")
            } else {
                if (currentList + 1 == listNum) {
                    currentList = -1
                    println("You deleted your currently active list. Now you have no active list. ")
                } else {
                    println("You successfully deleted list ${listNum}: ${lists[listNum -1]}")
                }
                lists.removeAt(listNum - 1)
            }
        }

    }
}

class ShowListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to be shown. Create a list using the addlist command.")
        } else {
            var count = 0
            lists.forEach { println("  ${++count}. Title: ${it.title}  ${if (it.desc == "") "" else "Description: ${it.desc} "} ${if (currentList + 1 == count) " <-- active" else ""} ") }
        }

    }

}

class EditListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to edit.")
        }

        if (args.size < 2) {
            println("Please specify a list you would like to edit")
            return
        }

        val listNum = args[1].toIntOrNull()
        if (listNum == null) {
            println("Invalid list number entered.")
        } else {
            if (listNum > lists.size || listNum <= 0) {
                println("Invalid list number entered.")
            } else {
                val list : TaskList = lists[listNum - 1]
                println("The list being edited:\nTitle: ${list.title}, ${if (list.desc == "") "" else "Description: ${list.desc} "}")
                println("")
                println("Enter the number associated with any field you would like to edit.")
                println("[0] -> Done\n[1] -> Edit Title\n[2] -> Edit Description")
                val validInput = listOf<String>("0", "1", "2")
                var numInt : Int

                while (true) {
                    while (true) {
                        print("Enter a number (0 - 2): ")
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
                            print("Update Title: ")
                            val title = readLine()!!.trim()
                            if (title == "") {
                                println("Title cannot be empty. Title was not updated.")
                            } else {
                                list.title = title
                                println("This list's title has been updated.")
                            }
                        }
                        2 -> {
                            print("Update Description: ")
                            val desc = readLine()!!.trim()
                            list.desc = desc
                            println("This list's description has been updated.")
                        }
                    }
                }
            }
        }
    }
}
