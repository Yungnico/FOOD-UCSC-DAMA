package com.example.food_ucsc.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.food_ucsc.R
import com.example.food_ucsc.ui.viewmodel.AppViewModelProvider
import com.example.food_ucsc.ui.viewmodel.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
    navController: NavController,
    orderId: String,
    viewModel: RatingViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) {
        orderId.toIntOrNull()?.let { viewModel.setPurchaseId(it) }
    }

    if (uiState.isSubmitted) {
        AlertDialog(
            onDismissRequest = { navController.popBackStack() },
            confirmButton = {
                TextButton(onClick = { navController.popBackStack() }) {
                    Text(stringResource(R.string.accept))
                }
            },
            title = { Text(stringResource(R.string.thank_you)) },
            text = { Text(stringResource(R.string.rating_sent_success)) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calify_order), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBF8FF))
            )

            uiState.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = Color(0xFFB3261E),
                    fontSize = 12.sp
                )
            }
        },
        containerColor = Color(0xFFFBF8FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.order_number, orderId),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // Order Rating Section
            RatingSection(
                title = stringResource(R.string.how_was_order),
                rating = uiState.orderRating,
                onRatingChange = { viewModel.updateOrderRating(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Comment Section
            OutlinedTextField(
                value = uiState.comment,
                onValueChange = { viewModel.updateComment(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.order_comment_label)) },
                placeholder = { Text(stringResource(R.string.order_comment_placeholder)) },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Restaurant Rating Section
            RatingSection(
                title = stringResource(R.string.how_was_place),
                rating = uiState.restaurantRating,
                onRatingChange = { viewModel.updateRestaurantRating(it) }
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.submitRating() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                enabled = uiState.orderRating > 0 && uiState.restaurantRating > 0 && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.send_rating), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RatingSection(
    title: String,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF21005D)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.stars_desc, i),
                    tint = if (i <= rating) Color(0xFFFFB74D) else Color.Gray,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onRatingChange(i) }
                )
            }
        }
    }
}
