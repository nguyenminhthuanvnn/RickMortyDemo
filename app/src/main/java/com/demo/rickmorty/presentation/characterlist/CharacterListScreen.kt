package com.demo.rickmorty.presentation.characterlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import android.widget.Toast
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.ui.components.CharacterItem
import com.demo.rickmorty.ui.components.LoadStateFooter

/**
 * Stateful entry point - wired to Hilt's ViewModel. Kept separate from the
 * stateless CharacterListContent below so the content composable is
 * trivially previewable/testable without a real ViewModel.
 */
@Composable
fun CharacterListScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CharacterListEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is CharacterListEffect.NavigateToDetail ->
                    onNavigateToDetail(effect.characterId)
            }
        }
    }

    CharacterListContent(
        state = state,
        pagingItems = pagingItems,
        onIntent = { viewModel.onIntent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListContent(
    state: CharacterListState,
    pagingItems: LazyPagingItems<Character>,
    onIntent: (CharacterListIntent) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text("Rick & Morty Characters") },
                    actions = {
                        IconButton(onClick = { onIntent(CharacterListIntent.OnToggleSearchBar) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                )
                if (state.isSearchBarVisible) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { onIntent(CharacterListIntent.OnSearchQueryChanged(it)) },
                        placeholder = { Text("Search by name…") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                // Initial full-screen loading state (first page, nothing cached yet)
                pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount == 0 -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                // Initial load failed
                pagingItems.loadState.refresh is LoadState.Error && pagingItems.itemCount == 0 -> {
                    val error = pagingItems.loadState.refresh as LoadState.Error
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error.error.localizedMessage ?: "Something went wrong",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = { pagingItems.retry() },
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                // Empty result set (e.g. search with no matches)
                pagingItems.itemCount == 0 -> {
                    Text(
                        text = "No characters found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    // The LazyColumn scrolling near the bottom is what drives
                    // Paging 3 to call load() for the next page automatically -
                    // this is the "scroll down to load more" behaviour.
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(
                            count = pagingItems.itemCount,
                            key = pagingItems.itemKey { it.id }
                        ) { index ->
                            val character = pagingItems[index]
                            if (character != null) {
                                CharacterItem(
                                    character = character,
                                    onClick = {
                                        onIntent(CharacterListIntent.OnCharacterClicked(character.id))
                                    }
                                )
                            }
                        }

                        item {
                            LoadStateFooter(
                                loadState = pagingItems.loadState.append,
                                onRetry = { pagingItems.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}
