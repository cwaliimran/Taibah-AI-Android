package com.taibahai.audio_player

import android.media.MediaRecorder
import android.util.Log
import com.google.zxing.common.StringUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

object AudioRecorder {
  /*  private const val TAG = "response"
    fun startRecording(recorder: MediaRecorder): String {
        resetRecorder(recorder)
        //getting file path
        val child = SimpleDateFormat("yyyy_MMM_dd_SSS", Locale.US)
            .format(System.currentTimeMillis()) + "_taibah.mp4"
        val file: File = File(getAudioOutputDirectory(), child)
        val audio_path = file.absolutePath
        //starting recorder
        configAudioRecorder(recorder)
        recorder.setOutputFile(audio_path)
        try {
            recorder.prepare()
        } catch (e: IOException) {
            Log.e(TAG, "prepare() failed")
        }
        recorder.start()
        return audio_path
    }

    fun configAudioRecorder(recorder: MediaRecorder) {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        recorder.setAudioSamplingRate(Constants.SAMPLING_RATE)
        recorder.setAudioChannels(Constants.RECORD_AUDIO_STEREO)
        recorder.setAudioEncodingBitRate(Constants.RECORD_ENCODING_BITRATE_128000)
        recorder.setMaxDuration(Constants.MAX_VOICE_DURATION)
    }

    fun resetRecorder(recorder: MediaRecorder?) {
        recorder?.reset()
    }

    fun stopRecorder(recorder: MediaRecorder?) {
        var recorder = recorder
        recorder!!.stop() // stop recording
        recorder.reset() // set state to idle
        recorder.release() // release resources back to the system
        recorder = null*/
    } //	implements RecorderContract.Recorder
    //	private MediaRecorder recorder = null;
    //	private File recordFile = null;
    //
    //	private boolean isPrepared = false;
    //	private boolean isRecording = false;
    //	private boolean isPaused = false;
    //	private Timer timerProgress;
    //	private long progress = 0;
    //	private static final String TAG = "response";
    //
    //	private RecorderContract.RecorderCallback recorderCallback;
    //
    //	private static class RecorderSingletonHolder {
    //		private static AudioRecorder singleton = new AudioRecorder();
    //
    //		public static AudioRecorder getSingleton() {
    //			return RecorderSingletonHolder.singleton;
    //		}
    //	}
    //
    //	public static AudioRecorder getInstance() {
    //		return RecorderSingletonHolder.getSingleton();
    //	}
    //
    //	private AudioRecorder() { }
    //
    //	@Override
    //	public void setRecorderCallback(RecorderContract.RecorderCallback callback) {
    //		this.recorderCallback = callback;
    //	}
    //
    //	@Override
    //	public void prepare(String outputFile) {
    //		recordFile = new File(outputFile);
    //		if (recordFile.exists() && recordFile.isFile()) {
    //			recorder = new MediaRecorder();
    //			recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
    //			recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
    //			recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    //			recorder.setAudioSamplingRate(Constants.SAMPLING_RATE);
    //			recorder.setAudioChannels(Constants.RECORD_AUDIO_STEREO);
    //			recorder.setAudioEncodingBitRate(Constants.RECORD_ENCODING_BITRATE_128000);
    //			recorder.setMaxDuration(-1);
    //			recorder.setOutputFile(recordFile.getAbsolutePath());
    //			try {
    //				recorder.prepare();
    //				isPrepared = true;
    //				if (recorderCallback != null) {
    //					recorderCallback.onPrepareRecord();
    //				}
    //			} catch (IOException | IllegalStateException e) {
    //				e.printStackTrace();
    //				Log.d("response", "prepare: "+e);
    ////				Timber.e(e, "prepare() failed");
    ////
    ////				if (recorderCallback != null) {
    ////					recorderCallback.onError(new RecorderInitException());
    ////				}
    //			}
    //		} else {
    //			Log.d("response", "error: ");
    //		}
    //	}
    //
    //	@Override
    //	public void startRecording() {
    //		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isPaused) {
    //			try {
    //				recorder.resume();
    ////				startRecordingTimer();
    //				if (recorderCallback != null) {
    //					recorderCallback.onStartRecord(recordFile);
    //				}
    //				isPaused = false;
    //			} catch (IllegalStateException e) {
    //				e.printStackTrace();
    //				Log.d(TAG, "startRecording: "+e);
    ////				if (recorderCallback != null) {
    ////					recorderCallback.onError(new RecorderInitException());
    ////				}
    //			}
    //		} else {
    //			if (isPrepared) {
    //				try {
    //					recorder.start();
    //					isRecording = true;
    ////					startRecordingTimer();
    //					if (recorderCallback != null) {
    //						recorderCallback.onStartRecord(recordFile);
    //					}
    //				} catch (RuntimeException e) {
    //					e.printStackTrace();
    ////					Timber.e(e, "startRecording() failed");
    ////					if (recorderCallback != null) {
    ////						recorderCallback.onError(new RecorderInitException());
    ////					}
    //				}
    //			} else {
    ////				Timber.e("Recorder is not prepared!!!");
    //				Log.d(TAG, "startRecording:error ");
    //			}
    //			isPaused = false;
    //		}
    //	}
    //
    //	@Override
    //	public void pauseRecording() {
    //		if (isRecording) {
    //			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    //				try {
    //					recorder.pause();
    ////					pauseRecordingTimer();
    //					if (recorderCallback != null) {
    //						recorderCallback.onPauseRecord();
    //					}
    //					isPaused = true;
    //				} catch (IllegalStateException e) {
    ////					Timber.e(e, "pauseRecording() failed");
    //					e.printStackTrace();
    //					Log.d(TAG, "pauseRecording: "+e);
    ////					if (recorderCallback != null) {
    ////						//TODO: Fix exception
    ////						recorderCallback.onError(new RecorderInitException());
    ////					}
    //				}
    //			} else {
    //				stopRecording();
    //			}
    //		}
    //	}
    //
    //	@Override
    //	public void stopRecording() {
    //		if (isRecording) {
    ////			stopRecordingTimer();
    //			try {
    //				recorder.stop();
    //			} catch (RuntimeException e) {
    //				e.printStackTrace();
    ////				Timber.e(e, "stopRecording() problems");
    //			}
    //				recorder.release();
    //			if (recorderCallback != null) {
    //				recorderCallback.onStopRecord(recordFile);
    //			}
    //			recordFile = null;
    //			isPrepared = false;
    //			isRecording = false;
    //			isPaused = false;
    //			recorder = null;
    //		} else {
    //			Log.d(TAG, "stopRecording: Recording has already stopped or hasn't started");
    ////			Timber.e("Recording has already stopped or hasn't started");
    //		}
    //	}
    //
    ////	private void startRecordingTimer() {
    ////		timerProgress = new Timer();
    ////		timerProgress.schedule(new TimerTask() {
    ////			@Override
    ////			public void run() {
    ////				if (recorderCallback != null && recorder != null) {
    ////					try {
    ////						recorderCallback.onRecordProgress(progress, recorder.getMaxAmplitude());
    ////					} catch (IllegalStateException e) {
    ////						Timber.e(e);
    ////					}
    ////					progress += VISUALIZATION_INTERVAL;
    ////				}
    ////			}
    ////		}, 0, VISUALIZATION_INTERVAL);
    ////	}
    //
    ////	private void stopRecordingTimer() {
    ////		timerProgress.cancel();
    ////		timerProgress.purge();
    ////		progress = 0;
    ////	}
    //
    ////	private void pauseRecordingTimer() {
    ////		timerProgress.cancel();
    ////		timerProgress.purge();
    ////	}
    //
    //	@Override
    //	public boolean isRecording() {
    //		return isRecording;
    //	}
    //
    //	@Override
    //	public boolean isPaused() {
    //		return isPaused;
    //	}