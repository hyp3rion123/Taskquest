package taskquest.utilities.controllers

import com.azure.core.credential.AccessToken
import com.azure.core.credential.TokenRequestContext
import com.azure.identity.DeviceCodeCredential
import com.azure.identity.DeviceCodeCredentialBuilder
import com.azure.identity.DeviceCodeInfo
import com.microsoft.graph.authentication.TokenCredentialAuthProvider
import com.microsoft.graph.models.BodyType
import com.microsoft.graph.models.DateTimeTimeZone
import com.microsoft.graph.models.Event
import com.microsoft.graph.models.ItemBody
import com.microsoft.graph.requests.GraphServiceClient
import okhttp3.Request
import java.util.*
import java.util.function.Consumer
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.EventCollectionPage
import okhttp3.Challenge
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import taskquest.utilities.controllers.SaveUtils
import taskquest.utilities.models.TaskList
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter


private var _properties: Properties? = null
private lateinit var _deviceCodeCredential: DeviceCodeCredential
private var _userClient: GraphServiceClient<Request>? = null
var authToken: AccessToken? = null

class Graph() {
    var thisChallenge: DeviceCodeInfo? = null
    init{
//        val expiryFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", )
//        val tokenExpiryTime = LocalDateTime.parse(SaveUtils.restoreToken(), expiryFormatter).toInstant(ZoneOffset.UTC)
//        if(Instant.now().isAfter(tokenExpiryTime)) {
            val oAuthProperties = Properties()
            try {
                val file = File("C:\\Users\\Andrei\\Desktop\\Schoolwork\\CS346\\cs-346-project\\app\\src\\main\\resources\\oAuth.properties")
                FileInputStream(file).use{oAuthProperties.load(it)}

                oAuthProperties.stringPropertyNames()
                    .associateWith { oAuthProperties.getProperty(it) }
                    .forEach {println(it)}
                initializeGraphForUserAuth(
                    oAuthProperties
                ) { challenge ->
                    if (challenge != null) {
                        println("user code :" + challenge.userCode + " url: " + challenge.verificationUrl)
                        thisChallenge = challenge
                    }
                }
            } catch (e: IOException) {
                println("Unable to read OAuth configuration: "+ e)
            }
//        }
    }

    fun updateTasks(lists: List<TaskList>) {
        //Remove all tasks that match the TaskQuest formatting
        val events = getCalendarEvents()
        events.forEach{
//            printEvent(it)
            if(it.subject?.substring(0,3) == "TQ-") {
//                println("Removing above event due to TQ with id " + it.id)
                it.id?.let { it1 ->
                    _userClient!!.me().events(it1)
                        .buildRequest()
                        .delete()
                }
            }
        }

        //Add all tasks from the task lists into Outlook
        lists.forEach{
            it.tasks.forEach{
                val props = it.toOutlookItem()
                if(props[2] != "") {
                    var event = Event()
                    var body = ItemBody()
                    var start = DateTimeTimeZone()
                    var end = DateTimeTimeZone()


                    body.contentType = BodyType.HTML
                    body.content = props[1]
                    start.dateTime = props[2]+"T09:00:00"
                    start.timeZone = "Pacific Standard Time"
                    end.dateTime = props[2]+"T10:00:00"
                    end.timeZone = "Pacific Standard Time"

                    event.subject = "TQ-"+props[0]
                    event.body = body
                    event.start = start
                    event.end = end
                    _userClient!!.me().events()
                        .buildRequest()
                        .post(event)
                }
            }
        }
    }


        @Throws(Exception::class)
        fun initializeGraphForUserAuth(properties: Properties?, challenge: Consumer<DeviceCodeInfo?>?) {
            // Ensure properties isn't null
            if (properties == null) {
                throw Exception("Properties cannot be null")
            }
            _properties = properties
            val clientId = properties.getProperty("app.clientId")
            val authTenantId = properties.getProperty("app.authTenant")
            val graphUserScopes = Arrays
                .asList(
                    *properties.getProperty("app.graphUserScopes").split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())
            _deviceCodeCredential = DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(authTenantId)
                .challengeConsumer(challenge)
                .build()
            val authProvider = TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential)
            _userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient()
            // Ensure credential isn't null
            if (_deviceCodeCredential == null) {
                throw java.lang.Exception("Graph has not been initialized for user auth")
            }
            val graphUserScopes2: List<String>? = _properties?.getProperty("app.graphUserScopes")?.split(",")
            val context = TokenRequestContext()
            graphUserScopes2?.forEach { context.addScopes(it) }
            authToken = _deviceCodeCredential!!.getToken(context).block()
//            authToken?.let { saveToken(authToken!!) }
        }

        fun displayAccessToken() {
            try {
                println("Access token: ${authToken?.token}")
                println("Expires: " + authToken?.expiresAt)
                println("is expired? " + authToken?.isExpired)
            } catch (e: java.lang.Exception) {
                println("Error getting access token")
                println(e.message)
            }
        }

        @Throws(java.lang.Exception::class)
        fun getUser(): User? {
            // Ensure client isn't null
            if (_userClient == null) {
                throw java.lang.Exception("Graph has not been initialized for user auth")
            }
            return _userClient!!.me()
                .buildRequest()
                .select("displayName,mail,userPrincipalName")
                .get()
        }
        fun printEvent(e: Event) {
            val doc : Document = Jsoup.parse(e.body?.content)
            val contentBody = doc.body().text()
            println("Subject: " + e.subject)
            println("Body: " + contentBody)
            println("Start: " + e.start?.dateTime)
            println("End: " + e.end?.dateTime)
        }

        @Throws(java.lang.Exception::class)
        fun getCalendarEvents(): List<Event> {
            // Ensure client isn't null
            if (_userClient == null) {
                throw java.lang.Exception("Graph has not been initialized for user auth")
            }
            var eventList = mutableListOf<Event>()
            try {
                var events: EventCollectionPage? = _userClient!!.me().calendar().events()
                    .buildRequest()
                    .get()
                while (events != null) {
                    events?.currentPage?.forEach{
//                        val doc : Document = Jsoup.parse(it.body?.content)
//                        val contentBody = doc.body().text()
//                        println("Subject: " + it.subject)
//                        println("Body: " + contentBody)
//                        println("Start: " + it.start?.dateTime)
//                        println("End: " + it.end?.dateTime)
                        eventList.add(it)
                    }
                    if(events?.nextPage == null) {
                        break
                    } else {
                        events = events.nextPage!!.buildRequest().get()
                    }
                }
            } catch (e: java.lang.Exception) {
                println("Error getting groups")
                println(e.message)
            }
            return eventList
        }
}

