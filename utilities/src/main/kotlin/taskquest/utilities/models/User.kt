package taskquest.utilities.models

import kotlinx.serialization.Serializable

@Serializable
class User() {
    var lastUsedList: Int = - 1
    var wallet: Int = 0
    val lists = mutableListOf<TaskList>()
    val store: Store = Store()

    fun to_string() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}