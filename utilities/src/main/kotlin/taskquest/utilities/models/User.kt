package taskquest.utilities.models

data class User(var lastUsedList: Int = - 1, var wallet: Int = 0) {
    val bannerMax = 9
    val bannerMin = 0
    val lists = mutableListOf<TaskList>()
    val purchasedItems = mutableListOf<Item>()
    val tags = mutableSetOf<String>()
    var profileImageName: String = "Default"
    var bannerRank: Int = 0
    var longestStreak: Int = 0
    var tasksDoneToday: Int = 0
    var level: Int = 0
    var width = 900.0;
    var height = 500.0;
    var x = 0.0;
    var y = 0.0;

    fun taskCompleteCounter() {
        tasksDoneToday += 1 // increment tasks done today
        if (tasksDoneToday % 3 == 0) { // for every 3 tasks done
            bannerRank += 1 // increment counter
        }
        if (bannerRank > bannerMax) {
            bannerRank = bannerMax
        }
        if (bannerRank < bannerMin) {
            bannerRank = bannerMin
        }
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