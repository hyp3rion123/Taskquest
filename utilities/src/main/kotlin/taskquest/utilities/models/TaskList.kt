package taskquest.utilities.models

import taskquest.utilities.models.enums.Difficulty
import taskquest.utilities.models.enums.Priority

// To-do lists that contains to-do items
class TaskList(
    val id: Int, var title: String, var desc: String = ""
) {
    val tasks: MutableList<Task> = mutableListOf<Task>()
    var nextId = 0
    var curTask = -1

    fun addItem(title: String, desc: String = "", dueDate: String = "",
                priority: Priority? = null, difficulty: Difficulty? = null, tags: MutableSet<String>? = null) {
        var newTask = Task(id=nextId, title=title, desc=desc, dueDate=dueDate, priority=priority,
            difficulty=difficulty)
        if (tags != null) {
            newTask.tags = tags
        }
        this.tasks.add(newTask)
        this.nextId += 1
    }

    fun deleteItemByID(id: Int) {

        for (idx in this.tasks.indices)
        {
            if (this.tasks[idx].id == id) {

                this.tasks.removeAt(idx)

                if (this.curTask == idx) {
                    this.curTask = -1
                } else if (this.curTask > idx) {
                    this.curTask--
                }

                break

            }
        }

        if (this.tasks.size == 0) {
            this.nextId = 0
        }
    }
    fun deleteItem(idx: Int) {
        this.tasks.removeAt(idx)

        if (this.curTask == idx) {
            this.curTask = -1
        } else if (this.curTask > idx) {
            this.curTask--
        }

        if (this.tasks.size == 0) {
            this.nextId = 0
        }
    }

    fun updateCurTask(id: Int) {
        for (idx in this.tasks.indices) {
            if (this.tasks[idx].id == id) {
                this.curTask = idx
                break
            }
        }
    }

    fun findIdx(id : Int): Int {
        for (idx in this.tasks.indices) {
            if (this.tasks[idx].id == id) {
                return idx
            }
        }
        return 0
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




