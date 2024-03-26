package com.taibahai.quran;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//@Entity annotation for giving table name and declaring it
@Entity(tableName = "favourite_table")
public class FavModel {
    //@primary Key to set id as primary key
    // and making auto increment for each new list
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "fav_position")
    long position;

    @ColumnInfo(name = "is_download")
    boolean isDownload;

    public FavModel(long position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}
