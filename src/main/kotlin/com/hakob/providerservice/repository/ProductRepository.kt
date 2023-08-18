package com.hakob.providerservice.repository

import com.hakob.providerservice.model.Product
import org.springframework.stereotype.Repository

@Repository
class ProductRepository {
    fun getProduct(): Product {
        return Product("product1")
    }
}