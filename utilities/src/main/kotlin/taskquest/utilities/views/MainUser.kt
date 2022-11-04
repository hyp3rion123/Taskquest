package taskquest.utilities.views

import kotlinx.serialization.Serializable
import taskquest.utilities.models.User

@Serializable
object MainUser {
    var userInfo: User = User()
}