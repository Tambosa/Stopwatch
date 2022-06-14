package com.gb.stopwatch.ui.viewmodel

import com.gb.stopwatch.domain.model.Stopwatch
import com.gb.stopwatch.domain.model.StopwatchState
import com.gb.stopwatch.domain.repository.StopWatchStateHolderFactory
import com.gb.stopwatch.domain.repository.StopwatchStateHolder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.ConcurrentHashMap

class StopwatchListOrchestrator(
    private val stopWatchStateHolderFactory: StopWatchStateHolderFactory,
    private val scope: CoroutineScope,
) {

    private var job: Job? = null
    private var stateHolders = ConcurrentHashMap<Stopwatch, StopwatchStateHolder>()
    private val mutableTicker = MutableStateFlow<Map<Stopwatch, String>>(mapOf())
    val ticker: StateFlow<Map<Stopwatch, String>> = mutableTicker

    fun start(stopwatch: Stopwatch) {
        if (job == null) startJob()
        stateHolders
            .getOrPut(stopwatch) { stopWatchStateHolderFactory.create() }
            .start()
    }

    private fun startJob() {
        job = scope.launch {
            while (isActive) {
                val newValues = stateHolders
                    .toSortedMap(compareBy { stopwatch -> stopwatch.id })
                    .map { (stopwatch, stateHolder) ->
                        stopwatch to stateHolder.getStringTimeRepresentation()
                    }
                    .toMap()
                mutableTicker.value = newValues
                delay(20)
            }
        }
    }

    fun pause(stopwatch: Stopwatch) {
        stateHolders[stopwatch]?.pause()
        if (stateHolders.values.all { stateHolder -> stateHolder.currentState is StopwatchState.Paused }) {
            stopJob()
        }
    }

    fun stop(stopwatch: Stopwatch) {
        stateHolders.remove(stopwatch)
        clearValue()
        if (stateHolders.isEmpty()) {
            stopJob()
        }
    }

    private fun stopJob() {
        scope.coroutineContext.cancelChildren()
        job = null
    }

    private fun clearValue() {
        val newValues = stateHolders
            .toSortedMap(compareBy { stopwatch -> stopwatch.id })
            .map { (stopwatch, stateHolder) ->
                stopwatch to stateHolder.getStringTimeRepresentation()
            }
            .toMap()
        mutableTicker.value = newValues
    }
}