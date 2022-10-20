package taskquest.console.views

import taskquest.console.controllers.CommandFactory
import taskquest.utilities.controllers.SaveUtils.Companion.restoreData
import taskquest.utilities.controllers.SaveUtils.Companion.saveData

var currentList = -1

fun main(args: Array<String>) {
    // data stored in a list internally
    // but saved in a file on exit

    val filename = "data.json"
    val currentUser = restoreData(filename)
    currentList = currentUser.lastUsedList

    val taskCommands = listOf<String>("add", "del", "show")

    println("Welcome to TaskQuest Console.")
    println("We support interactive mode where you can type and execute your commands one by one.")
    println("We also support argument mode where you can pass your commands via the --args flag.")
    println("")
    println("Your currently active list is the ${currentUser.lists[currentList].title} list")


    if (args.isEmpty()) {

        var curInstr : List<String>?

        // process commands
        while (true) {
            print(">> ")
            curInstr = readLine()?.split(' ')
            if (curInstr == null || curInstr[0].trim() == "quit" || curInstr[0].trim() == "q") {
                break
            } else if (taskCommands.contains(curInstr[0])) {
                val taskCommand = CommandFactory.createTaskComFromArgs(curInstr)
                taskCommand.execute(currentUser.lists[currentList])
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                taskListCommand.execute(currentUser.lists)
            }

            currentUser.lastUsedList = currentList
            saveData(currentUser, filename)
        }
    } else {
        val instructions : List<String> = args.toMutableList()
        val length = instructions.size
        var i = 0

        while (true) {
            i = CommandFactory.findFirstCommand(instructions, i, length)
            if (i == length) break
            val curInstr = instructions.slice(i until length)

            if (taskCommands.contains(curInstr[0])) {
                val taskCommand = CommandFactory.createTaskComFromArgs(curInstr)
                taskCommand.execute(currentUser.lists[currentList])
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                taskListCommand.execute(currentUser.lists)
            }
            i++

            currentUser.lastUsedList = currentList
            saveData(currentUser, filename)
        }

    }

    // save to-do list (json)
    currentUser.lastUsedList = currentList
    saveData(currentUser, filename)
}