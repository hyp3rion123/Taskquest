package taskquest.utilities.models

import kotlinx.serialization.Serializable

@Serializable
class User() {
    var lastUsedList: Int = - 1
    var wallet: Int = 0
    val lists = mutableListOf<TaskList>()
    var store: Store = Store()
    val tags = mutableSetOf<String>()
    var profileImageName: String = "Default.png"
    var longestStreak: Int = 0
    var level: Int = 0

    fun to_string() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}