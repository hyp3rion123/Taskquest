package taskquest.utilities.models

import org.junit.jupiter.api.Test

internal class TaskListTest {
    @Test
    fun addItems() {
        val list = TaskList(0, "Test List")
        list.addItem("item 1")
        list.addItem("item 2")
        list.addItem("item 3")
        list.addItem("item 4")
        list.addItem("item 5")
        assert(list.tasks.size == 5)
    }

    @Test
    fun delItems() {
        val list = TaskList(0, "Test List")
        list.addItem("item 1")
        list.deleteItem(0)
        assert(list.tasks.size == 0)
    }
}