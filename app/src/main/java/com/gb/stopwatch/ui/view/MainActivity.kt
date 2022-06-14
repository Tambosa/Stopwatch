package com.gb.stopwatch.ui.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.gb.stopwatch.R
import com.gb.stopwatch.domain.model.Stopwatch
import com.gb.stopwatch.domain.repository.ElapsedTimeCalculator
import com.gb.stopwatch.domain.repository.StopWatchStateHolderFactory
import com.gb.stopwatch.domain.repository.StopwatchStateCalculator
import com.gb.stopwatch.domain.utils.TimestampMillisecondsFormatter
import com.gb.stopwatch.domain.utils.TimestampProvider
import com.gb.stopwatch.ui.viewmodel.StopwatchListOrchestrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val stopwatchOne = Stopwatch(1, "First stopwatch")
    private val stopwatchTwo = Stopwatch(2, "Second stopwatch")

    private val timestampProvider = object : TimestampProvider {
        override fun getMilliseconds(): Long {
            return System.currentTimeMillis()
        }
    }
    private val stopwatchListOrchestrator = StopwatchListOrchestrator(
        StopWatchStateHolderFactory(
            StopwatchStateCalculator(
                timestampProvider,
                ElapsedTimeCalculator(timestampProvider)
            ),
            ElapsedTimeCalculator(timestampProvider),
            TimestampMillisecondsFormatter()
        ),
        CoroutineScope(
            Dispatchers.Main
                    + SupervisorJob()
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView1 = findViewById<TextView>(R.id.text_time)
        val textView2 = findViewById<TextView>(R.id.text_time2)
        CoroutineScope(
            Dispatchers.Main
                    + SupervisorJob()
        ).launch {
            stopwatchListOrchestrator.ticker.collect {
                textView1.text = it.getOrDefault(stopwatchOne, TimestampMillisecondsFormatter.DEFAULT_TIME)
                textView2.text = it.getOrDefault(stopwatchTwo, TimestampMillisecondsFormatter.DEFAULT_TIME)
            }
        }

        findViewById<Button>(R.id.button_start).setOnClickListener {
            stopwatchListOrchestrator.start(stopwatchOne)
        }
        findViewById<Button>(R.id.button_pause).setOnClickListener {
            stopwatchListOrchestrator.pause(stopwatchOne)
        }
        findViewById<Button>(R.id.button_stop).setOnClickListener {
            stopwatchListOrchestrator.stop(stopwatchOne)
        }

        findViewById<Button>(R.id.button_start2).setOnClickListener {
            stopwatchListOrchestrator.start(stopwatchTwo)
        }
        findViewById<Button>(R.id.button_pause2).setOnClickListener {
            stopwatchListOrchestrator.pause(stopwatchTwo)
        }
        findViewById<Button>(R.id.button_stop2).setOnClickListener {
            stopwatchListOrchestrator.stop(stopwatchTwo)
        }
    }
}

