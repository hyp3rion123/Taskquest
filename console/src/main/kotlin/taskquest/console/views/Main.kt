package taskquest.console.views

import taskquest.console.controllers.CommandFactory
import taskquest.console.controllers.ShowCommand
import taskquest.utilities.controllers.SaveUtils.Companion.restoreData
import taskquest.utilities.controllers.SaveUtils.Companion.saveData
import taskquest.utilities.models.User
import java.io.File
import java.lang.Exception

var currentList = -1

fun main(args: Array<String>) {
    // data stored in a list internally
    // but saved in a file on exit

    val filename = "data.json"
    if (!File(filename).exists()) {
        File(filename).createNewFile()
        val newUser = User()
        saveData(newUser, filename)
    }
    val currentUser = restoreData(filename)
    currentList = currentUser.lastUsedList

    val taskCommands = listOf<String>("add", "del", "show", "edit", "sort")

    println("Welcome to TaskQuest Console.")
    println("Enter 'help' for a detailed description of each supported command.")
    println("")

    if (currentList == -1) {
        println("You have no currently active list.")
    } else {
        println("Your currently active list is the ${currentUser.lists[currentList].title} list.")
        val command = ShowCommand(listOf("show"))
        command.execute(currentUser.lists[currentList])
    }



    if (args.isEmpty()) {

        var curInstr : List<String>?

        // process commands
        while (true) {
            print(">> ")
            curInstr = readLine()?.split(' ')
            if (curInstr == null || curInstr[0].trim().lowercase() == "quit"
                || curInstr[0].trim().lowercase() == "q" || curInstr[0].trim().lowercase() == "exit") {
                break
            } else if (taskCommands.contains(curInstr[0])) {
                val taskCommand = CommandFactory.createTaskComFromArgs(curInstr)
                if (currentList == -1) {
                    println("You have no currently active list. Please select a list.")
                } else {
                    try {
                        taskCommand.execute(currentUser.lists[currentList])
                    } catch (e: Exception) {
                        println("An error occurred.")
                    }
                }
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
                if (currentList == -1) {
                    println("You have no currently active list. Please select a list.")
                } else {
                    try {
                        taskCommand.execute(currentUser.lists[currentList])
                    } catch (e: Exception) {
                        println("An error occurred.")
                    }
                }
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