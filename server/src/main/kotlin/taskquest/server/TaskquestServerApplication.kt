package taskquest.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.User
import taskquest.utilities.views.MainUser
import java.io.File

@SpringBootApplication
class TaskquestServerApplication

fun main(args: Array<String>) {
    runApplication<TaskquestServerApplication>(*args)
}

@RestController
@RequestMapping("/users")
class MessageResource(val service: MessageService) {
    @GetMapping
    fun index(): User = service.get()

    @PostMapping
    fun post(@RequestBody user: User) {
        service.post(user)
    }
}

@Service
class MessageService {
    final var currentUser = User()
    final val filename = "data.json"
    init {
        if (!File(filename).exists()) {
            File(filename).createNewFile()
            SaveUtils.saveData(currentUser, filename)
        }
        currentUser = SaveUtils.restoreData(filename)
    }

    fun get() = currentUser
    fun post(user: User) {
        currentUser = user
         SaveUtils.saveData(currentUser, filename)
    }
}

