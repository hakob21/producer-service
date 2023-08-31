package com.hakob.providerservice.api

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import org.apache.hc.core5.http.HttpRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@Provider("provider-service")
@PactBroker(
    host = "16.171.86.61",
    port = "80",
)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("pact-test")
class ProductControllerContractTest {
    @LocalServerPort
    var port = 0

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = HttpTestTarget("localhost", port)
    }


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun verifyPact(context: PactVerificationContext, request: HttpRequest?) {
        context.verifyInteraction()
    }
}
