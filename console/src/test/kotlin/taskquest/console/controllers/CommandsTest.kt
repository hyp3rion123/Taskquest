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
        val helpCommand1 = CommandFactory.createTaskListComFromArgs(helpString1)
        assert(helpCommand1 is HelpCommand)

        val helpString2 = listOf("help")
        val helpCommand2 = CommandFactory.createTaskListComFromArgs(helpString2)
        assert(helpCommand2 is HelpCommand)

        // unknown commands/arguments also invoke help
        val unknownString1 = listOf("unknown")
        val unknownCommand1 = CommandFactory.createTaskListComFromArgs(unknownString1)
        assert(unknownCommand1 is HelpCommand)

        val unknownString2 = listOf("unknown", "unknown", "unknown")
        val unknownCommand2 = CommandFactory.createTaskListComFromArgs(unknownString2)
        assert(unknownCommand2 is HelpCommand)
    }


    @Test
    fun showCommand() {
        val list = TaskList(0, "Test List")
        val command = ShowCommand(listOf("show"))
        command.execute(list)
        assert(list.tasks.size == 0)
    }

    @Test
    fun sortByTitleAscCommand() {
        val list = TaskList(0, "Test List")
        list.addItem("banana")
        list.addItem("apple")
        val command = SortCommand(listOf("sort", "byTitleAsc"))
        command.execute(list)
        assert(list.tasks[0].title == "apple")
    }

    @Test
    fun sortByTitleDescCommand() {
        val list = TaskList(0, "Test List")
        list.addItem("apple")
        list.addItem("banana")
        val command = SortCommand(listOf("sort", "byTitleDesc"))
        command.execute(list)
        assert(list.tasks[0].title == "banana")
    }

    @Test
    fun sortByDueDateAscCommand() {
        val list = TaskList(0, "Test List")
        list.addItem(title="banana", dueDate="2022-01-02")
        list.addItem(title="apple", dueDate="2022-01-01")
        val command = SortCommand(listOf("sort", "byDueDateAsc"))
        command.execute(list)
        assert(list.tasks[0].title == "apple")
    }

    @Test
    fun sortByDueDateDescCommand() {
        val list = TaskList(0, "Test List")
        list.addItem(title="apple", dueDate="2022-01-01")
        list.addItem(title="banana", dueDate="2022-01-02")
        val command = SortCommand(listOf("sort", "byDueDateDesc"))
        command.execute(list)
        assert(list.tasks[0].title == "banana")
    }
}