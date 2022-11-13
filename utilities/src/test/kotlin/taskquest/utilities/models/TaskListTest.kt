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

    @Test
    fun moveItems() {
        val list = TaskList(0, "Test List")
        list.addItem("item 1")
        list.addItem("item 2")
        list.addItem("item 3")
        list.addItem("item 4")
        list.addItem("item 5")
        val ids: MutableList<Int> = mutableListOf<Int>()
        ids.add(list.tasks[0].id)
        ids.add(list.tasks[1].id)
        ids.add(list.tasks[2].id)
        ids.add(list.tasks[3].id)
        ids.add(list.tasks[4].id)
        //Move first item to middle
        list.moveItem(ids[0], ids[2])
        assert(list.tasks[0].id == ids[1] && list.tasks[2].id == ids[0])
        //Move back
        list.moveItem(ids[0], ids[1])
        assert(list.tasks[0].id == ids[0] && list.tasks[2].id == ids[2])
        //Move second item to end
        list.moveItem(ids[0], ids[4])
        assert(list.tasks[4].id == ids[0] && list.tasks[3].id == ids[4])
        //Move last item to middle
        list.moveItem(ids[0], ids[3])
        assert(list.tasks[2].id == ids[0] && list.tasks[3].id == ids[3] && list.tasks[4].id == 4)
    }

    @Test
    fun moveItems2() {
        val list = TaskList(0, "Test List")
        list.addItem("item 1")
        list.addItem("item 2")
        list.addItem("item 3")
        val ids: MutableList<Int> = mutableListOf<Int>()
        ids.add(list.tasks[0].id)
        ids.add(list.tasks[1].id)
        ids.add(list.tasks[2].id)
        println(list.tasks[0].id.toString() + ", " + list.tasks[1].id + ", " + list.tasks[2].id + ", ")

        //Move first to second position
        list.moveItem(ids[0], ids[1])
        println(list.tasks[0].id.toString() + ", " + list.tasks[1].id + ", " + list.tasks[2].id + ", ")
        //Move last to second position
        list.moveItem(ids[2], ids[0])
        println(list.tasks[0].id.toString() + ", " + list.tasks[1].id + ", " + list.tasks[2].id + ", ")
        assert(list.tasks[0].id == ids[1] && list.tasks[1].id == ids[2] && list.tasks[2].id == ids[0])
    }
}