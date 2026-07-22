package com.demo.rickmorty.presentation

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.model.CharacterStatus
import com.demo.rickmorty.presentation.characterlist.CharacterListContent
import com.demo.rickmorty.presentation.characterlist.CharacterListState
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI test exercising the stateless CharacterListContent directly,
 * bypassing Hilt/ViewModel wiring so it stays fast and focused on rendering.
 */
class CharacterListContentTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private val sampleCharacters = listOf(
        Character(
            id = 1,
            name = "Rick Sanchez",
            status = CharacterStatus.ALIVE,
            species = "Human",
            gender = "Male",
            imageUrl = "https://example.com/rick.png",
            originName = "Earth",
            locationName = "Citadel of Ricks"
        )
    )

    @Test
    fun characterName_isDisplayed_whenPagingDataLoaded() {
        composeRule.setContent {
            val items = flowOf(PagingData.from(sampleCharacters)).collectAsLazyPagingItems()
            CharacterListContent(
                state = CharacterListState(),
                pagingItems = items,
                onIntent = {}
            )
        }

        composeRule.onNodeWithText("Rick Sanchez").assertExists()
    }

    @Test
    fun searchBar_isShown_whenStateIsSearchActive() {
        composeRule.setContent {
            val items = flowOf(PagingData.from(sampleCharacters)).collectAsLazyPagingItems()
            CharacterListContent(
                state = CharacterListState(isSearchBarVisible = true, searchQuery = "rick"),
                pagingItems = items,
                onIntent = {}
            )
        }

        composeRule.onNodeWithText("rick").assertExists()
    }
}
