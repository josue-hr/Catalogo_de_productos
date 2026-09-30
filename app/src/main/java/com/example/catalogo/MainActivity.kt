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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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


/*
 * Pantalla principal del catálogo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen() {

    // Conectamos la pantalla con nuestro ViewModel
    val viewModel: ProductViewModel = viewModel()

    // Obtenemos los productos guardados en Room
    val productList by viewModel.products.collectAsState()

    // Controla si se muestra el diálogo para agregar
    var showAddDialog by remember {
        mutableStateOf(false)
    }

    // Guarda el producto que queremos editar
    var productToEdit by remember {
        mutableStateOf<Product?>(null)
    }


    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text("Catálogo de Productos")
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },


        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    showAddDialog = true
                }
            ) {

                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar Producto"
                )
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

                ProductItemCard(

                    product = product,

                    // EDITAR
                    onEdit = {
                        productToEdit = product
                    },

                    // ELIMINAR
                    onDelete = {
                        viewModel.deleteProduct(product)
                    }
                )
            }
        }


        // =====================================================
        // DIÁLOGO PARA AGREGAR PRODUCTO
        // =====================================================

        if (showAddDialog) {

            AddProductDialog(

                onDismiss = {
                    showAddDialog = false
                },

                onAddProduct = { newName, newPrice, newDesc ->

                    viewModel.addProduct(

                        name = newName,

                        price = newPrice.toDoubleOrNull() ?: 0.0,

                        description = newDesc
                    )

                    showAddDialog = false
                }
            )
        }


        // =====================================================
        // DIÁLOGO PARA EDITAR PRODUCTO
        // =====================================================

        if (productToEdit != null) {

            EditProductDialog(

                product = productToEdit!!,

                onDismiss = {
                    productToEdit = null
                },

                onUpdateProduct = { updatedName, updatedPrice, updatedDesc ->

                    val updatedProduct = productToEdit!!.copy(

                        name = updatedName,

                        price = updatedPrice.toDoubleOrNull() ?: 0.0,

                        description = updatedDesc
                    )

                    viewModel.updateProduct(updatedProduct)

                    productToEdit = null
                }
            )
        }
    }
}


/*
 * Tarjeta que muestra un producto.
 */
@Composable
fun ProductItemCard(

    product: Product,

    onEdit: () -> Unit,

    onDelete: () -> Unit

) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)

        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically

            ) {

                Text(

                    text = product.name,

                    style = MaterialTheme.typography.titleMedium,

                    modifier = Modifier.weight(1f)
                )


                // Botones de editar y eliminar
                Row {

                    // EDITAR
                    IconButton(
                        onClick = onEdit
                    ) {

                        Icon(

                            Icons.Default.Edit,

                            contentDescription = "Editar",

                            tint = MaterialTheme.colorScheme.primary
                        )
                    }


                    // ELIMINAR
                    IconButton(
                        onClick = onDelete
                    ) {

                        Icon(

                            Icons.Default.Delete,

                            contentDescription = "Eliminar",

                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // PRECIO
            Text(

                text = "$ ${product.price}",

                style = MaterialTheme.typography.bodyLarge,

                color = MaterialTheme.colorScheme.primary
            )


            Spacer(
                modifier = Modifier.height(4.dp)
            )


            // DESCRIPCIÓN
            Text(

                text = product.description,

                style = MaterialTheme.typography.bodyMedium,

                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


/*
 * Diálogo para agregar un producto.
 */
@Composable
fun AddProductDialog(

    onDismiss: () -> Unit,

    onAddProduct: (String, String, String) -> Unit

) {

    var name by remember {
        mutableStateOf("")
    }

    var price by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var showError by remember {
        mutableStateOf(false)
    }


    AlertDialog(

        onDismissRequest = onDismiss,


        title = {
            Text("Agregar Nuevo Producto")
        },


        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // NOMBRE
                OutlinedTextField(

                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Nombre del producto")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // PRECIO
                OutlinedTextField(

                    value = price,

                    onValueChange = {
                        price = it
                    },

                    label = {
                        Text("Precio")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // DESCRIPCIÓN
                OutlinedTextField(

                    value = description,

                    onValueChange = {
                        description = it
                    },

                    label = {
                        Text("Descripción")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                // MENSAJE DE ERROR
                if (showError) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(

                        text = "Por favor completa el nombre y el precio",

                        color = MaterialTheme.colorScheme.error,

                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },


        // BOTÓN GUARDAR
        confirmButton = {

            Button(

                onClick = {

                    if (
                        name.isBlank() ||
                        price.isBlank()
                    ) {

                        showError = true

                    } else {

                        onAddProduct(
                            name,
                            price,
                            description
                        )
                    }
                }

            ) {

                Text("Guardar")
            }
        },


        // BOTÓN CANCELAR
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancelar")
            }
        }
    )
}


/*
 * Diálogo para editar un producto.
 */
@Composable
fun EditProductDialog(

    product: Product,

    onDismiss: () -> Unit,

    onUpdateProduct: (String, String, String) -> Unit

) {

    var name by remember {
        mutableStateOf(product.name)
    }

    var price by remember {
        mutableStateOf(product.price.toString())
    }

    var description by remember {
        mutableStateOf(product.description)
    }

    var showError by remember {
        mutableStateOf(false)
    }


    AlertDialog(

        onDismissRequest = onDismiss,


        title = {
            Text("Editar Producto")
        },


        text = {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // NOMBRE
                OutlinedTextField(

                    value = name,

                    onValueChange = {
                        name = it
                    },

                    label = {
                        Text("Nombre del producto")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // PRECIO
                OutlinedTextField(

                    value = price,

                    onValueChange = {
                        price = it
                    },

                    label = {
                        Text("Precio")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                // DESCRIPCIÓN
                OutlinedTextField(

                    value = description,

                    onValueChange = {
                        description = it
                    },

                    label = {
                        Text("Descripción")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                // MENSAJE DE ERROR
                if (showError) {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Text(

                        text = "Por favor completa el nombre y el precio",

                        color = MaterialTheme.colorScheme.error,

                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },


        // BOTÓN ACTUALIZAR
        confirmButton = {

            Button(

                onClick = {

                    if (
                        name.isBlank() ||
                        price.isBlank()
                    ) {

                        showError = true

                    } else {

                        onUpdateProduct(
                            name,
                            price,
                            description
                        )
                    }
                }

            ) {

                Text("Actualizar")
            }
        },


        // BOTÓN CANCELAR
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Cancelar")
            }
        }
    )
}
