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

        fun getUsers(): String {
            val client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(20))
                .build()

            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/users"))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun postUsers(user: User): String {
            val json = SaveUtils.mapper.writeValueAsString(user)

            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }

        fun getStores(): String {
            val client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NEVER)
                .connectTimeout(Duration.ofSeconds(20))
                .build()

            val request = HttpRequest.newBuilder()
                .uri(URI.create("$SERVER_ADDRESS/stores"))
                .GET()
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return response.body()
        }
    }
}
