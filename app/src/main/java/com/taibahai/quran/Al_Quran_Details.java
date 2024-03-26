package com.taibahai.quran;


import static com.network.utils.AppClass.sharedPref;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ScrollView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.taibahai.R;
import com.taibahai.activities.ShareActivity;
import com.taibahai.audioPlayer.AudioPlayer;
import com.taibahai.databinding.ActivityAlQuranDetailsBinding;
import com.taibahai.notifications.MediaNotificationManager;
import com.taibahai.utils.Constants;
import com.taibahai.utils.CustomScrollView;
import com.tonyodev.fetch2.Status;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Al_Quran_Details extends AppCompatActivity {

    private static final int STANDARD_SPEED = 50;
    private ArrayList<SurahModel> mData;
    private SurahAdapter surahAdapter;
    ActivityAlQuranDetailsBinding binding;
    String name;
    ObjectAnimator objectAnimator;
    private int totalVerse, counter = 0, scroll = 0, speed = 3;
    private boolean isFling = false, isPlaySuffle = false, isRepeat = false, isUpdate = false;
    private static final String TAG = "Al_Quran_Details";
    int currentIndex = 0;
    String currentFile = "", surahName = "", surahId = "";
    SurahListModel model;
    List<SurahListModel> mPlayerList;
    //    RoomDatabaseRepository dbRepository;
    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlQuranDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        activity = this;
//        initDB();
        initClick();
        initAdapter();
        loadJson();
        initScroll();
        initAudioPlay();
    }

//    private void initDB() {
//        dbRepository = new RoomDatabaseRepository(context);
//    }

    @SuppressLint("ClickableViewAccessibility")
    private void initScroll() {
//        binding.scrollView.isFocusableInTouchMode();
        binding.recyclerView.setOnTouchListener((View view, MotionEvent event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // The user just touched the screen
                    if (binding.playLayout.play.isSelected())
                        startScroll();
                    break;
                case MotionEvent.ACTION_UP:
                    // The touch just ended
                    if (!isFling) {
                        if (binding.playLayout.play.isSelected())
                            startScroll();
                    } else {
                        isFling = false;
                    }
                    break;
            }

            return false;
        });

        binding.scrollView.setOnFlingListener(new CustomScrollView.OnFlingListener() {
            @Override
            public void onFlingStarted() {
                isFling = true;
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
            }

            @Override
            public void onFlingStopped() {
                isFling = false;
                if (binding.playLayout.play.isSelected())
                    startScroll();
            }
        });

        binding.playLayout.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                AudioPlayer.Companion.getInstance().removeCallbacks();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                AudioPlayer.Companion.getInstance().seek(seekBar.getProgress());
            }
        });

        binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            scroll = binding.scrollView.getScrollY();
            View view = binding.scrollView.getChildAt(0);
            int diff = (view.getBottom() - (binding.scrollView.getHeight() + binding.scrollView.getScrollY()));
//                if (diff == 0) {
//                    binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
//                    if (sharedPref.getBoolean("switch_loop_script") && binding.stop.isSelected()) {
//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                binding.stop.setSelected(true);
//                                speed = sharedPref.getInt("text_speed", 1);
//                                startScroll();
//                            }
//                        }, 1000);
//                    } else {
//                        fadeOutAnimation(binding.toggleLayout);
//                    }
//                    binding.stop.setSelected(false);
//                    objectAnimator.cancel();
//                }
        });

    }

    private void initClick() {
        binding.back.setOnClickListener(v -> onBackPressed());

//        binding.settings.setOnClickListener(v -> startActivityForResult(new Intent(context, ReaderSettingsActivity.class)
//                , Constants.ACTIVITY_RESULT_CODE));

        binding.playLayout.play.setOnClickListener(v -> {
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile);
            }
        });


