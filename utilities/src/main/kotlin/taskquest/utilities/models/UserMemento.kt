package taskquest.utilities.models

class UserMemento(user: User) {
    val currentUser = user
    fun getUser(): User {
        return currentUser
    }
}