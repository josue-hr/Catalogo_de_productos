package com.example.catalogo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.catalogo.ui.theme.CatalogoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CatalogoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CatalogScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen() {
    val productList = remember {
        mutableStateListOf(
            Product(1, "Laptop Lenovo", 750.00, "Laptop para trabajo y estudio"),
            Product(2, "Mouse Inalámbrico", 25.50, "Mouse óptico ergonómico"),
            Product(3, "Teclado Mecánico", 45.00, "Teclado RGB con switches azules")
        )
    }

    // Estado para controlar si la ventana de agregar está abierta o cerrada
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo de Productos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // Botón flotante para crear registros
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Producto")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(productList) { product ->
                ProductItemCard(product = product)
            }
        }

        // Ventana emergente (Formulario de creación con validación)
        if (showAddDialog) {
            AddProductDialog(
                onDismiss = { showAddDialog = false },
                onAddProduct = { newName, newPrice, newDesc ->
                    val newId = (productList.maxOfOrNull { it.id } ?: 0) + 1
                    productList.add(
                        Product(
                            id = newId,
                            name = newName,
                            price = newPrice.toDoubleOrNull() ?: 0.0,
                            description = newDesc
                        )
                    )
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onAddProduct: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) } // Validación de campos vacíos

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nuevo Producto") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del producto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Precio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mensaje de error si intenta dejar campos obligatorios vacíos
                if (showError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Por favor completa el nombre y el precio",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validar formularios: que no vayan vacíos
                    if (name.isBlank() || price.isBlank()) {
                        showError = true
                    } else {
                        onAddProduct(name, price, description)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun ProductItemCard(product: Product) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$ ${product.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}