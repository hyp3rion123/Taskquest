package taskquest.utilities.models
import java.util.*

class UserHistory {
    var historyUndo: Stack<UserMemento> = Stack<UserMemento>()
    var historyRedo: Stack<UserMemento> = Stack<UserMemento>()

    fun save(user: User) {//saves a user object in history
        historyUndo.push(user.save())
    }

    fun previous(user: User) {//go back one point in history
        if(!historyUndo.isEmpty()){
            historyRedo.push(user.save())
            user.previous(historyUndo.pop())
        } else {
            //println("Undo array is empty")
        }
    }

    fun next(user: User){//go forward one point in history
        if(!historyRedo.isEmpty()){
            historyUndo.push(user.save())
            user.previous(historyRedo.pop())
        } else {
            //println("Redo array is empty")
        }
    }
}