package taskquest.console.controllers

    // tests that rely on commands.kt are commented out since these will not work with new Task class
//import org.junit.jupiter.api.Test
//import taskquest.utilities.models.*
//
//internal class CommandsTest {
//
//    @Test
//    fun commandFactory() {
//        // basic commands
//        val addString = arrayOf("add", "title")
//        val addCommand = CommandFactory.createFromArgs(addString)
//        assert(addCommand is AddCommand)
//
//        val delString = arrayOf("del", "0")
//        val delCommand = CommandFactory.createFromArgs(delString)
//        assert(delCommand is DelCommand)
//
//        val showString = arrayOf("show")
//        val showCommand = CommandFactory.createFromArgs(showString)
//        assert(showCommand is ShowCommand)
//
//        // multiple ways to invoke help
//        val helpString1 = arrayOf("")
//        val helpCommand1 = CommandFactory.createFromArgs(helpString1)
//        assert(helpCommand1 is HelpCommand)
//
//        val helpString2 = arrayOf("help")
//        val helpCommand2 = CommandFactory.createFromArgs(helpString2)
//        assert(helpCommand2 is HelpCommand)
//
//        // unknown commands/arguments also invoke help
//        val unknownString1 = arrayOf("unknown")
//        val unknownCommand1 = CommandFactory.createFromArgs(unknownString1)
//        assert(unknownCommand1 is HelpCommand)
//
//        val unknownString2 = arrayOf("unknown", "unknown", "unknown")
//        val unknownCommand2 = CommandFactory.createFromArgs(unknownString2)
//        assert(unknownCommand2 is HelpCommand)
//    }
//
//    @Test
//    fun addCommand() {
//        val list = mutableListOf<Task>()
//        val command = AddCommand(arrayOf("add", "test"))
//        command.execute(list)
//        assert(list.size == 1)
//    }
//
//    @Test
//    fun delCommand() {
//        val list = mutableListOf<Task>()
//        list.add(0, "test0")
//        list.add(1, "test1")
//        list.add(2, "test2")
//
//        val command1 = DelCommand(arrayOf("del", "0"))
//        command1.execute(list)
//        assert(list.size == 2)
//        assert(list.first().id == 1)
//        assert(list.last().id == 2)
//
//        val command2 = DelCommand(arrayOf("del", "1"))
//        command2.execute(list)
//        assert(list.size == 1)
//        assert(list.first().id == 2)
//
//        val command3 = DelCommand(arrayOf("del", "2"))
//        command3.execute(list)
//        assert(list.size == 0)
//    }
//
//    @Test
//    fun showCommand() {
//        val list = mutableListOf<Task>()
//        val command = ShowCommand(arrayOf("show"))
//        command.execute(list)
//        assert(list.size == 0)
//
//        list.add(0, "test")
//        command.execute(list)
//        assert(list.size == 1)
//    }
//}