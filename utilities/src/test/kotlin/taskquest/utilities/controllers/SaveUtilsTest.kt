package taskquest.utilities.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import taskquest.utilities.models.TaskList
import taskquest.utilities.models.User
import java.io.File

internal class SaveUtilsTest {
//    @Test
//    fun save() {
//        val filename = "testfile.json"
//        File(filename).delete()
//
//        // create and save a list
//        val user = User()
//        val list = TaskList(0, "Test List")
//        list.addItem("item 1")
//        list.addItem("item 2")
//        list.addItem("item 3")
//        list.addItem("item 4")
//        list.addItem("item 5")
//        user.lists.add(list)
//        SaveUtils.saveUserData(user, filename)
//
//        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
//
//        // ensure that the saved file contains the correct data
//        assert(mapper.writeValueAsString(user) == File(filename).readText())
//
//        // cleanup
//        File(filename).delete()
//    }
//
//    @Test
//    fun restore() {
//        val filename = "testfile.json"
//        File(filename).delete()
//
//        // create and save a list
//        val user = User()
//        val list = TaskList(0, "Test List")
//        list.addItem("item 1")
//        list.addItem("item 2")
//        list.addItem("item 3")
//        list.addItem("item 4")
//        list.addItem("item 5")
//        user.lists.add(list)
//        SaveUtils.saveUserData(user, filename)
//
//        // ensure that restore gives us the same list
//        val user2 = SaveUtils.restoreUserData(filename)
//        user.lists[0].tasks.zip(user2.lists[0].tasks).forEach {pair ->
//            assert(pair.component1().title == pair.component2().title)
//        }
//
//        // cleanup
//        File(filename).delete()
//    }
}