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

    @Test
    fun selectListCommandOne() {
        val user = User(0)
        user.lastUsedList = 1
        val list = TaskList(0, "Test List")
        list.addItem("item 1")
        user.lists.add(list)
        val list2 = TaskList(1, "Test List Two")
        list2.addItem("item 1")
        user.lists.add(list2)
        val currentList = 1
        val command = SelectListCommand(listOf("listselect", "2"))
        command.execute(user.lists)
        assert(user.lastUsedList == currentList)
    }

    @Test
    fun delCommand() {
        val list = TaskList(0, "Test List")
        list.addItem(title="apple")
        list.addItem(title="banana")
        assert(list.tasks.size == 2)

        val command = DelCommand(listOf("del", "2"))
        command.execute(list)
        assert(list.tasks.size == 1)

        val command2 = DelCommand(listOf("del", "1"))
        command2.execute(list)
        assert(list.tasks.size == 0)
    }

    @Test
    fun listDelCommand() {
        val user = User(0)
        val list = TaskList(0, "Test List")
        list.addItem("apple")
        user.lists.add(list)
        val list2 = TaskList(1, "Test List 2")
        list.addItem("banana")
        user.lists.add(list2)
        assert(user.lists.size == 2)

        val command = DeleteListCommand(listOf("listdel", "2"))
        command.execute(user.lists)
        assert(user.lists.size == 1)

        val command2 = DeleteListCommand(listOf("listdel", "1"))
        command2.execute(user.lists)
        assert(user.lists.size == 0)
    }

    @Test
    fun addTagsCommandNorm() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "2", "tag1", "tag2"))
        command.execute()
        assert(user.tags.size == 0)
    }

    @Test
    fun addTagsCommandLessArgs() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "55", "tag1", "tag2", "tag3"))
        command.execute()
        assert(user.tags.size == 0)
    }

    @Test
    fun addTagsCommandMoreArgs() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "1", "tag1", "tag2", "tag3"))
        command.execute()
        assert(user.tags.size == 0)
    }

    @Test
    fun delTagCommand() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "2", "tag1", "tag2"))
        command.execute()
        assert(user.tags.size == 0)

        val command2 = DelTagsCommand(listOf("deltag", "tag1"))
        command2.execute()
        assert(user.tags.size == 0)

    }

    @Test
    fun delTagCommandFail() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "2", "tag1", "tag2"))
        command.execute()
        assert(user.tags.size == 0)

        val command2 = DelTagsCommand(listOf("deltag", "tag"))
        command2.execute()
        assert(user.tags.size == 0)

    }

    @Test
    fun showTagCommandWithTags() {
        val user = User(0)
        val command = AddTagsCommand(listOf("addtags", "2", "tag1", "tag2"))
        command.execute()

        val command2 = ShowTagsCommand()
        command2.execute()
        assert(user.tags.size == 0)

    }

    @Test
    fun showTagCommandWithOutTags() {
        val user = User(0)
        val command = ShowTagsCommand()
        command.execute()
        assert(user.tags.size == 0)

    }

    @Test
    fun coinCommand() {
        val user = User(0)
        val command = ShowCoins()
        command.execute()
        assert(user.wallet == 0)
    }

    @Test
    fun delAll() {
        val user = User(0)
        val command = DelTagsCommand(listOf("deltag, tag1"))
        command.execute()

        val command2 = DelTagsCommand(listOf("deltag, tag2"))
        command2.execute()

        assert(user.tags.size == 0)
    }

}