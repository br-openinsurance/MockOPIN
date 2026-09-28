package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.cleanups.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.domain.WebhookEntity
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.exceptions.HttpStatusException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
@MicronautTest(transactional = false, environments = ["db"])
class WebhookServiceSpec extends CleanupSpecification {
    @Inject
    WebhookService webhookService

    @Inject
    HttpClient httpClient

    @MockBean(HttpClient)
    HttpClient mockHttpClient() {
        return Mock(HttpClient)
    }

    def "We can create and update a webhook record"() {
        given:
        def req = new UpdateWebhook()
                .webhookUri("https://example.com")
        def req2 = new UpdateWebhook()
        def clientId = "random_client_id"

        when:
        def resp = webhookService.updateWebhook(req, clientId)

        then:
        noExceptionThrown()
        resp.clientId == clientId
        resp.webhookUri == req.webhookUri

        when:
        resp = webhookService.updateWebhook(req2, clientId)

        then:
        noExceptionThrown()
        resp.clientId == clientId
        resp.webhookUri == null
    }

    @Unroll
    def "updateWebhook rejects an invalid webhook URI - #description"() {
        when:
        webhookService.updateWebhook(new UpdateWebhook().webhookUri(invalidUri), UUID.randomUUID().toString())

        then:
        def ex = thrown(HttpStatusException)
        ex.status == HttpStatus.BAD_REQUEST

        where:
        description                              | invalidUri
        "non-https scheme"                       | "http://example.com/webhook"
        "missing scheme"                         | "example.com/webhook"
        "malformed URI"                          | "https://[invalid"
        "missing host"                           | "https:///path"
        "localhost hostname"                     | "https://localhost/webhook"
        "loopback IP literal"                    | "https://127.0.0.1/webhook"
        "link-local IP literal (cloud metadata)" | "https://169.254.169.254/latest/meta-data"
        "private IP literal"                     | "https://10.0.0.5/webhook"
    }

    def "Sending a notification for an unregistered client doesn't result in error"() {
        when:
        webhookService.notify("random_client", "/webhook")

        then:
        noExceptionThrown()
    }

    def "Sending a notification for a client without webhook uri doesn't result in error"() {
        given:
        def entity = new WebhookEntity()
        entity.setClientId("random_client")
        webhookRepository.save(entity)

        when:
        webhookService.notify("random_client", "/webhook")

        then:
        noExceptionThrown()
    }

    def "We can send a notification"() {
        given:
        def clientId = UUID.randomUUID().toString()
        def entity = new WebhookEntity()
        entity.setClientId(clientId)
        entity.setWebhookUri("https://webhook.com")
        webhookRepository.save(entity)

        def response = Mock(HttpResponse) {
            code() >> 200
            body() >> "Mocked Response"
        }
        def blockingHttpClient = Mock(BlockingHttpClient) {
            exchange(_ as HttpRequest, String.class) >> response
        }

        // Mock behavior for HttpClient toBlocking()
        httpClient.toBlocking() >> blockingHttpClient

        when:
        webhookService.notify(clientId, "/webhook")

        then:
        noExceptionThrown()
    }

    def "enable cleanup"() {
        //This must be the final test
        when:
        runCleanup = true

        then:
        runCleanup
    }
}