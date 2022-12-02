package taskquest.console.views

import taskquest.console.controllers.CommandFactory
import taskquest.console.controllers.ShowCommand
import taskquest.console.controllers.SignInCommandFactory
import taskquest.utilities.controllers.CloudUtils
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.User
import java.lang.Exception
import java.net.ConnectException
import kotlin.system.exitProcess

var undoUser: User? = null
var currentUser = User(0)
var redoUser: User? = null
var currentList = -1
var offline = false

fun main(args: Array<String>) {
    val taskCommands = listOf<String>("add", "del", "show", "edit", "sort", "complete")
    val userCommands = listOf<String>("addtags", "deltag", "showtags", "wallet", "help", "undo", "redo")

    // interactive mode
    if (args.isEmpty()) {
        TaskQuestLogo.FirstConfig.printLogo()

        println("Welcome to TaskQuest Console.")
        println("Would you like to login, register for a new account, or work offline? (login/register/offline/delete).")

        val loginCommands = listOf<String>("login", "register", "offline", "delete", "help")
        while (true) {
            print(">> ")

            val curInstr : List<String>? = readLine()?.trim()?.split("\\s+".toRegex())
            if (curInstr == null || curInstr[0].trim().lowercase() == "quit"
                || curInstr[0].trim().lowercase() == "q" || curInstr[0].trim().lowercase() == "exit") {
                exitProcess(0)
            } else if (loginCommands.contains(curInstr[0])) {
                val loginCommand = SignInCommandFactory.createFromArgs(curInstr)
                try {
                    if (loginCommand.execute()) {
                        break
                    }
                } catch (e: Exception) {
                    println("An error occurred.")
                    println("Please try again.")
                }
            } else {
                println("Invalid command. Type help for information on valid commands.")
            }
        }

        currentList = currentUser.lastUsedList

        println("Enter 'help' for a detailed description of each supported command.")
        println("")

        if (currentList == -1) {
            println("You have no currently active list.")
        } else {
            println("Your currently active list is the ${currentUser.lists[currentList].title} list.")
            val command = ShowCommand(listOf("show"))
            command.execute(currentUser.lists[currentList])
        }

        var curInstr : List<String>?

        // process commands
        while (true) {
            print(">> ")
            curInstr = readLine()?.trim()?.split("\\s+".toRegex())
            if (curInstr == null || curInstr[0].trim().lowercase() == "quit"
                || curInstr[0].trim().lowercase() == "q" || curInstr[0].trim().lowercase() == "exit") {
                break
            } else if (taskCommands.contains(curInstr[0])) {
                undoUser = SaveUtils.cloneUserData(currentUser)
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
            } else if (userCommands.contains(curInstr[0])) {
                val userCommand = CommandFactory.createUserComFromArgs(curInstr)
                try {
                    userCommand.execute()
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                try {
                    taskListCommand.execute(currentUser.lists)
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            }

            currentUser.lastUsedList = currentList
            if (!offline) {
                try {
                    CloudUtils.updateUser(currentUser)
                } catch (_: ConnectException) {
                    println("Cloud server could not be reached; data not saved in cloud.")
                }
            } else {
                SaveUtils.saveUserData(currentUser)
            }
        }
    } else {
        // direct from CLI mode
        currentUser = SaveUtils.restoreUserData()
        currentList = currentUser.lastUsedList
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
            } else if (userCommands.contains(curInstr[0])) {
                val userCommand = CommandFactory.createUserComFromArgs(curInstr)
                try {
                    userCommand.execute()
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            } else {
                val taskListCommand = CommandFactory.createTaskListComFromArgs(curInstr)
                try {
                    taskListCommand.execute(currentUser.lists)
                } catch (e: Exception) {
                    println("An error occurred.")
                }
            }
            i++

            currentUser.lastUsedList = currentList
            SaveUtils.saveUserData(currentUser)
        }

    }

    // save to-do list (json)
    currentUser.lastUsedList = currentList
    if (!offline) {
        try {
            CloudUtils.updateUser(currentUser)
        } catch (_: ConnectException) {
            println("Cloud server could not be reached; data not saved in cloud.")
        }
    } else {
        SaveUtils.saveUserData(currentUser)
    }
}