package com.spdata.em55;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by suntianwei on 2016/12/26.
 */

public class GetEm55External {

    public static String readEm55() {
        String state = null;
        Log.d(TAG, "readEm55: " + state);
        File file = new File("/sys/class/misc/aw9523/gpio");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            state = bufferedReader.readLine();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return state;
    }
}
