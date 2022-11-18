package taskquest.utilities.models

import java.time.LocalDate

data class User(var lastUsedList: Int = - 1, var wallet: Int = 0) {
    val lists = mutableListOf<TaskList>()
    val purchasedItems = mutableListOf<Item>()
    val tags = mutableSetOf<String>()
    var nextId = 0
    var profileImageName = "Default.png"
    var bannerRank = 0
    var longestStreak = 0
    var tasksDoneToday = 0
    var dateLastCompleted = ""
    var level = 0 // unused - rank used instead
    var width = 900.0
    var height = 600.0
    var x = 0.0
    var y = 0.0
    val bannerMax = 9
    val bannerMin = 0
    var multiplier = 1.0

    fun convertToString() {
        for (list in lists) {
            //println(list.title)
            for (task in list.tasks) {
                //println(task.title)
            }
        }
    }

    fun deleteList(id: Int) {

        for (idx in this.lists.indices) {
            if (this.lists[idx].id == id) {

                this.lists.removeAt(idx)

                if (this.lastUsedList == idx) {
                    this.lastUsedList = -1
                } else if (this.lastUsedList > idx) {
                    this.lastUsedList--
                }

                break

            }
        }

    }

    fun updateActiveList(id: Int) {

        for (idx in this.lists.indices) {
            if (this.lists[idx].id == id) {
                this.lastUsedList = idx
                break
            }
        }
    }

    fun addList(title : String, desc : String = "") {
        this.lists.add(TaskList(this.nextId, title, desc))
        this.nextId += 1
    }

    fun completeTask(task: Task) {
        task.completeOnce = true

        if (this.dateLastCompleted != LocalDate.now().toString()) {
            this.tasksDoneToday = 1
            this.multiplier = 1.0
            this.dateLastCompleted = LocalDate.now().toString()
            this.wallet += (task.rewardCoins * this.multiplier).toInt()
        } else {
            this.tasksDoneToday += 1 // increment tasks done today
            if (this.tasksDoneToday == 6) {
                this.multiplier = 0.75
            } else if (this.tasksDoneToday == 11) {
                this.multiplier = 0.5
            } else if (this.tasksDoneToday == 16) {
                this.multiplier = 0.25
            } else if (this.tasksDoneToday > 21) {
                this.multiplier = 0.0
            }

            this.wallet += (task.rewardCoins * this.multiplier).toInt()
        }

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

    fun findIdx(id: Int) : Int {
        for (idx in this.lists.indices) {
            if (this.lists[idx].id == id) {
                return idx
            }
        }

        return 0
    }

}