package com.example.rental_recommend.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rental_recommend.model.RentalHouse
import com.example.rental_recommend.viewmodel.RentalViewModel
import com.example.rental_recommend.viewmodel.RentalListState
import com.example.rental_recommend.components.RentalItem
import com.example.rental_recommend.viewmodel.RentalViewModelFactory
import com.example.rental_recommend.viewmodel.RentalEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: RentalViewModel = viewModel(
        factory = RentalViewModelFactory(LocalContext.current)
    )
) {
    val rentalListState by viewModel.rentalListState.collectAsState()
    val events by viewModel.events.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(events) {
        when (events) {
            is RentalEvent.AuthError -> {
                onNavigateToAuth()
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadRentalList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的收藏") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        when (val state = rentalListState) {
            is RentalListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is RentalListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message)
                }
            }
            is RentalListState.Success -> {
                if (state.rentals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无收藏",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.rentals) { rental ->
                            RentalItem(
                                rental = rental,
                                onClick = { onNavigateToDetail(rental.id) },
                                onFavoriteClick = {
                                    viewModel.toggleFavorite(rental.id)
                                },
                                isFavorite = true
                            )
                        }
                    }
                }
            }
        }
    }
}