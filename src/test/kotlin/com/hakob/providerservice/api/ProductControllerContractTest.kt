package com.hakob.providerservice.api

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import com.hakob.providerservice.model.Product
import com.hakob.providerservice.repository.ProductRepository
import org.apache.hc.core5.http.HttpRequest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

//@PactFolder("pacts")
@Provider("provider-service")
@PactBroker(
    host = "16.171.86.61",
    port = "80",
//    authentication = PactBrokerAuth(username = "pact_workshop", password = "pact_workshop")
)
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerContractTest(
//    @MockBean
//    private val productRepository: ProductRepository

) {
    @LocalServerPort
    var port = 0

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = HttpTestTarget("localhost", port)
        System.setProperty("pact.verifier.publishResults", "true");
        println("")
//        context.target(HttpTestTarget("localhost", port))
    }


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun verifyPact(context: PactVerificationContext, request: HttpRequest?) {
//        replaceAuthHeader(request)
        context.verifyInteraction()
    }

    @State("products exist")
    fun toProductsExistState() {
//        Mockito.`when`(productRepository.getProduct()).thenReturn(
//            Product("product1")
//        )
    }

}
