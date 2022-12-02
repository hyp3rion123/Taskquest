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
    @GetMapping("/{id}")
    fun index(@PathVariable id: String): User = service.get(id)

    @PostMapping()
    fun post(@RequestParam username: String, @RequestParam password: String): User? = service.post(username, password)

    @PutMapping()
    fun put(@RequestBody user: User) {
        service.put(user)
    }

    @DeleteMapping()
    fun delete(@RequestParam username: String, @RequestParam password: String): String? = service.delete(username, password)
}

@Service
object UserService {
    final var currentUser = User(0)

    private final const val connectStr = "DefaultEndpointsProtocol=https;AccountName=taskqueststorage;AccountKey=LIJ4c9UzdCuk/RP6rXPEQFbuYMTuvrWbes/rlvuFJTLz0oQ0KRt3F1MrLToKlUCZmHQQLJ97cDck+AStv0SoRQ==;EndpointSuffix=core.windows.net"

    private final val client: BlobServiceClient = BlobServiceClientBuilder()
        .connectionString(connectStr)
        .buildClient()

    private final val blobContainerClient: BlobContainerClient = client.getBlobContainerClient("user-info")

    var userData = UserData()
    private const val userDataFileName = "users.json"
    private val userDataBlobContainerClient: BlobContainerClient = client.getBlobContainerClient("server-data")
    private val userDataBlobClient: BlobClient = userDataBlobContainerClient.getBlobClient(userDataFileName)

    fun get(id: String): User {
        val filename = "$id.json"
        val blobClient: BlobClient = blobContainerClient.getBlobClient(filename)

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

    fun post(username: String, password: String): User? {
        // first we download our information about all of our users and store it in our data structure
        if (userDataBlobClient.exists()) {
            userDataBlobClient.downloadToFile(userDataFileName)
        } else {
            File(userDataFileName).createNewFile()
            val json = SaveUtils.mapper.writeValueAsString(userData)
            File(userDataFileName).writeText(json)
            userDataBlobClient.uploadFromFile(userDataFileName)
        }
        var userDataJson = File(userDataFileName).readText()
        userData = SaveUtils.mapper.readValue<UserData>(userDataJson)
        // we delete the file after we get our data structure to avoid dealing with overwriting later
        File(userDataFileName).delete()

        // second, we check if the desired username is in use
        for (userInfo in userData.users) {
            if (userInfo.username == username) {
                return null
            }
        }

        // third, if we reach this point then our username is valid and we create the new user
        currentUser = User(userData.curId)
        val newUserInfo = UserInfo(userData.curId, username, password)

        // fourth, we update our collection of user information
        userData.curId += 1
        userData.users.add(newUserInfo)

        // fifth, we upload our new user data to azure cloud storage
        val json = SaveUtils.mapper.writeValueAsString(currentUser)
        val filename = "${currentUser.id}.json"
        val blobClient: BlobClient = blobContainerClient.getBlobClient(filename)

        File(filename).createNewFile()
        File(filename).writeText(json)

        blobClient.deleteIfExists()
        blobClient.uploadFromFile(filename)
        File(filename).delete()

        // sixth, we upload our updated user info to azure cloud storage
        userDataJson = SaveUtils.mapper.writeValueAsString(userData)
        File(userDataFileName).createNewFile()
        File(userDataFileName).writeText(userDataJson)

        userDataBlobClient.deleteIfExists()
        userDataBlobClient.uploadFromFile(userDataFileName)
        File(userDataFileName).delete()

        // finally, we return the new user
        return currentUser
    }

    fun put(user: User) {
        currentUser = user
        val json = SaveUtils.mapper.writeValueAsString(currentUser)
        val filename = "${user.id}.json"
        val blobClient: BlobClient = blobContainerClient.getBlobClient(filename)

        File(filename).createNewFile()
        File(filename).writeText(json)

        blobClient.deleteIfExists()
        blobClient.uploadFromFile(filename)
        File(filename).delete()
    }

    fun delete(username: String, password: String): String? {
        // first we download our information about all of our users and store it in our data structure
        if (userDataBlobClient.exists()) {
            userDataBlobClient.downloadToFile(userDataFileName)
        } else {
            File(userDataFileName).createNewFile()
            val json = SaveUtils.mapper.writeValueAsString(userData)
            File(userDataFileName).writeText(json)
            userDataBlobClient.uploadFromFile(userDataFileName)
        }
        var userDataJson = File(userDataFileName).readText()
        userData = SaveUtils.mapper.readValue<UserData>(userDataJson)
        // we delete the file after we get our data structure to avoid dealing with overwriting later
        File(userDataFileName).delete()

        // second, we check if the credentials match
        for (userInfo in userData.users) {
            if (userInfo.username == username) {
                if (userInfo.password == password) {
                    userData.users.remove(userInfo)

                    // we upload our updated user info to azure cloud storage
                    userDataJson = SaveUtils.mapper.writeValueAsString(userData)
                    File(userDataFileName).createNewFile()
                    File(userDataFileName).writeText(userDataJson)

                    userDataBlobClient.deleteIfExists()
                    userDataBlobClient.uploadFromFile(userDataFileName)
                    File(userDataFileName).delete()

                    return "deleted"
                }
            }
        }

        return null
    }
}

@RestController
@RequestMapping("/login")
class LoginResource(val service: LoginService) {
    @GetMapping
    fun index(@RequestParam username : String, @RequestParam password : String): User? = service.get(username, password)
}

@Service
class LoginService {
    final var userData = UserData()
    private final val userDataFileName = "users.json"

    private final val connectStr = "DefaultEndpointsProtocol=https;AccountName=taskqueststorage;AccountKey=LIJ4c9UzdCuk/RP6rXPEQFbuYMTuvrWbes/rlvuFJTLz0oQ0KRt3F1MrLToKlUCZmHQQLJ97cDck+AStv0SoRQ==;EndpointSuffix=core.windows.net"

    private final val client: BlobServiceClient = BlobServiceClientBuilder()
        .connectionString(connectStr)
        .buildClient()

    private final val blobContainerClient: BlobContainerClient = client.getBlobContainerClient("server-data")

    private final val blobClient: BlobClient = blobContainerClient.getBlobClient(userDataFileName)

    fun get(username: String, password: String): User? {
        if (blobClient.exists()) {
            blobClient.downloadToFile(userDataFileName)
        } else {
            File(userDataFileName).createNewFile()
            val json = SaveUtils.mapper.writeValueAsString(userData)
            File(userDataFileName).writeText(json)
            blobClient.uploadFromFile(userDataFileName)
        }
        val json = File(userDataFileName).readText()
        userData = SaveUtils.mapper.readValue<UserData>(json)
        File(userDataFileName).delete()

        var userId = -1
        for (userInfo in userData.users) {
            if (userInfo.username == username) {
                if (userInfo.password == password) {
                    userId = userInfo.id
                }
            }
        }

        if (userId == -1) {
            return null
        }

        return UserService.get(userId.toString())
    }
}
