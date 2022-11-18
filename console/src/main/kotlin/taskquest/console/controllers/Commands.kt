package taskquest.console.controllers

import taskquest.utilities.models.TaskList
import taskquest.utilities.models.Task
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import taskquest.console.views.currentList
import taskquest.console.views.currentUser
import taskquest.console.views.redoUser
import taskquest.console.views.undoUser
import taskquest.utilities.controllers.DateValidator
import taskquest.utilities.controllers.FunctionClass
import taskquest.utilities.controllers.SaveUtils

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
            "complete" -> CompleteCommand(args)
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

    fun createUserComFromArgs(args: List<String>): UserCommand =
        when (args[0]) {
            "addtags" -> AddTagsCommand(args)
            "deltag" -> DelTagsCommand(args)
            "showtags" -> ShowTagsCommand()
            "wallet" -> ShowCoins()
            "undo" -> UndoCommand()
            "redo" -> RedoCommand()
            else -> HelpCommand(args)
        }

    fun findFirstCommand(args: List<String>, idx: Int, length: Int) : Int {

        val commands = listOf<String>("add", "del", "show", "edit", "sort", "listadd", "listselect", "listdel",
            "listshow", "listedit", "help", "addtags", "deltag", "showtags", "wallet")

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
        println("[0] -> Done\n[1] -> Description\n[2] -> Due Date\n[3] -> Priority\n[4] -> Difficulty\n[5] -> Tags")
        val validInput = listOf<String>("0", "1", "2", "3", "4", "5")

        var desc = ""
        var dueDate = ""
        var priority : Priority? = null
        var difficulty : Difficulty? = null
        var tags : MutableSet<String>? = null

        var numInt : Int

        while (true) {
            while (true) {
                print("Enter a number (0 - 5): ")
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
                    var dueDateInput = readLine()!!.trim()

                    val obj = DateValidator("yyyy-MM-dd")
                    val validDate = obj.validDate(dueDateInput)

                    if (!validDate) {
                        println("Invalid date entered. The due date was not successfully set.")
                    } else {

                        val userDate = dueDateInput.split("-").toMutableList()
                        if (userDate[1].length == 1) {
                            userDate[1] = "0" + userDate[1]
                         }

                        if (userDate[2].length == 1) {
                            userDate[2] = "0" + userDate[2]
                        } else if (userDate[2].length > 2) {
                            userDate[2] = userDate[2].substring(0, 2)
                        }

                        dueDateInput = userDate[0] + "-" + userDate[1] + "-" + userDate[2]
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
                5 -> {
                    if (currentUser.tags.size == 0) {
                        println("You have not created any tags to add.")
                        continue
                    }
                    println("Here are your tags: ")
                    ShowTagsCommand().execute()
                    print("Add tags: ")
                    tags = readLine()?.trim()?.split("\\s+".toRegex())?.toMutableSet()
                    var tagsRemove = mutableSetOf<String>()
                    if (tags != null) {
                        for (tag in tags) {
                            if (!currentUser.tags.contains(tag)) {
                                println("The tag ${tag} does not exist and so was not added to this task.")
                                tagsRemove.add(tag)
                            }
                        }
                        tags.removeAll(tagsRemove)
                    }
                }
            }

        }

        list.addItem(title, desc, dueDate, priority, difficulty, tags)
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
                    println("You deleted your only task titled ${list.tasks[taskNum - 1].title}. Your ${list.title} list is now empty.")
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
            list.tasks.forEach {
                val lead = "  ${++count}. "
                print(lead)
                println("${it.title}:")

                val indent = " ".repeat(lead.length)
                if (it.desc != "") println("${indent}${it.desc}\n")
                println(if (it.complete) "${indent}Status: Complete" else "${indent}Status: Incomplete")
                if (it.priority != null) println("${indent}Priority: ${it.priority}")
                if (it.difficulty != null) println("${indent}Difficulty: ${it.difficulty}")
                if (it.dueDate != "") println("${indent}Due Date: ${it.dueDate}")
                if (it.tags.size != 0) {
                    print("${indent}Tags: ")
                    it.tags.forEach { print("$it ") }
                    println()
                }
                println("${indent}Date Created: ${it.dateCreated}")
                println("")
            }
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
                println("${task.title}:")
                if (task.desc != "") println("${task.desc}\n")
                println(if (task.complete) "Status: Complete" else "Status: Incomplete")
                if (task.priority != null) println("Priority: ${task.priority}")
                if (task.difficulty != null) println("Difficulty: ${task.difficulty}")
                if (task.dueDate != "") println("Due Date: ${task.dueDate}")
                if (task.tags.size != 0) {
                    print("Tags: ")
                    task.tags.forEach { print("$it ") }
                    println()
                }
                println("Date Created: ${task.dateCreated}")
                println("")
                println("Enter the number associated with any field you would like to edit.")
                println("[0] -> Done\n[1] -> Edit Title\n[2] -> Edit Description\n[3] -> Edit Due Date\n[4] -> Edit Priority\n[5] -> Edit Difficulty\n[6] -> Edit Completion Status\n[7] -> Edit Tags")
                val validInput = listOf<String>("0", "1", "2", "3", "4", "5", "6", "7")
                var numInt : Int

                while (true) {
                    while (true) {
                        print("Enter a number (0 - 7): ")
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
                            var dueDateInput = readLine()!!.trim()

                            val obj = DateValidator("yyyy-MM-dd")
                            val validDate = obj.validDate(dueDateInput)

                            if (!validDate) {
                                println("Invalid date entered. The due date was not successfully set.")
                            } else {

                                val userDate = dueDateInput.split("-").toMutableList()

                                if (userDate[2].length == 1) {
                                    userDate[2] = "0" + userDate[2]
                                } else if (userDate[2].length > 2) {
                                    userDate[2] = userDate[2].substring(0, 2)
                                }

                                if (userDate[1].length == 1) {
                                    userDate[1] = "0" + userDate[1]
                                }

                                dueDateInput = userDate[0] + "-" + userDate[1] + "-" + userDate[2]
                                task.dueDate = dueDateInput
                            }
                        }
                        4 -> {
                            print("Update priority 1 - 3 (where 1 is highest priority, 3 is lowest): ")
                            val priorityNum = readLine()!!.trim()
                            try {
                                task.priority = Priority.values()[priorityNum.toInt() - 1]
                                task.calcCoinValue()
                            } catch (e: RuntimeException) {
                                println("Priority was not successfully updated.")
                            }
                        }
                        5 -> {
                            print("Update difficulty 1 - 3 (where 1 is most difficult, 3 is least): ")
                            val difficultyNum = readLine()!!.trim()
                            try {
                                task.difficulty = Difficulty.values()[difficultyNum.toInt() - 1]
                                task.calcCoinValue()
                            } catch (e: RuntimeException) {
                                println("Difficulty was not successfully updated.")
                            }
                        }
                        6 -> {
                            print("This task is currently marked as ${if (task.complete) "Complete" else "Incomplete"}. Would you like to change this status (Y/N): ")
                            val change = readLine()!!.trim().lowercase()
                            if (change == "y") {
                                task.complete = !task.complete
                                if (task.complete == true && task.completeOnce == false) {
                                    currentUser.completeTask(task)
                                    val coinValue = (task.rewardCoins * currentUser.multiplier).toInt()
                                    println("Congratulations on completing ${task.title}! You have earned $coinValue coin${if (coinValue > 1) "s" else ""}!")
                                } else if (task.complete == true) {
                                    println("This task is now marked as complete, but you have already been rewarded for completing this task.")
                                }
                            } else {
                                println("The completion status was not updated.")
                            }
                        }
                        7 -> {
                            if (currentUser.tags.size == 0) {
                                println("You have not created any tags to add.")
                                continue
                            }
                            println("Here are your available tags: ")
                            ShowTagsCommand().execute()
                            if (task.tags.size == 0) {
                                println("You have no tags for this task. Add some!")
                            }
                            print("Enter tags for this task: ")
                            var tags = readLine()?.trim()?.split("\\s+".toRegex())?.toMutableSet()
                            var tagsRemove = mutableSetOf<String>()
                            if (tags != null) {
                                for (tag in tags) {
                                    if (!currentUser.tags.contains(tag)) {
                                        println("The tag ${tag} does not exist and so was not added to this task.")
                                        tagsRemove.add(tag)
                                    }
                                }
                                tags.removeAll(tagsRemove)
                                task.tags = tags
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

        FunctionClass.sortTasksBy(args[1], list)

    }

}

interface TaskListCommand {
    fun execute(lists: MutableList<TaskList>)
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

        currentUser.addList(title, desc)

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
            return
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
            return
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
                    println("You successfully deleted list ${listNum}: ${lists[listNum - 1].title}")
                }
                lists.removeAt(listNum - 1)

                if (lists.size == 0) {
                    currentUser.nextId = 0
                    currentList = -1
                } else if (listNum - 1 < currentList) {
                    currentList--
                }
            }

        }

    }
}

class ShowListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to be shown. Create a list using the listadd command.")
        } else {
            println("Lists:")
            var count = 0
            lists.forEach {
                val lead = "  ${++count}. "
                print(lead)
                println("Name: ${it.title}${if (currentList + 1 == count) " <-- active" else ""}")

                val indent = " ".repeat(lead.length)
                if (it.desc != "") println("${indent}Description: ${it.desc}")
                println("")
            }
        }

    }

}

class EditListCommand(private val args: List<String>) : TaskListCommand {

    override fun execute(lists: MutableList<TaskList>) {

        if (lists.size == 0) {
            println("You have no lists to edit.")
            return
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
                println("The list being edited: ")
                println("Name: ${list.title}")
                if (list.desc != "") println("Description: ${list.desc}")
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

interface UserCommand {
    fun execute()

}

class AddTagsCommand(private val args: List<String>) : UserCommand {

    override fun execute() {

        if (args.size < 3) {
            println("Please specify a number of tags you would like to add, as well as those tags names.")
            return
        }

        var numTags = args[1].toIntOrNull()
        if (numTags == null || numTags <= 0) {
            println("Invalid number of tags being added.")
            return
        }

        var count = 1
        while (numTags != 0 && count <= args.size - 2) {
            currentUser.tags.add(args[count + 1])
            count++
            numTags--
        }

    }

}

class DelTagsCommand(private val args: List<String>) : UserCommand {

    override fun execute() {

        if (args.size < 2) {
            println("Please specify a tag you would like to delete.")
            return
        }

        val tagName = args[1]

        if (currentUser.tags.contains(tagName)) {
            currentUser.tags.remove(tagName)
            println("Tag $tagName successfuly deleted")
        } else {
            println("Tag $tagName does not exist")
        }

    }

}

class ShowTagsCommand() : UserCommand {

    override fun execute() {

        if (currentUser.tags.size == 0) {
            println("You have no tags to display. Create tags for yourself using the addtags command.")
        } else {
            var count = 0
            currentUser.tags.forEach {
                val lead = "  ${++count}. "
                print(lead)
                println(it)
            }

        }

    }

}

class ShowCoins() : UserCommand {

    override fun execute() {
        println("You have ${currentUser.wallet} coin${if (currentUser.wallet > 1) "s " else " "}in your wallet.")
    }

}

class HelpCommand(val args: List<String>) : TaskCommand, TaskListCommand, UserCommand {
    override fun execute(list: TaskList) {
        println("Invalid command. Type help for information on valid commands.")
    }

    override fun execute(lists: MutableList<TaskList>) {
        println("Invalid command. Type help for information on valid commands.")
    }

    override fun execute() {
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
        println("")
        println("\tUser Commands: ")
        println("\t\taddtags num_tags  ->  Adds num_tags to a users tags. Specify the names of the tags in this command as well (ie addtags 2 tag1 tag2)")
        println("\t\tdeltag tag_name  ->  Deletes tag tag_name.")
        println("\t\tshowtags  ->  Displays a users tags.")
        println("\t\twallet  ->  Displays the number of coins one has.")
    }
}

class CompleteCommand(private val args: List<String>) : TaskCommand {
    override fun execute(list: TaskList) {

        if (args.size < 2) {
            println("Please specify a task you would like to mark as complete/incomplete")
            return
        }

        val taskNum = args[1].toIntOrNull()
        if (taskNum == null) {
            println("Invalid task number entered.")
        } else {
            if (taskNum > list.tasks.size || taskNum <= 0) {
                println("Invalid task number entered.")
            } else {
                val task = list.tasks[taskNum - 1]
                task.complete = !task.complete
                if (task.complete == true && task.completeOnce == false) {
                    currentUser.completeTask(task)
                    val coinValue = (task.rewardCoins * currentUser.multiplier).toInt()
                    println("Congratulations on completing ${task.title}! You have earned $coinValue coin${if (coinValue > 1) "s" else ""}!")
                } else if (task.complete == true) {
                    println("${task.title} is now marked as complete, but you have already been rewarded for completing this task.")
                }
            }
        }
    }
}

class UndoCommand() : UserCommand {

    override fun execute() {
        if (undoUser != null) {
            redoUser = SaveUtils.cloneUserData(currentUser)
            currentUser = SaveUtils.cloneUserData(undoUser!!)
            undoUser = null
            println("Most recent change undone.")
        } else {
            println("No changes to undo!")
        }
    }

}

class RedoCommand() : UserCommand {

    override fun execute() {
        if (redoUser != null) {
            undoUser = SaveUtils.cloneUserData(currentUser)
            currentUser = SaveUtils.cloneUserData(redoUser!!)
            redoUser = null
            println("Most recent change redone.")
        } else {
            println("No changes to redo!")
        }
    }

}
