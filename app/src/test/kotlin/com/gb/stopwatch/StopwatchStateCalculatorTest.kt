package com.gb.stopwatch

import com.gb.stopwatch.domain.model.StopwatchState
import com.gb.stopwatch.domain.repository.ElapsedTimeCalculator
import com.gb.stopwatch.domain.repository.StopwatchStateCalculator
import com.gb.stopwatch.domain.utils.TimestampProvider
import org.junit.Assert.*
import org.junit.Test

class StopwatchStateCalculatorTest {
    private val timestampProvider = object : TimestampProvider {
        override fun getMilliseconds(): Long {
            return System.currentTimeMillis()
        }
    }

    private val calculator = StopwatchStateCalculator(
        timestampProvider,
        ElapsedTimeCalculator(timestampProvider)
    )

    @Test
    fun calculator_CorrectResult() {
        assertEquals(
            StopwatchState.Paused(1000),
            calculator.calculatePausedState(StopwatchState.Paused(1000))
        )
    }

    @Test
    fun calculator_WrongResult() {
        assertNotEquals(
            StopwatchState.Paused(1000),
            calculator.calculatePausedState(StopwatchState.Paused(2000))
        )
    }

    @Test
    fun calculator_IsNotNull() {
        assertNotNull(calculator)
    }

    @Test
    fun timestamp_IsCorrect() {
        assertEquals(System.currentTimeMillis(), timestampProvider.getMilliseconds())
    }

    @Test
    fun calculator_returnIsNotNull() {
        assertNotNull(calculator.calculatePausedState(StopwatchState.Paused(1000)))
    }
}