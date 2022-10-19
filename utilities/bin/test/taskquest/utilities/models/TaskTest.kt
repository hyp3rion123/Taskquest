package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.Priority
import java.time.LocalDate

internal class TaskTest {
    @Test
    fun createItem() {
        val task = Task(id = 0, title = "title")
        assert(task.id == 0)
        assert(task.title == "title")
        assert(task.desc == "")
        assert(task.dueDate == "")
        assert(LocalDate.parse(task.dateCreated) == LocalDate.now())
        assert(task.priority == null)
        assert(task.difficulty == null)
        assert(!task.complete)
        assert(task.tags.isEmpty())
    }

    @Test
    fun changeItem() {
        val task = Task(id = 0, title = "title")
        task.title = "title 2"
        assert(task.title == "title 2")
        task.desc = "test description"
        assert(task.desc == "test description")
        task.dueDate = LocalDate.now().toString()
        assert(LocalDate.parse(task.dateCreated) == LocalDate.now())
        task.priority = Priority.High
        assert(task.priority == Priority.High)
        task.complete = true
        assert(task.complete)
        task.tags.add("Test Tag")
        assert(task.tags.contains("Test Tag"))
    }

    // not yet updated with new Task class
    @Test
    fun addItems() {
        val list = mutableListOf<Task>()
        list.add("item 1")
        list.add("item 2")
        list.add("item 3")
        list.add("item 4")
        list.add("item 5")
        assert(list.size == 5)
    }

    @Test
    fun delItems() {
        val list = mutableListOf<Task>()
        list.add("item 1")
        list.removeAt(0)
        assert(list.size == 0)
    }

    @Test
    fun save() {
        val filename = "testfile.json"
        File(filename).delete()

        // create and save a list
        val list = mutableListOf<Task>()
        list.add("item 1")
        list.add("item 2")
        list.add("item 3")
        list.add("item 4")
        list.add("item 5")
        list.save(filename)

        // ensure that the saved file contains the correct data
        assert(Json.encodeToString(list) == File(filename).readText())

        // cleanup
        File(filename).delete()
    }

    @Test
    fun restore() {
        val filename = "testfile.json"
        File(filename).delete()

        // create and save a list
        val list1 = mutableListOf<Task>()
        list1.add("item 1")
        list1.add("item 2")
        list1.add("item 3")
        list1.add("item 4")
        list1.add("item 5")
        list1.save(filename)

        // ensure that restore gives us the same list
        val list2 = mutableListOf<Task>()
        list2.restore(filename)
        assert(list1 == list2)

        // cleanup
        File(filename).delete()
    }
}