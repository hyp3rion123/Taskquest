package taskquest.console.controllers

import org.junit.jupiter.api.Test
import taskquest.utilities.models.*

internal class CommandsTest {

    @Test
    fun commandFactory() {
        // basic commands
        val addString = listOf("add")
        val addCommand = CommandFactory.createTaskComFromArgs(addString)
        assert(addCommand is AddCommand)

        val delString = listOf("del", "1")
        val delCommand = CommandFactory.createTaskComFromArgs(delString)
        assert(delCommand is DelCommand)

        val showString = listOf("show")
        val showCommand = CommandFactory.createTaskComFromArgs(showString)
        assert(showCommand is ShowCommand)

        // multiple ways to invoke help
        val helpString1 = listOf("")
        val helpCommand1 = CommandFactory.createTaskComFromArgs(helpString1)
        assert(helpCommand1 is HelpCommand)

        val helpString2 = listOf("help")
        val helpCommand2 = CommandFactory.createTaskListComFromArgs(helpString2)
        assert(helpCommand2 is HelpCommand)

        // unknown commands/arguments also invoke help
        val unknownString1 = listOf("unknown")
        val unknownCommand1 = CommandFactory.createTaskComFromArgs(unknownString1)
        assert(unknownCommand1 is HelpCommand)

        val unknownString2 = listOf("unknown", "unknown", "unknown")
        val unknownCommand2 = CommandFactory.createTaskComFromArgs(unknownString2)
        assert(unknownCommand2 is HelpCommand)
    }


    @Test
    fun showCommand() {
        val list = TaskList(0, "Test List")
        val command = ShowCommand(listOf("show"))
        command.execute(list)
        assert(list.tasks.size == 0)
    }
}