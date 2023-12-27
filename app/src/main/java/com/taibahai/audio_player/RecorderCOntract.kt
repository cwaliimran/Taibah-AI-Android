package com.taibahai.audio_player

import java.io.File

interface RecorderContract {
    interface RecorderCallback {
        fun onPrepareRecord()
        fun onStartRecord(output: File?)
        fun onPauseRecord()
        fun onRecordProgress(mills: Long, amp: Int)
        fun onStopRecord(output: File?) //		void onError(AppException throwable);
    }

    interface Recorder {
        fun setRecorderCallback(callback: RecorderCallback?)
        fun prepare(outputFile: String?)
        fun startRecording()
        fun pauseRecording()
        fun stopRecording()
        val isRecording: Boolean
        val isPaused: Boolean
    }
}