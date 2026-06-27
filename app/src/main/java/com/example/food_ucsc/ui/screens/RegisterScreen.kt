package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.navigation.Screen
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
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
            .background(MaterialTheme.colorScheme.primary)
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
                    contentDescription = stringResource(R.string.back_label),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.background,
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
                    text = stringResource(R.string.create_account),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 38.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.already_registered),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                // NAME
                InputLabel(stringResource(R.string.name_label).uppercase(), MaterialTheme.colorScheme.onSurfaceVariant)

                CustomTextField(
                    value = nombre,
                    onValueChange = { nombre = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                InputLabel(stringResource(R.string.last_name_paternal_label).uppercase(), MaterialTheme.colorScheme.onSurfaceVariant)
                CustomTextField(
                    value = apellidoPaterno,
                    onValueChange = { apellidoPaterno = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                InputLabel(stringResource(R.string.last_name_maternal_label).uppercase(), MaterialTheme.colorScheme.onSurfaceVariant)
                CustomTextField(
                    value = apellidoMaterno,
                    onValueChange = { apellidoMaterno = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // EMAIL
                InputLabel(stringResource(R.string.email_label).uppercase(), MaterialTheme.colorScheme.onSurfaceVariant)

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(18.dp))

                // PASSWORD
                InputLabel(stringResource(R.string.password_label).uppercase(), MaterialTheme.colorScheme.onSurfaceVariant)

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                uiState.errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
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
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.register_button),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
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
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    )
}
