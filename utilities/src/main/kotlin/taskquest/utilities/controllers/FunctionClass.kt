package taskquest.utilities.controllers

import taskquest.utilities.models.TaskList

object FunctionClass {

    fun sortTasksBy(sortMethod : String, list : TaskList) =
        when (sortMethod) {
            "byTitleAsc" -> list.tasks.sortBy { it.title }
            "byTitleDesc" -> list.tasks.sortByDescending { it.title }
            "byDueDateAsc" -> list.tasks.sortBy { it.dueDate }
            "byDueDateDesc" -> list.tasks.sortByDescending { it.dueDate }
            "byDateCreatedAsc" -> list.tasks.sortBy { it.dateCreated }
            "byDateCreatedDesc" -> list.tasks.sortByDescending { it.dateCreated }
            "byPriorityAsc" -> list.tasks.sortByDescending { it.priority }
            "byPriorityDesc" -> list.tasks.sortBy { it.priority }
            "byDifficultyAsc" -> list.tasks.sortByDescending { it.difficulty }
            "byDifficultyDesc" -> list.tasks.sortBy { it.difficulty }
            "byCompletion" -> list.tasks.sortBy { it.complete }
            "default" -> list.tasks.sortBy { it.id }
            else -> println("Sorting method not supported.")
        }


}