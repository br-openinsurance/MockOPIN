package com.raidiam.trustframework.mockinsurance.services

import com.raidiam.trustframework.mockinsurance.CleanupSpecification
import com.raidiam.trustframework.mockinsurance.domain.WebhookEntity
import com.raidiam.trustframework.mockinsurance.models.generated.UpdateWebhook
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Stepwise

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
                .clientId("random_client_id")
                .webhookUri("https://example.com")
        def req2 = new UpdateWebhook()
                .clientId("random_client_id")

        when:
        def resp = webhookService.setWebhookUri(req)

        then:
        noExceptionThrown()
        resp.clientId == req.clientId
        resp.webhookUri == req.webhookUri

        when:
        resp = webhookService.setWebhookUri(req2)

        then:
        noExceptionThrown()
        resp.clientId == req2.clientId
        resp.webhookUri == null
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