//        binding.playLayout.star.setOnClickListener(v -> {
//            isUpdate = true;
//            if (!model.isFav()) {
//                dbRepository.addToFavSurah(new FavModel(Long.parseLong(model.getId())));
//                v.setSelected(true);
//                model.setFav(true);
//                Log.d("response", "park id inserted: " + model.getId());
//            } else {
//                dbRepository.deleteFromFavSurah(Integer.parseInt(model.getId()));
//                v.setSelected(false);
//                model.setFav(false);
//                Log.d("response", "park id deleted: " + model.getId());
//            }
//
//        });

    }

    private void initAdapter() {
        mPlayerList = new ArrayList<>();
        mData = new ArrayList<>();
        surahAdapter = new SurahAdapter(context);
        binding.recyclerView.setAdapter(surahAdapter);
        binding.recyclerView.setNestedScrollingEnabled(false);
        surahAdapter.setOnItemClickListner((view, homeModel) -> {
            Intent intent1 = new Intent(context, ShareActivity.class);
            intent1.putExtra("surah_name", name);
            intent1.putExtra("share_type", "quran_ayat");
            intent1.putExtra("arabic_text", homeModel.getArabicText());
            intent1.putExtra("english_text", homeModel.getEnglishText());
            intent1.putExtra("ayat_number", homeModel.getPosition());
            intent1.putExtra("surah_number", surahId);
            startActivity(intent1);
        });
    }

    private void loadJson() {
        mData.clear();
        Intent intent = getIntent();
        surahId = intent.getStringExtra("ayat_id");
        String verse = intent.getStringExtra("ayat_verse");
        totalVerse = Integer.parseInt(verse);
        name = intent.getStringExtra("ayat_name");
        String type = intent.getStringExtra("ayat_type");
        Bundle args = intent.getBundleExtra(StringUtils.BUNDLE);
        assert args != null;
        mPlayerList = (List<SurahListModel>) args.getSerializable(StringUtils.ARRAY);
        updateCurrentIndex();

        if (mPlayerList.size() == 1) {
//            binding.playLayout.forward.setAlpha(.4f);
//            binding.playLayout.backward.setAlpha(.4f);
//            binding.playLayout.shuffle.setAlpha(.4f);
//            binding.playLayout.forward.setEnabled(false);
//            binding.playLayout.backward.setEnabled(false);
//            binding.playLayout.shuffle.setEnabled(false);
        }


        binding.detailsAyatName.setText(name);
        binding.detailsVerseNumber.setText(verse);
        try {
            if (type.equals(StringUtils.MAKKI)) {
                binding.surahType.setText(getString(R.string.makki));
            }
            if (type.equals(StringUtils.MADNI)) {
                binding.surahType.setText(getString(R.string.madni));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateUi();

    }

    private void showAyatList() {
        class loadAyats extends AsyncTask<Void, Void, List<SurahModel>> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mData.clear();
                surahAdapter.updateList(mData);
                binding.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<SurahModel> doInBackground(Void... voids) {
                try {
                    JSONArray jsonArr = new JSONArray(loadQuranJson(context, surahId));
//                    Log.d("response jsonArr: ", jsonArr.toString());
                    for (int i = 0; i < jsonArr.length(); i++) {
                        SurahModel surahModel = new SurahModel();
                        if (surahId.equals(jsonArr.getJSONObject(i).getString("surah_number"))) {
                            surahModel.setPosition(jsonArr.getJSONObject(i).getString("verse_number"));
                            surahModel.setArabicText(jsonArr.getJSONObject(i).getString("text"));
                            surahModel.setEnglishText(jsonArr.getJSONObject(i).getString("translation_en"));
                            surahModel.setEnglish_translation(jsonArr.getJSONObject(i).getString("transliteration_en"));
                            mData.add(surahModel);
                            counter++;
                            if (counter == totalVerse) {
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mData;
            }

            @Override
            protected void onPostExecute(List<SurahModel> tasks) {
                super.onPostExecute(tasks);
                counter = 0;
                surahAdapter.updateList(tasks);
                binding.progressBar.setVisibility(View.GONE);
                binding.playView.setVisibility(View.VISIBLE);
                playSurah();
                new Handler().postDelayed(Al_Quran_Details.this::startScroll, 1000);
            }
        }
        new loadAyats().execute();
    }

    private void startPlaying(String audio_url) {
        AudioPlayer.Companion.getInstance().setData(audio_url);
        AudioPlayer.Companion.getInstance().playOrPause();
        new Handler().postDelayed(() -> MediaNotificationManager.showMediaNotification(surahName), 200);
    }

    private void stopPlaying() {
        AudioPlayer.Companion.getInstance().stop();
    }

    private void initAudioPlay() {
        AudioPlayer.Companion.getInstance().OnItemClickListener(new AudioPlayer.OnViewClickListener() {

            @Override
            public void onPlayStarted(int duration) {
                Log.d(TAG, "onPlayStarted: ");
                String total_duration = getTimeString(duration);
                binding.playLayout.totalTime.setText(total_duration);
                binding.playLayout.play.setSelected(true);
                if (objectAnimator != null) {
                    if (!objectAnimator.isRunning()) {
                        startScroll();
                    }
                }
            }

            @Override
            public void updateDuration(int duration, int currentPosition) {
                String current_duration = getTimeString(currentPosition);
                binding.playLayout.currentTime.setText(current_duration);
                binding.playLayout.seekbar.setProgress(duration);
            }

            @Override
            public void onPause() {
                binding.playLayout.play.setSelected(false);
                stopScroll();
            }

            @Override
            public void onCompleted(MediaPlayer mp1) {
                Log.d(TAG, "onCompleted: isRepeat " + isRepeat + " isShuffle " + isPlaySuffle);
                binding.playLayout.play.setSelected(false);
                binding.playLayout.seekbar.setProgress(0);
                binding.playLayout.currentTime.setText("0:00");
                stopScroll();
                if (isRepeat) {
                    binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
                    new Handler().postDelayed(() -> binding.playLayout.play.callOnClick(), 1000);
                } else {
                    if (isPlaySuffle) {
//                        binding.playLayout.forward.callOnClick();
                    }
                }
            }
        });
    }

    private void stopScroll() {
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
    }

    private void playNext() {
        currentIndex++;
        if (currentIndex > mPlayerList.size() - 1) {
            currentIndex = 0;
        }
        updateUi();
    }

    private void playBack() {
        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = mPlayerList.size() - 1;
        }
        updateUi();
    }

    private void updateUi() {
        stopScroll();
        binding.scrollView.fullScroll(ScrollView.FOCUS_UP);
        model = mPlayerList.get(currentIndex);
        if (isPlaySuffle) {
            if (model.getId().equals(surahId)) {
                playNext();
                return;
            }
        }
        if (model.getDownload() != null) {
            if (model.getDownload().getStatus() == Status.COMPLETED) {
                String child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.getAudio());
                if (isFileExists(child, context)) {
                    surahId = model.getId();
                    totalVerse = Integer.parseInt(model.getTotalVerses());
                    showAyatList();
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void playSurah() {
        currentFile = model.getDownload().getFile();
        surahName = model.getTransliterationEn();
        binding.playLayout.surahName.setText(surahName);
        binding.detailsAyatName.setText(surahName);
        binding.detailsVerseNumber.setText(totalVerse + "");
        binding.playLayout.star.setSelected(model.isFav());
        try {
            if (model.getRevelationType().equals(StringUtils.MAKKI)) {
                binding.surahType.setText(getString(R.string.makki));
            }
            if (model.getRevelationType().equals(StringUtils.MADNI)) {
                binding.surahType.setText(getString(R.string.madni));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        startPlaying(currentFile);
    }

    public void updateCurrentIndex() {
        for (int i = 0; i < mPlayerList.size(); i++) {
            SurahListModel model = mPlayerList.get(i);
            if (!surahId.isEmpty()) {
                if (surahId.equals(model.getId())) {
                    currentIndex = i;
                    break;
                }
            }
        }
    }

    public void startScroll() {
        int scrollSpeed = sharedPref.getInt(StringUtils.SCROLL_SPEED);
        if (scrollSpeed != 0) {
            speed = scrollSpeed;
        }
        objectAnimator = ObjectAnimator.ofInt(binding.scrollView, "scrollY", binding.scrollView.getChildAt(0).getHeight() + scroll - binding.scrollView.getHeight());
        int duration = binding.scrollView.getChildAt(0).getHeight() / speed;
        // int total_duration=duration * Constants.getHeight(ScriptActivity.this);
        int total_duration = duration * STANDARD_SPEED;
        Log.d("response", "startScroll: " + total_duration / 1000 + " sec");
//        Log.d("response", "helf height: "+Constants.getHeight(ScriptActivity.this));
        objectAnimator.setDuration(total_duration);
        objectAnimator.setAutoCancel(true);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            surahAdapter.notifyDataSetChanged();
            if (binding.playLayout.play.isSelected())
                startScroll();
        }
    }

    @Override
    public void onBackPressed() {
        if (isUpdate)
            finishWithUpdate(activity, model);
        else
            super.onBackPressed();
    }


    public static String loadQuranJson(Context context, String id) {
        String json = "";
        try {
            InputStream is = context.getResources().openRawResource(getFileName(id));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private static int getFileName(String id) {
        int surah_id = Integer.parseInt(id);
        if (surah_id >= 1 && surah_id <= 2)
            return R.raw.quran_part_0;
        else if (surah_id >= 3 && surah_id <= 4)
            return R.raw.quran_part_1;
        else if (surah_id >= 5 && surah_id <= 8)
            return R.raw.quran_part_2;
        else if (surah_id >= 9 && surah_id <= 16)
            return R.raw.quran_part_3;
        else if (surah_id >= 17 && surah_id <= 24)
            return R.raw.quran_part_4;
        else if (surah_id >= 25 && surah_id <= 32)
            return R.raw.quran_part_5;
        else if (surah_id >= 33 && surah_id <= 40)
            return R.raw.quran_part_6;
        else if (surah_id >= 41 && surah_id <= 52)
            return R.raw.quran_part_7;
        else if (surah_id >= 53 && surah_id <= 64)
            return R.raw.quran_part_8;
        else if (surah_id >= 65 && surah_id <= 80)
            return R.raw.quran_part_9;
        else if (surah_id >= 81 && surah_id <= 114)
            return R.raw.quran_part_10;
        else
            return -1;
    }

    public static String getTimeString(long duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) - (minutes * 60));
        return minutes + ":" + String.format("%02d", seconds);
    }


    public static boolean isFileExists(String childPath, Context context) {
        File yourFile = new File(getAudioOutputDirectory(context), childPath);
        return yourFile.exists();
    }

    public static File getAudioOutputDirectory(Context context) {
        File mediaStorageDir = new File(context.getFilesDir() + "/" +
                context.getString(R.string.app_name) + "/Audios");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir;
    }

    protected void finishWithUpdate(Activity activity, Object val) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(StringUtils.OBJECT, (Serializable) val);
        setResult(Activity.RESULT_OK, returnIntent);
        activity.finish();
    }
}
