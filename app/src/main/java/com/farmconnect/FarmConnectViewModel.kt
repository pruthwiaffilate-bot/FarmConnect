package com.farmconnect

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

sealed class Screen(val route: String) {
    object Registration : Screen("registration")
    object Marketplace : Screen("marketplace")
    object AddProduct : Screen("add_product")
}

data class Farmer(
    val name: String,
    val phone: String,
    val landArea: String,
    val landLocation: String,
)

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: String,
    val quantity: String,
    var sold: Boolean = false,
)

class FarmConnectViewModel : ViewModel() {
    val farmer = mutableStateOf<Farmer?>(null)
    val products = mutableStateListOf<Product>()
    val otpCode = mutableStateOf("1234")

    fun registerFarmer(name: String, phone: String, landArea: String, landLocation: String) {
        farmer.value = Farmer(name, phone, landArea, landLocation)
    }

    fun addProduct(title: String, description: String, price: String, quantity: String) {
        products.add(Product(
            id = products.size + 1,
            title = title,
            description = description,
            price = price,
            quantity = quantity,
        ))
    }

    fun buyProduct(productId: Int) {
        products.indexOfFirst { it.id == productId }.takeIf { it != -1 }?.let { index ->
            products[index] = products[index].copy(sold = true)
        }
    }
}
