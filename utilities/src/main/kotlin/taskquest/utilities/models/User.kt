package taskquest.utilities.models

import kotlinx.serialization.Serializable

@Serializable
class User() {
    var lastUsedList: Int = - 1
    var wallet: Int = 0
    val lists = mutableListOf<TaskList>()
    var store: Store = Store()
    val tags = mutableSetOf<String>()
    var width = 900.0;
    var height = 500.0;
    var x = 0.0;
    var y = 0.0;

    fun to_string() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}