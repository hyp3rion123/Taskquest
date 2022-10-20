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

    fun addItem(title: String, desc: String = "", dueDate: String = "",
                priority: Priority? = null, difficulty: Difficulty? = null) {
        this.tasks.add(Task(id=this.tasks.size, title=title, desc=desc, dueDate=dueDate, priority=priority,
                            difficulty=difficulty))
    }


    fun deleteItem(idx: Int) {
        this.tasks.removeAt(idx)

    }

}




