package taskquest.utilities.models

import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority

// To-do lists that contains to-do items
class TaskList(
    val id: Int, var title: String, var desc: String = ""
) {
    val tasks: MutableList<Task> = mutableListOf<Task>()
    var nextId = 0

    fun addItem(title: String, desc: String = "", dueDate: String = "",
                priority: Priority? = null, difficulty: Difficulty? = null, tags: MutableSet<String>? = null) {
        var newTask = Task(id=this.tasks.size, title=title, desc=desc, dueDate=dueDate, priority=priority,
            difficulty=difficulty)
        if (tags != null) {
            newTask.tags = tags
        }
        this.tasks.add(newTask)
    }
    fun addItem(task: Task) {
        task.id = nextId
        this.tasks.add(task)
        nextId += 1
    }

    fun deleteItemByID(id: Int) {
        this.tasks.removeIf { it.id == id }
    }
    fun deleteItem(idx: Int) {
        val currentId = this.tasks[idx].id
        this.tasks.forEach { if (it.id > currentId) it.id -= 1 }
        this.tasks.removeAt(idx)
    }

    fun moveItem(from: Int, to: Int): Boolean {
        var posFrom = -1
        var posTo = -1
        for ((index, value) in this.tasks.withIndex()) {
            if (value.id == from) {
                posFrom = index
            } else if (value.id == to) {
                posTo = index
            }
        }
        if (posFrom == -1 || posTo == -1) {
            return false // this shouldn't happen unless id assignment logic is unsound or front-end isn't able to get ids
        }
        val taskToMove = this.tasks[posFrom]
        this.tasks.removeAt(posFrom)
        if (posTo > posFrom) posFrom -= 1
        this.tasks.add(posTo,taskToMove)
        return true
    }


}




