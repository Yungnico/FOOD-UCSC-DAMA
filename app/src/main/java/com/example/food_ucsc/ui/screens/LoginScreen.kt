package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    // Colores usados en HomeScreen
    val moradoPrincipal = Color(0xFF6750A4)
    val fondoClaro = Color(0xFFFBF8FF)
    val inputColor = Color(0xFFF3F0F8)
    val textoSecundario = Color(0xFF7A7A7A)

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

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = fondoClaro,
                        shape = RoundedCornerShape(
                            topStart = 36.dp,
                            topEnd = 36.dp
                        )
                    )
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(70.dp))

                // Logo superior
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = stringResource(R.string.logo_desc),
                    tint = moradoPrincipal,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.welcome),
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = moradoPrincipal
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.login_continue),
                    color = textoSecundario,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(36.dp))

                // NAME
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.email_label).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textoSecundario
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputColor,
                            unfocusedContainerColor = inputColor,
                            disabledContainerColor = inputColor,

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,

                            focusedTextColor = moradoPrincipal,
                            unfocusedTextColor = moradoPrincipal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // PASSWORD
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.password_label).uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textoSecundario
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = inputColor,
                            unfocusedContainerColor = inputColor,
                            disabledContainerColor = inputColor,

                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,

                            focusedTextColor = moradoPrincipal,
                            unfocusedTextColor = moradoPrincipal
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color(0xFFB3261E),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        authViewModel.login(email = email, password = password)
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
                            text = stringResource(R.string.login_button),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(
                    onClick = { }
                ) {
                    Text(
                        text = stringResource(R.string.forgot_password),
                        color = textoSecundario,
                        fontSize = 13.sp
                    )
                }

                TextButton(
                    onClick = {
                        navController.navigate(Screen.Register.route)
                    }
                ) {
                    Text(
                        text = stringResource(R.string.register_prompt),
                        color = moradoPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}