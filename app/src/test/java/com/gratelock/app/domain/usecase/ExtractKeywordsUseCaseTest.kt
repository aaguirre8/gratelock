package com.gratelock.app.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExtractKeywordsUseCaseTest {

    private lateinit var useCase: ExtractKeywordsUseCase

    @Before
    fun setUp() {
        useCase = ExtractKeywordsUseCase()
    }

    @Test
    fun invoke_extractsMeaningfulWords() {
        val result = useCase(
            "I love my morning coffee",
            "The sunshine was beautiful",
            "My dog is the best friend",
        )
        assertTrue(result.isNotEmpty())
        // Should not contain stop words
        val keywords = result.split(",")
        keywords.forEach { keyword ->
            assertTrue(keyword.length > 2)
        }
    }

    @Test
    fun invoke_removesStopWords() {
        val result = useCase(
            "I am grateful for the sun",
            "It is a beautiful day",
            "We are happy and thankful",
        )
        val keywords = result.split(",")
        assertTrue("the" !in keywords)
        assertTrue("for" !in keywords)
        assertTrue("and" !in keywords)
    }

    @Test
    fun invoke_returnsMaxFiveKeywords() {
        val result = useCase(
            "coffee morning sunshine happiness family friends dog cat music peace",
            "love nature garden flowers trees birds ocean river mountain valley",
            "health wellness strength courage wisdom patience kindness grace beauty hope",
        )
        if (result.isNotEmpty()) {
            val keywords = result.split(",")
            assertTrue(keywords.size <= 5)
        }
    }

    @Test
    fun invoke_emptyFields_returnsEmptyString() {
        val result = useCase("", "", "")
        assertEquals("", result)
    }

    @Test
    fun invoke_onlyStopWords_returnsEmptyString() {
        val result = useCase("I am the", "it is a", "we are so")
        assertEquals("", result)
    }

    @Test
    fun invoke_frequentWordsRankedHigher() {
        val result = useCase(
            "I love coffee and coffee makes mornings great",
            "Coffee is my favorite morning drink",
            "Nothing beats morning coffee ritual",
        )
        val keywords = result.split(",")
        // "coffee" appears frequently and should be first
        assertEquals("coffee", keywords.first())
    }
}
