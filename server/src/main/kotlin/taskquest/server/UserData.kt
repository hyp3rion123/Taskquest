package taskquest.server

data class UserData(var curId: Int = 1) {
    val users = mutableListOf<UserInfo>()
}