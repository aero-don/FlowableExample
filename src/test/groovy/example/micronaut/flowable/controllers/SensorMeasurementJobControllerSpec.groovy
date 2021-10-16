package example.micronaut.flowable.controllers

import io.micronaut.core.io.buffer.ByteBuffer
import io.micronaut.http.client.annotation.Client
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.reactor.http.client.ReactorHttpClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import reactor.core.publisher.Flux
import reactor.test.StepVerifier;
import spock.lang.AutoCleanup
import spock.lang.Specification
import spock.lang.Shared

import jakarta.inject.Inject

@MicronautTest
class SensorControllerSpec extends Specification {

    @Shared @Inject
    EmbeddedServer embeddedServer

    @Shared @AutoCleanup @Inject @Client("/")
    ReactorHttpClient client

    void "test index"() {
        given:
        Flux<HttpResponse<ByteBuffer>> response = client.exchange("/sensor")

        expect:
        StepVerifier.create(response.flatMap(res -> res.status())).expectNext(HttpStatus.OK)
    }
}
