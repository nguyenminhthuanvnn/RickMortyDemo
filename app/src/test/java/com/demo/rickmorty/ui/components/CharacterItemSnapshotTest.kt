package com.demo.rickmorty.ui.components

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.demo.rickmorty.domain.model.Character
import com.demo.rickmorty.domain.model.CharacterStatus
import com.demo.rickmorty.presentation.theme.RickMortyTheme
import com.demo.rickmorty.util.MainDispatcherRule
import org.junit.Rule
import org.junit.Test

class CharacterItemSnapshotTest {
    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5
    )

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun characterItem_Alive() {
        paparazzi.snapshot {
            RickMortyTheme {
                CharacterItem(
                    character = Character(
                        id = 1,
                        name = "Rick Sanchez",
                        status = CharacterStatus.ALIVE,
                        species = "Human",
                        gender = "Male",
                        imageUrl = "",
                        originName = "Earth",
                        locationName = "Earth"
                    ),
                    onClick = {}
                )
            }
        }
    }
}
