package taskquest.utilities.models

import com.google.gson.Gson
import java.time.LocalDate

data class User(val id: Int, var lastUsedList: Int = - 1, var wallet: Int = 0) {
    // IMPORTANT NOTE: any variable you add and want to be able to undo must be added to the user memento function below
    // to properly restore everything
    var name = ""
    var lists = mutableListOf<TaskList>()
    var purchasedItems = mutableListOf<Item>()
    var tags = mutableSetOf<String>()
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
    val bannerMax = 4
    val bannerMin = 0
    var multiplier = 1.0
    var bannerRequirements = listOf(1,3,6,10,15);

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
        updateBannerRank() // update banner rank
    }
    fun updateBannerRank(){
        bannerRank = 0
        // gets maximum banner based on bannerRequirements
        for (threshold in bannerRequirements) {
            if (tasksDoneToday >= threshold) {
                bannerRank += 1
            }
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

    fun save(): UserMemento {
        //Convert to gson so we have a deep copy
        var gson = Gson()
        var jsonString = gson.toJson(this)
        var testModel = gson.fromJson(jsonString, User::class.java)
        return UserMemento(testModel)
    }

    fun previous(prevUser: UserMemento) {
        val prevUserCopy = prevUser.getUser()
        lists = prevUserCopy.lists
        purchasedItems = prevUserCopy.purchasedItems
        tags = prevUserCopy.tags
        nextId = prevUserCopy.nextId
        bannerRank = prevUserCopy.bannerRank
        longestStreak = prevUserCopy.longestStreak
        tasksDoneToday = prevUserCopy.tasksDoneToday
        dateLastCompleted = prevUserCopy.dateLastCompleted
        multiplier = prevUserCopy.multiplier
        lastUsedList = prevUserCopy.lastUsedList
        wallet = prevUserCopy.wallet
    }
}