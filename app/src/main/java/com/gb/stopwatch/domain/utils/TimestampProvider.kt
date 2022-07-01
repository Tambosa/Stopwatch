package com.gb.stopwatch.domain.utils

interface TimestampProvider {
    fun getMilliseconds(): Long
}