package com.gb.stopwatch.domain.repository

import com.gb.stopwatch.domain.utils.TimestampMillisecondsFormatter

class StopWatchStateHolderFactory(
    private val stopwatchStateCalculator: StopwatchStateCalculator,
    private val elapsedTimeCalculator: ElapsedTimeCalculator,
    private val timestampMillisecondsFormatter: TimestampMillisecondsFormatter
) {
    fun create(): StopwatchStateHolder {
        return StopwatchStateHolder(
            stopwatchStateCalculator,
            elapsedTimeCalculator,
            timestampMillisecondsFormatter
        )
    }
}