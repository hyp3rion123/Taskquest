package taskquest.utilities.models

import org.junit.jupiter.api.Test
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority
import java.time.LocalDate

internal class TaskTest {
    @Test
    fun createTask() {
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
    fun changeTask() {
        val task = Task(id = 0, title = "title")
        task.title = "title 2"
        assert(task.title == "title 2")
        task.desc = "test description"
        assert(task.desc == "test description")
        task.dueDate = LocalDate.now().toString()
        assert(LocalDate.parse(task.dateCreated) == LocalDate.now())
        task.priority = Priority.High
        assert(task.priority == Priority.High)
        task.difficulty = Difficulty.Hard
        assert(task.difficulty == Difficulty.Hard)
        task.complete = true
        assert(task.complete)
        task.tags.add("Test Tag")
        assert(task.tags.contains("Test Tag"))
    }
}