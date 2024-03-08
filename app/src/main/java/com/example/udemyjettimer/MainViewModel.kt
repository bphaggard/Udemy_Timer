package com.example.udemyjettimer

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel: ViewModel() {
    private val _currentTime: MutableStateFlow<Long> = MutableStateFlow(totalTime)
    val currentTime: StateFlow<Long>
        get() = _currentTime
    private val _isTimeRunning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isTimeRunning: StateFlow<Boolean>
        get() = _isTimeRunning

    private val timer: CountDownTimer =
        object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished : Long) {
                _currentTime.value = millisUntilFinished
            }

            override fun onFinish() {
                _currentTime.value = totalTime
                _isTimeRunning.value = false
            }
        }

    companion object {
        const val totalTime = 30 * 1000L
        const val interval = 1000L
    }

    fun startTimer() {
        if (isTimeRunning.value) {
            resetTimer()
        }
        timer.start()
        _isTimeRunning.value = true
    }

    fun restartTimer() {
        if (_isTimeRunning.value) {
            resetTimer()
        }
    }

    private fun resetTimer() {
        timer.cancel()
        _currentTime.value = totalTime
        _isTimeRunning.value = false
    }

    override fun onCleared() {
        super.onCleared()
        restartTimer()
    }
}