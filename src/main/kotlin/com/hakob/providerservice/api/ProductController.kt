package com.hakob.providerservice.api

import com.hakob.providerservice.model.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProductController {

    @GetMapping("/product")
    fun getProduct(): Product {
        return Product()
    }
}