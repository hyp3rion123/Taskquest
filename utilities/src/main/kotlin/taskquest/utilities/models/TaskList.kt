package taskquest.utilities.models

import kotlinx.serialization.Serializable
import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority

// To-do lists that contains to-do items
@Serializable
class TaskList(
    val id: Int, var title: String, var desc: String = ""
    ) {

    val tasks: MutableList<Task> = mutableListOf<Task>()

    // test
    fun addItem(id: Int, title: String, priority: Priority, difficulty: Difficulty) {
        this.tasks.add(Task(id=id, title=title, priority=priority, difficulty=difficulty))
    }

    fun addItem(id: Int, title: String, desc: String, dueDate: String, dateCreated: String,
                priority: Priority, difficulty: Difficulty, complete: Boolean) {
        this.tasks.add(Task(id, title, desc, dueDate, dateCreated, priority, difficulty, complete))
    }

    fun deleteItem(id: Int) {
        this.tasks.removeIf { it.id == id }
    }
}




