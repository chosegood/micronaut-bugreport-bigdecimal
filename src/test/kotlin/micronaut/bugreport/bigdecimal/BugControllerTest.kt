package micronaut.bugreport.bigdecimal

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.runtime.server.EmbeddedServer
import org.apache.http.client.entity.GzipCompressingEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.EntityTemplate
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.HttpClients
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BugControllerTest {

    var embeddedServer: EmbeddedServer = ApplicationContext.run(EmbeddedServer::class.java)

    @BeforeEach
    fun setup() {
        embeddedServer.start()
    }

    @AfterEach
    fun tearDown() {
        embeddedServer.stop()
    }

    @Test
    fun jacksonRetainsPrecision() {
        val objectMapper = embeddedServer.applicationContext.getBean(ObjectMapper::class.java)

        val initialDataset = mapOf<String, Any?>(
                "string" to "string",
                "bigDecimal" to BigDecimal("888.7794538169553400000")
        )

        val writeValueAsBytes = objectMapper.writeValueAsBytes(initialDataset)
        val responseDataset = objectMapper.readValue<Map<String, Any?>>(writeValueAsBytes)
        assertThat(responseDataset, equalTo(initialDataset))
    }

    @Test
    fun micronautRetainsPrecision() {
        val objectMapper = embeddedServer.applicationContext.getBean(ObjectMapper::class.java)
        val httpClient = HttpClients.createDefault()

        val initialDataset = mapOf<String, Any?>(
                "string" to "string",
                "bigDecimal" to BigDecimal("888.7794538169553400000")
        )

        val postRequest = HttpPost(embeddedServer.uri.resolve("/bug"))
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
        postRequest.entity = GzipCompressingEntity(EntityTemplate { out -> objectMapper.writeValue(out, initialDataset) })

        httpClient.execute(postRequest, BasicResponseHandler())

        val getRequest = HttpGet(embeddedServer.uri.resolve("/bug"))
        val getResponse = httpClient.execute(getRequest, BasicResponseHandler())
        val responseDataset = objectMapper.readValue<Map<String, Any?>>(getResponse)
        assertThat(responseDataset, equalTo(initialDataset))
    }

}
