package com.demo.rickmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demo.rickmorty.presentation.characterdetail.CharacterDetailScreen
import com.demo.rickmorty.presentation.characterlist.CharacterListScreen
import com.demo.rickmorty.presentation.theme.RickMortyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RickMortyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "character_list"
                    ) {
                        composable("character_list") {
                            CharacterListScreen(
                                onNavigateToDetail = { id ->
                                    navController.navigate("character_detail/$id")
                                }
                            )
                        }
                        composable(
                            route = "character_detail/{characterId}",
                            arguments = listOf(
                                navArgument("characterId") { type = NavType.IntType }
                            )
                        ) {
                            CharacterDetailScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
