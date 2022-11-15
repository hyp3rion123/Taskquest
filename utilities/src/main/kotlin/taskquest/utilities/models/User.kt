package taskquest.utilities.models

data class User(var lastUsedList: Int = - 1, var wallet: Int = 0) {
    val lists = mutableListOf<TaskList>()
    val purchasedItems = mutableListOf<Item>()
    val tags = mutableSetOf<String>()
    var profileImageName: String = "Default.png"
    var longestStreak: Int = 0
    var level: Int = 0
    var width = 900.0;
    var height = 500.0;
    var x = 0.0;
    var y = 0.0;

    fun addTaskList(name : String) {
        var lastId = lists[-1].id
        lists.add(TaskList(lastId + 1, name))
    }

    fun convertToString() {
        for (list in lists) {
            println(list.title)
            for (task in list.tasks) {
                println(task.title)
            }
        }
    }
}