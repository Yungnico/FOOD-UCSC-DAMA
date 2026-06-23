package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val moradoPrincipal = Color(0xFF6750A4)
    val fondoClaro = Color(0xFFFBF8FF)
    val inputColor = Color(0xFFF3F0F8)
    val textoSecundario = Color(0xFF7A7A7A)

    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(moradoPrincipal)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = fondoClaro,
                        shape = RoundedCornerShape(
                            topStart = 40.dp,
                            topEnd = 40.dp
                        )
                    )
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Crear una nueva\ncuenta",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D1D1D),
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Ya estás registrado? Inicia sesión aquí",
                    color = textoSecundario,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // NAME
                InputLabel("NOMBRE", textoSecundario)

                CustomTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    inputColor = inputColor
                )

                Spacer(modifier = Modifier.height(18.dp))

                InputLabel("APELLIDO PATERNO", textoSecundario)
                CustomTextField(
                    value = apellidoPaterno,
                    onValueChange = { apellidoPaterno = it },
                    inputColor = inputColor
                )

                Spacer(modifier = Modifier.height(18.dp))

                InputLabel("APELLIDO MATERNO", textoSecundario)
                CustomTextField(
                    value = apellidoMaterno,
                    onValueChange = { apellidoMaterno = it },
                    inputColor = inputColor
                )

                Spacer(modifier = Modifier.height(18.dp))

                // EMAIL
                InputLabel("CORREO", textoSecundario)

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    inputColor = inputColor,
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PASSWORD
                InputLabel("CONTRASEÑA", textoSecundario)

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    inputColor = inputColor,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // FECHA
                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFB3261E),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        authViewModel.register(
                            nombre = nombre,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            email = email,
                            password = password
                        )
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = moradoPrincipal
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Registrarse",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InputLabel(
    text: String,
    color: Color
) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
    )
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    inputColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        visualTransformation = if (isPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = inputColor,
            unfocusedContainerColor = inputColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}