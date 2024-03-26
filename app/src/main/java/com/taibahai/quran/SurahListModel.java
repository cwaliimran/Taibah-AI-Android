package com.taibahai.quran;


import android.content.Context;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.taibahai.R;
import com.tonyodev.fetch2.Download;

import java.io.File;
import java.io.Serializable;


public class SurahListModel implements Serializable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("transliteration_en")
    @Expose
    private String transliterationEn;
    @SerializedName("translation_en")
    @Expose
    private String translationEn;
    @SerializedName("total_verses")
    @Expose
    private String totalVerses;
    @SerializedName("revelation_type")
    @Expose
    private String revelationType;
    @SerializedName("audio")
    @Expose
    private String audio;

    private boolean fav;

    private int downloadId;

    Download download;

    public int getDownloadId(Context context) {
        String BASE_URL_1 = "https://taibahislamic.com/admin/";

        String url = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(audio);
        File yourFile = new File(getAudioOutputDirectory(context), url);
            return getUniqueId(BASE_URL_1 + audio, yourFile.getAbsolutePath());
    }

    private int getUniqueId(String s, String absolutePath) {
        return (s.hashCode() * 31) + absolutePath.hashCode();
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public Download getDownload() {
        return download;
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransliterationEn() {
        return transliterationEn;
    }

    public void setTransliterationEn(String transliterationEn) {
        this.transliterationEn = transliterationEn;
    }

    public String getTranslationEn() {
        return translationEn;
    }

    public void setTranslationEn(String translationEn) {
        this.translationEn = translationEn;
    }

    public String getTotalVerses() {
        return totalVerses;
    }

    public void setTotalVerses(String totalVerses) {
        this.totalVerses = totalVerses;
    }

    public String getRevelationType() {
        return revelationType;
    }

    public void setRevelationType(String revelationType) {
        this.revelationType = revelationType;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public static File getAudioOutputDirectory(Context context) {
        File mediaStorageDir = new File(context.getFilesDir() + "/" +
                context.getString(R.string.app_name) + "/Audios");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        return mediaStorageDir;
    }

}
