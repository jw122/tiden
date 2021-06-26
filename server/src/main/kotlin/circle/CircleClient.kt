package circle

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun getStablecoins(): String {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://api-sandbox.circle.com/v1/stablecoins")
    println("got response: " + response.readText());
    client.close()
    return response.readText()
}