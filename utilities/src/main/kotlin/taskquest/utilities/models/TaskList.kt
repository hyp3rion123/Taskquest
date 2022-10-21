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

    fun addItem(id: Int, title: String, desc: String, dueDate: String,
                priority: Priority?, difficulty: Difficulty?) {
        this.tasks.add(Task(id=id, title=title, desc=desc, dueDate=dueDate, priority=priority,
                            difficulty=difficulty))
    }
    fun addItem(task: Task) {
        this.tasks.add(task)
    }

    fun deleteItemByID(id: Int) {
        this.tasks.removeIf { it.id == id }
    }

    fun getLength(): Int {
        return this.tasks.size
    }
}




