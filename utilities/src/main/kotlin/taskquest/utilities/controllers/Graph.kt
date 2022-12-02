package taskquest.utilities.controllers

import com.azure.core.credential.AccessToken
import com.azure.identity.InteractiveBrowserCredential
import com.azure.identity.InteractiveBrowserCredentialBuilder
import com.microsoft.graph.authentication.TokenCredentialAuthProvider
import com.microsoft.graph.models.*
import com.microsoft.graph.requests.EventCollectionPage
import com.microsoft.graph.requests.GraphServiceClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import taskquest.utilities.models.TaskList
import java.io.IOException
import java.util.*

private var _properties: Properties? = null
private lateinit var _interactiveCredential: InteractiveBrowserCredential
private var _userClient: GraphServiceClient<Request>? = null
var authToken: AccessToken? = null

class Graph() {
    var synced = false;
    fun init(){
            val oAuthProperties = Properties()
            try {
                javaClass.getResourceAsStream("/oAuth.properties").use { inputStream ->
                    oAuthProperties.load(inputStream)
                }

                oAuthProperties.stringPropertyNames()
                    .associateWith { oAuthProperties.getProperty(it) }

                _properties = oAuthProperties
                val clientId = oAuthProperties.getProperty("app.clientId")
                val redirectUrl = oAuthProperties.getProperty("app.redirectURL")
                val authTenantId = oAuthProperties.getProperty("app.authTenant")
                val graphUserScopes = Arrays
                    .asList(
                        *oAuthProperties.getProperty("app.graphUserScopes").split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray())
                _interactiveCredential = InteractiveBrowserCredentialBuilder()
                    .clientId(clientId)
                    .tenantId(authTenantId)
                    .redirectUrl(redirectUrl)
                    .build()
                val authProvider = TokenCredentialAuthProvider(graphUserScopes, _interactiveCredential)
                _userClient = GraphServiceClient.builder()
                    .authenticationProvider(authProvider)
                    .buildClient()
                synced = true;
//                val context = TokenRequestContext()
//                graphUserScopes?.forEach { context.addScopes(it) }
//                authToken = _interactiveCredential!!.getToken(context).block()
//                println(authToken?.token)
            } catch (e: IOException) {
                println("Unable to read OAuth configuration: "+ e)
            }

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
        fun printEvent(e: Event) {
            val doc : Document = Jsoup.parse(e.body?.content)
            val contentBody = doc.body().text()
//            println("Subject: " + e.subject)
//            println("Body: " + contentBody)
//            println("Start: " + e.start?.dateTime)
//            println("End: " + e.end?.dateTime)
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
                    events.currentPage.forEach{
                        eventList.add(it)
                    }
                    if(events.nextPage == null) {
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

