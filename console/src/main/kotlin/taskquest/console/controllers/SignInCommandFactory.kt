package taskquest.console.controllers

import com.fasterxml.jackson.module.kotlin.readValue
import taskquest.console.views.*
import taskquest.utilities.controllers.CloudUtils
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.User

object SignInCommandFactory {
    fun createFromArgs(args: List<String>): SignInCommand =
        when (args[0]) {
            "login" -> LoginCommand()
            "register" -> RegisterCommand()
            "offline" -> OfflineCommand()
            "delete" -> DeleteCommand()
            else -> LoginHelpCommand()
        }
}

interface SignInCommand {
    fun execute(): Boolean
}

class LoginCommand() : SignInCommand {

    override fun execute(): Boolean {
        print("Please enter your username: ")
        val username = readLine()!!.trim()

        print("Please enter your password: ")
        val password = readLine()!!.trim()

        println()

        val res = CloudUtils.login(username, password)
        if (res == "") {
            println("Invalid login credentials. Please try again.")
            return false
        } else {
            currentUser = SaveUtils.mapper.readValue<User>(res)

            if (currentUser.name == "") {
                println("Sorry, we didn't catch your name last name! What is your name?: ")
                val name = readLine()!!.trim()

                println()

                currentUser.name = name
                println("Welcome $name! Thanks for using TaskQuest!")
            } else {
                println("Welcome back ${currentUser.name}!")
            }
            return true
        }
    }

}

class RegisterCommand() : SignInCommand {

    override fun execute(): Boolean {
        println("We're delighted you'd like to register!")
        print("Please enter your desired username: ")
        val username = readLine()!!.trim()

        print("Please enter your desired password: ")
        val password = readLine()!!.trim()

        println()

        val res = CloudUtils.createUser(username, password)
        if (res == "") {
            println("I'm so sorry, but that username is already in use.")
            println("Please try again with a different username.")
            return false
        } else {
            currentUser = SaveUtils.mapper.readValue<User>(res)
            println("Thanks so much for registering!")
            print("Would you please tell us your name?: ")
            val name = readLine()!!.trim()

            println()

            currentUser.name = name
            println("Welcome $name! We hope you enjoy using TaskQuest!")
            return true
        }
    }

}

class OfflineCommand() : SignInCommand {

    override fun execute(): Boolean {
        println("You're working offline, any work done here will not be saved in the cloud, and will not be accessible by your other devices.")
        currentUser = SaveUtils.restoreUserData()
        offline = true

        if (currentUser.name == "") {
            println("What is your name?: ")
            val name = readLine()!!.trim()

            println()

            currentUser.name = name
            println("Welcome $name! We hope you enjoy using TaskQuest!")
        } else {
            println("Welcome back ${currentUser.name}!")
        }

        return true
    }

}

class DeleteCommand() : SignInCommand {

    override fun execute(): Boolean {
        println("WARNING: Deleting your account is irreversible.")
        print("Please enter the username of the account you wish to delete: ")
        val username = readLine()!!.trim()

        print("Please enter the password: ")
        val password = readLine()!!.trim()

        println()

        val res = CloudUtils.deleteUser(username, password)
        if (res == "") {
            println("Invalid login credentials or account did not exist.")
            println("Please try again.")
        } else {
            println("Account $username successfully deleted.")
            println("We're sorry to see you go! :(")
        }

        return false
    }

}

class LoginHelpCommand() : SignInCommand {

    override fun execute(): Boolean {
        println("Command: ")
        println("\tlogin  ->  Login to a TaskQuest account")
        println("\tregister  ->  Register for a new TaskQuest account")
        println("\toffline  ->  Work offline without an account and cloud saving")
        println("\thelp  ->  Usage info")
        return false
    }
}
