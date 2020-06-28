package com.qr.shimloaf.spicyclamatapp;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ClamatoUtils extends AppCompatActivity {

    Application a;

    public ClamatoUtils(Application application) {
        a = application;
    }

    public void quickVibe(int n) {
        Vibrator vibe = (Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(n, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public String generateTitle() {
        String[] part1 = a.getResources().getStringArray(R.array.titles_1);
        String[] part2 = a.getResources().getStringArray(R.array.titles_2);
        return (part1[(int)(Math.random()*part1.length)] + " " + part2[(int)(Math.random()*part2.length)]);
    }

    private void writeToFile(String data, String path, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(path, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Toast.makeText(context, "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        }
    }

    private String readFromFile(String path, Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(path);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(context, "File not found. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();

        }

        return ret;
    }
}
