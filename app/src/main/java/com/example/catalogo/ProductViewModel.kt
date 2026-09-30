package com.example.catalogo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val productDao = AppDatabase
        .getDatabase(application)
        .productDao()

    val products: StateFlow<List<Product>> =
        productDao.getAllProducts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addProduct(
        name: String,
        price: Double,
        description: String
    ) {
        viewModelScope.launch {
            productDao.insertProduct(
                Product(
                    name = name,
                    price = price,
                    description = description
                )
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productDao.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.deleteProduct(product)
        }
    }
}
