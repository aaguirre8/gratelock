package com.gratelock.app.ui.screen.gratitudeform

import com.gratelock.app.data.local.entity.JournalEntry
import com.gratelock.app.data.repository.EntryRepository
import com.gratelock.app.data.repository.StatsRepository
import com.gratelock.app.domain.usecase.CalculateStreakUseCase
import com.gratelock.app.domain.usecase.ExtractKeywordsUseCase
import com.gratelock.app.domain.usecase.UnlockAppsUseCase
import com.gratelock.app.domain.usecase.ValidateTypedEntryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class GratitudeFormViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var validateTypedEntryUseCase: ValidateTypedEntryUseCase
    private lateinit var extractKeywordsUseCase: ExtractKeywordsUseCase
    private lateinit var calculateStreakUseCase: CalculateStreakUseCase
    private lateinit var unlockAppsUseCase: UnlockAppsUseCase
    private lateinit var entryRepository: EntryRepository
    private lateinit var statsRepository: StatsRepository
    private lateinit var viewModel: GratitudeFormViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Real use cases for validation and keywords (no external deps)
        validateTypedEntryUseCase = ValidateTypedEntryUseCase()
        extractKeywordsUseCase = ExtractKeywordsUseCase()
        // Mocked use cases for streak/unlock (have external deps)
        calculateStreakUseCase = mock()
        unlockAppsUseCase = mock()
        entryRepository = mock()
        statsRepository = mock()

        viewModel = GratitudeFormViewModel(
            validateTypedEntryUseCase = validateTypedEntryUseCase,
            extractKeywordsUseCase = extractKeywordsUseCase,
            calculateStreakUseCase = calculateStreakUseCase,
            unlockAppsUseCase = unlockAppsUseCase,
            entryRepository = entryRepository,
            statsRepository = statsRepository,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isReady() {
        assertTrue(viewModel.uiState.value is GratitudeFormUiState.Ready)
    }

    @Test
    fun fieldChanged_updatesFieldText() {
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(0, "hello"))

        val state = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertEquals("hello", state.field1)
    }

    @Test
    fun fieldChanged_updatesCorrectField() {
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(1, "second"))
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(2, "third"))

        val state = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertEquals("", state.field1)
        assertEquals("second", state.field2)
        assertEquals("third", state.field3)
    }

    @Test
    fun fieldChanged_clearsErrorForEditedField() {
        // First submit with invalid data to get errors
        viewModel.onEvent(GratitudeFormEvent.Submit)
        val stateWithErrors = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertTrue(stateWithErrors.fieldErrors.isNotEmpty())

        // Edit field 0
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(0, "new text"))
        val stateAfterEdit = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertTrue(0 !in stateAfterEdit.fieldErrors)
    }

    @Test
    fun submit_invalidFields_showsErrors() {
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(0, "hi"))
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(1, ""))
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(2, ""))

        viewModel.onEvent(GratitudeFormEvent.Submit)

        val state = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertTrue(state.fieldErrors.isNotEmpty())
    }

    @Test
    fun submit_validFields_savesEntryAndTransitionsToSuccess() = runTest {
        whenever(entryRepository.saveEntry(any())).thenReturn(1L)
        whenever(entryRepository.getEntryCount()).thenReturn(1)
        whenever(calculateStreakUseCase.invoke()).thenReturn(1)

        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(0, "I'm grateful for my morning coffee today"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(1, "The sunshine through the window was beautiful"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(2, "My friend called to check on me today"),
        )

        viewModel.onEvent(GratitudeFormEvent.Submit)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GratitudeFormUiState.Success)
    }

    @Test
    fun submit_validFields_callsAllUseCases() = runTest {
        whenever(entryRepository.saveEntry(any())).thenReturn(1L)
        whenever(entryRepository.getEntryCount()).thenReturn(5)
        whenever(calculateStreakUseCase.invoke()).thenReturn(3)

        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(0, "I'm grateful for my morning coffee today"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(1, "The sunshine through the window was beautiful"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(2, "My friend called to check on me today"),
        )

        viewModel.onEvent(GratitudeFormEvent.Submit)
        advanceUntilIdle()

        verify(entryRepository).saveEntry(any())
        verify(statsRepository).updateTotalEntries(5)
        verify(calculateStreakUseCase).invoke()
        verify(unlockAppsUseCase).invoke()
    }

    @Test
    fun submit_validFields_successStateHasCorrectStreak() = runTest {
        whenever(entryRepository.saveEntry(any())).thenReturn(1L)
        whenever(entryRepository.getEntryCount()).thenReturn(10)
        whenever(calculateStreakUseCase.invoke()).thenReturn(7)

        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(0, "I'm grateful for my morning coffee today"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(1, "The sunshine through the window was beautiful"),
        )
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(2, "My friend called to check on me today"),
        )

        viewModel.onEvent(GratitudeFormEvent.Submit)
        advanceUntilIdle()

        val state = viewModel.uiState.value as GratitudeFormUiState.Success
        assertEquals(7, state.newStreak)
    }

    @Test
    fun submit_duplicateFields_showsDuplicateError() {
        val text = "I'm grateful for my morning coffee today"
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(0, text))
        viewModel.onEvent(GratitudeFormEvent.FieldChanged(1, text))
        viewModel.onEvent(
            GratitudeFormEvent.FieldChanged(2, "My friend called to check on me today"),
        )

        viewModel.onEvent(GratitudeFormEvent.Submit)

        val state = viewModel.uiState.value as GratitudeFormUiState.Ready
        assertTrue(1 in state.fieldErrors)
    }
}
