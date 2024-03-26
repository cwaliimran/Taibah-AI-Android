package com.taibahai.quran;

import static com.network.utils.AppClass.sharedPref;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.network.network.NetworkUtils;
import com.taibahai.R;
import com.taibahai.activities.ChapterDetailActivity;
import com.taibahai.audioPlayer.AudioPlayer;
import com.taibahai.databinding.FragmentAlQuranBinding;
import com.taibahai.notifications.MediaNotificationManager;
import com.taibahai.utils.Constants;
import com.taibahai.utils.JsonUtils;
import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlQuranFragment extends AppCompatActivity
{
    String BASE_URL_1 = "https://taibahislamic.com/admin/";
    private static final String TAG = "AlQuranFragment";
    boolean isFavourite = false, isDownload = true, isPlaySuffle = false, isPlaySimple = false;
    JSONArray jsonArr = null;
    private List<SurahListModel> mData;
    private List<SurahListModel> mFilteredData;
    private List<SurahListModel> mPlayerList;
    private AllSurahListAdapter allSurahListAdapter;
    private List<FavModel> favModels;
    FetchConfiguration fetchConfiguration;
    Fetch fetch;
    Request request;
    FragmentAlQuranBinding binding;
    int currentIndex = 0;
    String currentFile = "", surahName = "", surahId = "";
    SurahListModel model;

    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAlQuranBinding.inflate(getLayoutInflater());
        context = this;
        initViews();
        initDownloader();
        initAdapter();
        initClicks();
        initData();
        setContentView(binding.getRoot());
    }


    private void initViews() {
        binding.recyclerView.setNestedScrollingEnabled(false);
        mData = new ArrayList<>();
        mFilteredData = new ArrayList<>();
        favModels = new ArrayList<>();
        mPlayerList = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        initAudioPlay();
        if (!mData.isEmpty()) {
            getDownloads();
        }
    }

    private void initDownloader() {
        fetchConfiguration = new FetchConfiguration.Builder(context).build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        String requestUrl = sharedPref.getString(StringUtils.PREV_SURAH_URL, "");
        String requestPath = sharedPref.getString(StringUtils.PREV_SURAH_FILEPATH, "");

        if (!requestUrl.isEmpty() || !requestPath.isEmpty()) {
            request = new Request(requestUrl, requestPath);
            request.setGroupId(StringUtils.SURAH_GROUP_ID);
            fetch.getDownload(request.getId(), result -> {
                if (result != null) {
                    if (result.getStatus() != Status.COMPLETED) {
                        fetch.enqueue(request, updatedRequest -> {
                            //Request was successfully enqueued for download.
                        }, error -> {
                            Log.d(TAG, "call: " + result.getError());
                            //An error occurred enqueuing the request.
                        });
                    }
                }
            }).addListener(fetchListener);
        }
    }

    private void getDownloads() {
        fetch.getDownloadsInGroup(StringUtils.SURAH_GROUP_ID, downloads -> {
            final ArrayList<Download> list = new ArrayList<>(downloads);
            Log.d(TAG, "call: " + new Gson().toJson(list));
//                Collections.sort(list, (first, second) -> Long.compare(first.getCreated(), second.getCreated()));
            for (Download download : list) {
                allSurahListAdapter.updateView(download);
            }
        }).addListener(fetchListener);
    }

    private void initAdapter() {
        allSurahListAdapter = new AllSurahListAdapter(context);
        binding.recyclerView.setAdapter(allSurahListAdapter);
        allSurahListAdapter.setOnItemClickListener(new AllSurahListAdapter.OnPlayListener() {
            @Override
            public void onDownload(final SurahListModel model) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    TedPermission.create().setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    if (NetworkUtils.INSTANCE.isInternetAvailable()) {
                                        if (isDownload)
                                            downloadAudio(model.getAudio());
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.turn_on_internet), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                }
                            }).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS).check();
                } else {
                    TedPermission.create().setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    if (NetworkUtils.INSTANCE.isInternetAvailable()) {
                                        if (isDownload)
                                            downloadAudio(model.getAudio());
                                    } else {
                                        Toast.makeText(context, context.getString(R.string.turn_on_internet), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onPermissionDenied(List<String> deniedPermissions) {
                                }
                            }).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).check();

                }
            }

            @Override
            public void onDelete(SurahListModel model) {
                fetch.remove(model.getDownloadId(context));
            }

            @Override
            public void onPause(SurahListModel model) {
                sharedPref.removeKey(StringUtils.PREV_SURAH_FILEPATH);
                sharedPref.removeKey(StringUtils.PREV_SURAH_URL);
                fetch.pause(model.getDownloadId(context));
            }

            @Override
            public void onResume(SurahListModel model) {
                fetch.resume(model.getDownloadId(context));
            }

            @Override
            public void onRetryDownload(SurahListModel model) {
                fetch.retry(model.getDownloadId(context));
            }

            @Override
            public void onPlayClick(SurahListModel result) {
                model = result;
                isPlaySimple = false;
                isPlaySuffle = false;
                AudioPlayer.Companion.getInstance().release();
                updateUi();
                getPlayerList();
                gotoDetails();
            }
        });
    }

    private void updateUi() {
        surahId = model.getId();
        currentFile = model.getDownload().getFile();
        surahName = model.getTransliterationEn();
        binding.playLayout.surahName.setText(surahName);
        allSurahListAdapter.updateView(surahId);
    }

    private void gotoDetails() {
        Intent intent = new Intent(context, Al_Quran_Details.class);
        intent.putExtra("ayat_id", model.getId());
        intent.putExtra("ayat_name", model.getTransliterationEn());
        intent.putExtra("ayat_verse", model.getTotalVerses());
        intent.putExtra("ayat_type", model.getRevelationType());
        if (!mPlayerList.isEmpty()) {
            Bundle args = new Bundle();
            args.putSerializable(StringUtils.ARRAY, (Serializable) mPlayerList);
            intent.putExtra(StringUtils.BUNDLE, args);
        }
        startActivityForResult(intent, Constants.ACTIVITY_RESULT_CODE);
    }

    public void downloadAudio(String s) {
        String child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(s);
        File file = new File(getAudioOutputDirectory(context), child);
        String audio_path = file.getAbsolutePath();

        request = new Request(BASE_URL_1 + s, audio_path);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.setGroupId(StringUtils.SURAH_GROUP_ID);
//        request.addHeader(CLIENT_KEY, StringUtils.CLIENT_KEY_HEADER);

        sharedPref.storeString(StringUtils.PREV_SURAH_URL, request.getUrl());
        sharedPref.storeString(StringUtils.PREV_SURAH_FILEPATH, request.getFile());

        fetch.enqueue(request, updatedRequest -> {
            //Request was successfully enqueued for download.
        }, error -> {
            //An error occurred enqueuing the request.
        });

    }

    private void initData() {
        try {
            mFilteredData.clear();
            jsonArr = new JSONArray(JsonUtils.readRawResource(context, R.raw.allsurahlist));
            Gson gson = new Gson();
            Type type = new TypeToken<List<SurahListModel>>() {
            }.getType();
            mFilteredData = gson.fromJson(jsonArr.toString(), type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadJson();
//        new getFavSurah().execute();
    }

    private void initClicks() {

        binding.showFav.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isFavourite = isChecked;
//            new getFavSurah().execute();
        });

        binding.playLayout.play.setOnClickListener(v -> {
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile);
                allSurahListAdapter.updateView(surahId);
            }
        });

        binding.playLayout.fastForward.setOnClickListener(v -> {
            if (!mPlayerList.isEmpty())
                playNext();
        });

        binding.playBtn.setOnClickListener(v -> {
            getPlayerList();
            if (!mPlayerList.isEmpty()) {
                AudioPlayer.Companion.getInstance().release();
                isPlaySimple = true;
                isPlaySuffle = false;
                currentIndex = -1;
                playNext();
            } else {
                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT).show();
            }
        });

        binding.shuffleBtn.setOnClickListener(v -> {
            getPlayerList();
            if (!mPlayerList.isEmpty()) {
                AudioPlayer.Companion.getInstance().release();
                isPlaySimple = false;
                isPlaySuffle = true;
                currentIndex = -1;
                playNext();
            } else {
                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getPlayerList() {
        mPlayerList.clear();
        for (SurahListModel model : mData) {
            if (model.getDownload() != null) {
                if (model.getDownload().getStatus() == Status.COMPLETED) {
                    String child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.getAudio());
                    if (isFileExists(child, context)) {
                        mPlayerList.add(model);
                    }
                }
            }
        }
        if (isPlaySuffle) {
            Collections.shuffle(mPlayerList);
        }
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

    private void playNext() {
        currentIndex++;
        if (currentIndex > mPlayerList.size() - 1) {
            currentIndex = 0;
        }
        SurahListModel model = mPlayerList.get(currentIndex);
        if (model.getDownload() != null) {
            if (model.getDownload().getStatus() == Status.COMPLETED) {
                String child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.getAudio());
                if (isFileExists(child, context)) {
                    surahId = model.getId();
                    currentFile = model.getDownload().getFile();
                    surahName = model.getTransliterationEn();
                    binding.playLayout.surahName.setText(surahName);
                    startPlaying(currentFile);
                    allSurahListAdapter.updateView(model.getId());
                }
            }
        }
    }

    private void loadJson() {
        mData.clear();
        for (SurahListModel model : mFilteredData) {
            long id = Long.parseLong(model.getId());
            if (isFavSaved(id) && isFavourite && !(id == -1)) {
                model.setFav(true);
                mData.add(model);
            }

            if (!isFavourite) {
                model.setFav(isFavSaved(id));
                mData.add(model);
            }
        }

        allSurahListAdapter.updateData(mData);

        binding.progressBar.setVisibility(View.GONE);
        binding.buttonsLayout.setVisibility(View.VISIBLE);
        binding.playView.setVisibility(View.VISIBLE);
        binding.playLayout.imamFullName.setSelected(true);

        getDownloads();

        if (mData.size() == 0) {
            binding.textView.setVisibility(View.VISIBLE);
        } else {
            binding.textView.setVisibility(View.GONE);
        }
    }

    private boolean isFavSaved(long id) {
        boolean check = false;
        for (FavModel favModels : favModels) {
            long parkIdCurrent = favModels.getPosition();
//            Log.d("response", "isSaved: " + parkIdCurrent + "");
            if (parkIdCurrent == id) {
                check = true;
                break;
            }
        }
        return check;
    }


//    @Override
//    public void onChanged(Download download, @NotNull Reason reason) {
//        if (request.getId() == download.getId()) {
//            allSurahListAdapter.updateView(download);
//        }
//    }

//    class getFavSurah extends AsyncTask<Void, Void, List<FavModel>> {
//
//        @Override
//        protected List<FavModel> doInBackground(Void... voids) {
//            return DatabaseClient
//                    .getInstance(getActivity())
//                    .getAppDatabase().surahDao()
//                    .getAllfav();
//        }
//
//        @Override
//        protected void onPostExecute(List<FavModel> tasks) {
//            super.onPostExecute(tasks);
//            favModels.clear();
//            favModels.addAll(tasks);
//            new Handler().postDelayed(AlQuranFragment.this::loadJson, 200);
//        }
//    }

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
                Log.d(TAG, "onPlayStarted: " + binding.playLayout.play.isSelected());
                binding.playLayout.play.setSelected(true);
            }

            @Override
            public void updateDuration(int duration, int currentPosition) {
                if (!binding.playLayout.play.isSelected())
                    binding.playLayout.play.setSelected(true);
            }

            @Override
            public void onPause() {
                binding.playLayout.play.setSelected(false);
                allSurahListAdapter.updateView(StringUtils.NO_INDEX);
            }

            @Override
            public void onCompleted(MediaPlayer mp1) {
                binding.playLayout.play.setSelected(false);
                allSurahListAdapter.updateView(StringUtils.NO_INDEX);
                if (isPlaySimple || isPlaySuffle)
                    playNext();
            }
        });
    }

    FetchListener fetchListener = new AbstractFetchListener() {

        @Override
        public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
            super.onError(download, error, throwable);
            Log.d(TAG, "onError: " + error.getValue() + " " + error.getHttpResponse() + " " + error.getThrowable());
            Log.d(TAG, "onError: " + error.getThrowable() + " " + error.getHttpResponse() + " " + error.getThrowable());
            assert throwable != null;
            Toast.makeText(context, throwable.getMessage() + "", Toast.LENGTH_SHORT).show();
            allSurahListAdapter.updateView(download);
            isDownload = true;
        }


        @Override
        public void onAdded(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            allSurahListAdapter.updateView(download);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
            isDownload = true;
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
            if (isDownload)
                allSurahListAdapter.updateView(download);
            isDownload = false;
            Log.d(TAG, "onProgress: " + downloadedBytesPerSecond / 1024);
        }

        @Override
        public void onPaused(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
            isDownload = true;
        }

        @Override
        public void onResumed(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            isDownload = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                allSurahListAdapter.updateView(download);
                                if (download.getFile() != null && !download.getFile().isEmpty())
                                    deleteFile(download.getFile(), context);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                            }
                        }).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS).check();
            } else {
                TedPermission.create().setPermissionListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted() {
                                allSurahListAdapter.updateView(download);
                                if (download.getFile() != null && !download.getFile().isEmpty())
                                    deleteFile(download.getFile(), context);
                            }

                            @Override
                            public void onPermissionDenied(List<String> deniedPermissions) {
                            }
                        }).setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).check();

            }

        }

        @Override
        public void onDeleted(@NotNull Download download) {
            allSurahListAdapter.updateView(download);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (request != null) {
//            fetch.removeFetchObserversForDownload(request.getId(), this);
            fetch.removeListener(fetchListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fetch.close();
//        AudioPlayer.Companion.getInstance().release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            if (data.hasExtra(StringUtils.OBJECT)) {
                model = (SurahListModel) data.getSerializableExtra(StringUtils.OBJECT);
                updateUi();
                updateCurrentIndex();
            }
        }
    }

    public static File getAudioOutputDirectory(Context context) {
        File mediaStorageDir = new File(context.getFilesDir() + "/" +
                context.getString(R.string.app_name) + "/Audios");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir;
    }

    public static boolean isFileExists(String childPath, Context context) {
        File yourFile = new File(getAudioOutputDirectory(context), childPath);
        return yourFile.exists();
    }

    public static boolean deleteFile(String filePath, Context context) {
        File dir = context.getFilesDir();
        File file = new File(dir, filePath);
        return file.delete();
    }


}