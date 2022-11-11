package taskquest.utilities.models

data class User(var lastUsedList: Int = - 1, var wallet: Int = 0) {
    val lists = mutableListOf<TaskList>()
    val purchasedItems = mutableListOf<Item>()
    val tags = mutableSetOf<String>()
    var profileImageName: String = "Default.png"
    var longestStreak: Int = 0
    var level: Int = 0

    fun convertToString() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}