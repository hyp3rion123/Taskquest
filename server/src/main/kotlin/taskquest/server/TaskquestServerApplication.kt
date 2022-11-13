package taskquest.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.Item
import taskquest.utilities.models.Store
import taskquest.utilities.models.User
import java.io.File

@SpringBootApplication
class TaskquestServerApplication

fun main(args: Array<String>) {
    runApplication<TaskquestServerApplication>(*args)
}

@RestController
@RequestMapping("/users")
class UserResource(val service: UserService) {
    @GetMapping
    fun index(): User = service.get()

    @PostMapping
    fun post(@RequestBody user: User) {
        service.post(user)
    }
}

@Service
class UserService {
    final var currentUser = User()
    final val filename = "userdata.json"
    init {
        if (!File(filename).exists()) {
            File(filename).createNewFile()
            SaveUtils.saveUserData(currentUser, filename)
        }
        currentUser = SaveUtils.restoreUserData(filename)
    }

    fun get() = currentUser
    fun post(user: User) {
        currentUser = user
        SaveUtils.saveUserData(currentUser, filename)
    }
}

@RestController
@RequestMapping("/stores")
class StoreResource(val service: StoreService) {
    @GetMapping
    fun index(): Store = service.get()

    @PostMapping
    fun post(@RequestBody item: Item) {
        service.post(item)
    }
}

@Service
class StoreService {
    final var store = Store()
    final val filename = "storedata.json"
    init {
        if (!File(filename).exists()) {
            File(filename).createNewFile()
            SaveUtils.saveStoreData(store, filename)
        }
        store = SaveUtils.restoreStoreData(filename)
    }

    fun get() = store
    fun post(item: Item) {
        store.addItem(item.name, item.price, item.type)
        SaveUtils.saveStoreData(store, filename)
    }
}
