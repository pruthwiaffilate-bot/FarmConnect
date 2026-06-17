package com.farmconnect

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun FarmConnectApp(viewModel: FarmConnectViewModel) {
    val navController = rememberNavController()
    var otpMismatch by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = if (viewModel.farmer.value == null) Screen.Registration.route else Screen.Marketplace.route
    ) {
        composable(Screen.Registration.route) {
            RegistrationScreen(
                onRegister = { name, phone, landArea, landLocation, otp ->
                    if (otp == viewModel.otpCode.value) {
                        viewModel.registerFarmer(name, phone, landArea, landLocation)
                        navController.navigate(Screen.Marketplace.route) {
                            popUpTo(Screen.Registration.route) { inclusive = true }
                        }
                    }
                },
                incorrectOtp = otpMismatch,
                onOtpMismatch = { otpMismatch = it }
            )
        }
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(
                farmer = viewModel.farmer.value,
                products = viewModel.products,
                onAddProduct = { navController.navigate(Screen.AddProduct.route) },
                onBuyProduct = { id -> viewModel.buyProduct(id) }
            )
        }
        composable(Screen.AddProduct.route) {
            AddProductScreen(
                onSaveProduct = { title, description, price, quantity ->
                    viewModel.addProduct(title, description, price, quantity)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun RegistrationScreen(
    onRegister: (String, String, String, String, String) -> Unit,
    incorrectOtp: Boolean,
    onOtpMismatch: (Boolean) -> Unit,
) {
    val name = remember { mutableStateOf("") }
    val phone = remember { mutableStateOf("") }
    val landArea = remember { mutableStateOf("") }
    val landLocation = remember { mutableStateOf("") }
    val otp = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Farmer Registration", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = phone.value, onValueChange = { phone.value = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = landArea.value, onValueChange = { landArea.value = it }, label = { Text("Land Area") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = landLocation.value, onValueChange = { landLocation.value = it }, label = { Text("Land Location") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = otp.value, onValueChange = { otp.value = it; onOtpMismatch(false) }, label = { Text("OTP (try 1234)") }, modifier = Modifier.fillMaxWidth())
        if (incorrectOtp) {
            Text("Incorrect OTP. Please enter 1234.", color = Color.Red)
        }
        Button(onClick = {
            if (otp.value == "1234") {
                onRegister(name.value, phone.value, landArea.value, landLocation.value, otp.value)
            } else {
                onOtpMismatch(true)
            }
        }, modifier = Modifier.align(Alignment.End)) {
            Text("Register")
        }
    }
}

@Composable
fun MarketplaceScreen(
    farmer: Farmer?,
    products: List<Product>,
    onAddProduct: () -> Unit,
    onBuyProduct: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("FarmConnect Marketplace", fontWeight = FontWeight.Bold)
        farmer?.let {
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Farmer: ${it.name}")
                    Text("Phone: ${it.phone}")
                    Text("Land: ${it.landArea}, ${it.landLocation}")
                }
            }
        }
        Button(onClick = onAddProduct) {
            Text("Upload Product")
        }
        Text("Available Products", fontWeight = FontWeight.SemiBold)
        if (products.isEmpty()) {
            Text("No products yet. Add one to start selling.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(products) { product ->
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(product.title, fontWeight = FontWeight.Bold)
                            Text(product.description)
                            Text("Price: ₹${product.price}")
                            Text("Quantity: ${product.quantity}")
                            Text(if (product.sold) "Sold" else "In stock", color = if (product.sold) Color.Gray else Color(0xFF2E7D32))
                            if (!product.sold) {
                                Button(onClick = { onBuyProduct(product.id) }) {
                                    Text("Buy")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductScreen(
    onSaveProduct: (String, String, String, String) -> Unit,
) {
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val quantity = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Upload Product", fontWeight = FontWeight.Bold)
        OutlinedTextField(value = title.value, onValueChange = { title.value = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = price.value, onValueChange = { price.value = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = quantity.value, onValueChange = { quantity.value = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { onSaveProduct(title.value, description.value, price.value, quantity.value) }, modifier = Modifier.align(Alignment.End)) {
            Text("Save Product")
        }
    }
}
