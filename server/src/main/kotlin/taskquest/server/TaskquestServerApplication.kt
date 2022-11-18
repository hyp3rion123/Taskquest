package taskquest.server

import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.fasterxml.jackson.module.kotlin.readValue
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
    private final val filename = "userdata.json"

    private final val connectStr = "DefaultEndpointsProtocol=https;AccountName=taskqueststorage;AccountKey=LIJ4c9UzdCuk/RP6rXPEQFbuYMTuvrWbes/rlvuFJTLz0oQ0KRt3F1MrLToKlUCZmHQQLJ97cDck+AStv0SoRQ==;EndpointSuffix=core.windows.net"

    private final val client: BlobServiceClient = BlobServiceClientBuilder()
        .connectionString(connectStr)
        .buildClient()

    private final val blobContainerClient: BlobContainerClient = client.getBlobContainerClient("server-data")

    private final val blobClient: BlobClient = blobContainerClient.getBlobClient(filename)

    fun get(): User {
        if (blobClient.exists()) {
            blobClient.downloadToFile(filename)
        } else {
            File(filename).createNewFile()
            val json = SaveUtils.mapper.writeValueAsString(currentUser)
            File(filename).writeText(json)
        }
        val json = File(filename).readText()
        currentUser = SaveUtils.mapper.readValue<User>(json)
        File(filename).delete()
        return currentUser
    }
    fun post(user: User) {
        currentUser = user
        val json = SaveUtils.mapper.writeValueAsString(currentUser)
        File(filename).createNewFile()
        File(filename).writeText(json)

        blobClient.deleteIfExists()
        blobClient.uploadFromFile(filename)
        File(filename).delete()
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
