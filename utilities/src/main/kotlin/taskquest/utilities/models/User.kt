package taskquest.utilities.models

data class User(var lastUsedList: Int = - 1, var wallet: Int = 0) {
    val lists = mutableListOf<TaskList>()
    val store: Store = Store()

    fun convertToString() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}