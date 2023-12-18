package com.taibahai.utils;

import android.content.Context;

import androidx.annotation.RawRes;


import com.taibahai.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JsonUtils {


    //read raw files method
    public static String readRawResource(Context context, @RawRes int res) {
        return readStream(context.getResources().openRawResource(res));
    }

    private static String readStream(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

//    public static String loadJSONFromAsset(Context context) {
//        String json = "";
//        try {
//            InputStream is = context.getResources().openRawResource(R.raw.allsurahlist);
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            json = new String(buffer, StandardCharsets.UTF_8);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            return null;
//        }
//        return json;
//    }



}