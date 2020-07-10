package com.qr.shimloaf.spicyclamatapp.Utility;

import android.app.Application;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class ClamatoUtils extends AppCompatActivity {

    Application a;
    MediaPlayer mp = new MediaPlayer();

    public ClamatoUtils(Application application) {
        a = application;
    }

    public String getUniqueDelineation() {
        return "978234897453298723458972539768523987";
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
        return (part1[(int) (Math.random() * part1.length)] + " " + part2[(int) (Math.random() * part2.length)]);
    }

    public void verifySaveData(Context context) {

        String[] paths = new String[1];
        paths[0] = "notes.txt";

        for (String p : paths) {
            try {
                InputStream inputStream = context.openFileInput(p);
            } catch (IOException e) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(p, Context.MODE_PRIVATE));
                    outputStreamWriter.write("Initialized.");
                    outputStreamWriter.close();
                    Toast.makeText(context, "Initialized " + p, Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(context, "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void removeFile(String path) {

    }

    public void renameFile(String oldPath, String oldDirectory, String newPath, String newDirectory) {
        writeToFile(readFromFile(oldPath, oldDirectory), newPath, newDirectory);
        removeFile(oldPath);
    }

    public void writeToFile(String data, String path, String directory) {
        File d = a.getApplicationContext().getDir(directory, Context.MODE_PRIVATE);
        File myPath = new File(d, path);

        try {
            FileWriter writer = new FileWriter(myPath);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            Toast.makeText(a.getApplicationContext(), "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        }
    }

    public String readFromFile(String path, String directory) {

        StringBuilder ret = new StringBuilder();
        File d = a.getApplicationContext().getDir(directory, Context.MODE_PRIVATE);
        File myPath = new File(d, path);

        try (FileReader reader = new FileReader(myPath)) {
            int content;

            while ((content = reader.read()) != -1) {
                ret.append((char) content);
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(a.getApplicationContext(), "File not found. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(a.getApplicationContext(), "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();

        }

        return ret.toString();
    }

    public String readFromFile(File f) {

        StringBuilder ret = new StringBuilder();

        try (FileReader reader = new FileReader(f)) {
            int content;

            while ((content = reader.read()) != -1) {
                ret.append((char) content);
            }

        } catch (FileNotFoundException e) {
            Toast.makeText(a.getApplicationContext(), "File not found. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(a.getApplicationContext(), "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();

        }

        return ret.toString();
    }
}
