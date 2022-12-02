package taskquest.utilities.controllers

import taskquest.utilities.models.User
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class CloudUtils {
    companion object {
        val SERVER_ADDRESS = "https://taskquest-server.greenmoss-6ea3acae.eastus.azurecontainerapps.io"
        val client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(20))
            .build()

        fun login(username: String, password: String): String {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/login?username=$username&password=$password"))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun createUser(username: String, password: String): String {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/users?username=$username&password=$password"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun updateUser(user: User): String {
            val json = SaveUtils.mapper.writeValueAsString(user)

            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun deleteUser(username: String, password: String): String {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/users?username=$username&password=$password"))
                .DELETE()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }
    }
}
