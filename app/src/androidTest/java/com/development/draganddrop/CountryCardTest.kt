package com.development.draganddrop

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.development.draganddrop.ui.theme.DragAndDropTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CountryCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun myTest() {
        val flagPath = "https://upload.wikimedia.org/wikipedia/commons/9/94/Nuvola_Indian_flag.svg"
        val countryName = "India"
        composeTestRule.apply {
            setContent {
                DragAndDropTheme {
                    CountryCard(url = flagPath, name = countryName)
                }
            }
            onNodeWithContentDescription(countryName).assertIsDisplayed()
            onNodeWithText(text = countryName).assertIsDisplayed()
        }
    }
}