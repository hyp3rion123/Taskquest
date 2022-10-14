package taskquest.console

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File

internal class ModelTest {
    @Test
    fun createItem() {
        val item = Item(id = 0, text = "text")
        assert(item.id == 0)
        assert(item.text == "text")
    }

    @Test
    fun changeItem() {
        val item = Item(id = 0, text = "text")
        item.text = "text 2"
        assert(item.text == "text 2")
    }

    @Test
    fun addItems() {
        val list = mutableListOf<Item>()
        list.add("item 1")
        list.add("item 2")
        list.add("item 3")
        list.add("item 4")
        list.add("item 5")
        assert(list.size == 5)
    }

    @Test
    fun delItems() {
        val list = mutableListOf<Item>()
        list.add("item 1")
        list.removeAt(0)
        assert(list.size == 0)
    }

    @Test
    fun save() {
        val filename = "testfile.json"
        File(filename).delete()

        // create and save a list
        val list = mutableListOf<Item>()
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
        val list1 = mutableListOf<Item>()
        list1.add("item 1")
        list1.add("item 2")
        list1.add("item 3")
        list1.add("item 4")
        list1.add("item 5")
        list1.save(filename)

        // ensure that restore gives us the same list
        val list2 = mutableListOf<Item>()
        list2.restore(filename)
        assert(list1 == list2)

        // cleanup
        File(filename).delete()
    }
}