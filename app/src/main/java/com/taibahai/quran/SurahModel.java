package com.taibahai.quran;

public class SurahModel {
    private String position;

    private String catagory_id;
    private String id;
    private String hadiths_number;
    private String imam_name;
    private String hadiths_type;
    private String chapter;
    private String arabicText;
    private String englishText;
    private String english_translation;


    public SurahModel() {
    }

    public String getEnglish_translation() {
        return english_translation;
    }

    public void setEnglish_translation(String english_translation) {
        this.english_translation = english_translation;
    }

    public String getImam_name() {
        return imam_name;
    }

    public void setImam_name(String imam_name) {
        this.imam_name = imam_name;
    }

    public String getHadiths_type() {
        return hadiths_type;
    }

    public void setHadiths_type(String hadiths_type) {
        this.hadiths_type = hadiths_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatagory_id() {
        return catagory_id;
    }

    public void setCatagory_id(String catagory_id) {
        this.catagory_id = catagory_id;
    }

    public String getHadiths_number() {
        return hadiths_number;
    }

    public void setHadiths_number(String hadiths_number) {
        this.hadiths_number = hadiths_number;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getArabicText() {
        return arabicText;
    }

    public void setArabicText(String arabicText) {
        this.arabicText = arabicText;
    }

    public String getEnglishText() {
        return englishText;
    }

    public void setEnglishText(String englishText) {
        this.englishText = englishText;
    }

    @Override
    public String toString() {
        return "SurahModel{" +
                "position=" + position +
                ", arabicText='" + arabicText + '\'' +
                ", englishText='" + englishText + '\'' +
                '}';
    }
}
