package taskquest.utilities.models
import java.util.*

class UserHistory {
    val maxStackSize = 5 //to prevent out of memory
    var historyUndo: Stack<UserMemento> = Stack<UserMemento>()
    var historyRedo: Stack<UserMemento> = Stack<UserMemento>()

    fun save(user: User) {//saves a user object in history
        println("in history save, user has: " + user.lists.size)
        historyUndo.push(user.save())
    }

    fun previous(user: User) {//go back one point in history
        if(!historyUndo.isEmpty()){
            historyRedo.push(user.save())
            println("in history previous BEFORE: " + historyUndo.peek().currentUser.lists.size)
            user.previous(historyUndo.pop())
            println("in history previous AFTER: " + user.lists.size)
        } else {
            println("Undo array is empty")
        }
    }

    fun next(user: User){//go forward one point in history
        if(!historyRedo.isEmpty()){
            historyUndo.push(user.save())
//            println("in history previous BEFORE: " + historyUndo.peek().currentUser.lists.size)
            user.previous(historyRedo.pop())
//            println("in history previous AFTER: " + user.lists.size)
        } else {
            println("Redo array is empty")
        }
    }

//    fun smartPush(stack: Stack<UserMemento>) {
//        var userArray = mutableListOf<User>()
//        for (i in 1..maxStackSize) {
//
//        }
//    }
}