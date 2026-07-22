package com.demo.rickmorty.presentation.characterlist

import androidx.paging.PagingData
import app.cash.turbine.test
import com.demo.rickmorty.domain.usecase.GetCharactersUseCase
import com.demo.rickmorty.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

/**
 * Tests the MVI contract of CharacterListViewModel: given an Intent, the
 * resulting State (and, where relevant, Effect) is asserted - independent
 * of the Compose UI layer.
 */
class CharacterListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCharactersUseCase: GetCharactersUseCase = mockk()
    private lateinit var viewModel: CharacterListViewModel

    private fun createViewModel() {
        every { getCharactersUseCase(any()) } returns flowOf(PagingData.empty())
        viewModel = CharacterListViewModel(getCharactersUseCase)
    }

    @Test
    fun `initial state has empty query and hidden search bar`() {
        createViewModel()

        val state = viewModel.state.value

        assertThat(state.searchQuery).isEmpty()
        assertThat(state.isSearchBarVisible).isFalse()
    }

    @Test
    fun `OnToggleSearchBar intent flips visibility`() {
        createViewModel()

        viewModel.onIntent(CharacterListIntent.OnToggleSearchBar)

        assertThat(viewModel.state.value.isSearchBarVisible).isTrue()
    }

    @Test
    fun `OnToggleSearchBar off again clears the query`() {
        createViewModel()

        viewModel.onIntent(CharacterListIntent.OnToggleSearchBar) // show
        viewModel.onIntent(CharacterListIntent.OnSearchQueryChanged("morty"))
        viewModel.onIntent(CharacterListIntent.OnToggleSearchBar) // hide

        assertThat(viewModel.state.value.searchQuery).isEmpty()
    }

    @Test
    fun `OnSearchQueryChanged updates state immediately`() {
        createViewModel()

        viewModel.onIntent(CharacterListIntent.OnSearchQueryChanged("summer"))

        assertThat(viewModel.state.value.searchQuery).isEqualTo("summer")
    }

    @Test
    fun `OnClearSearch resets query to empty`() {
        createViewModel()

        viewModel.onIntent(CharacterListIntent.OnSearchQueryChanged("beth"))
        viewModel.onIntent(CharacterListIntent.OnClearSearch)

        assertThat(viewModel.state.value.searchQuery).isEmpty()
    }

    @Test
    fun `search query change triggers a new use case call after debounce`() = runTest {
        createViewModel()

        viewModel.pagingDataFlow.test {
            viewModel.onIntent(CharacterListIntent.OnSearchQueryChanged("rick"))
            awaitItem() // empty PagingData emitted once the debounced query resolves
            cancelAndIgnoreRemainingEvents()
        }

        verify(atLeast = 1) { getCharactersUseCase("rick") }
    }
}
