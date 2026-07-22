package com.demo.rickmorty.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState

/**
 * Renders the footer row Paging 3 shows while a NEXT page is loading, or a
 * retry button if the next-page fetch failed - this is what the user sees
 * while scrolling down triggers "load more".
 */
@Composable
fun LoadStateFooter(
    loadState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            is LoadState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = loadState.error.localizedMessage ?: "Couldn't load more",
                    style = MaterialTheme.typography.bodySmall
                )
                Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Retry")
                }
            }
            is LoadState.NotLoading -> Unit
        }
    }
}
