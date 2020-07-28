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
import java.util.ArrayList;
import java.util.HashMap;

public class ClamatoUtils extends AppCompatActivity {

    public static class ClamatoGame {
        int id;
        String[] titles;
        String instructions;
        String[] tips;
        String[] facts;
        String[] variants;
        int playersMin;
        int playersMax;
        String length;
        String[] skills;
        String[] taglines;

        public int getId() {
            return id;
        }

        public String getInstructions() {
            return instructions;
        }

        public String getLength() {
            return length;
        }

        public String[] getFacts() {
            return facts;
        }

        public String[] getSkills() {
            return skills;
        }

        public String[] getTaglines() {
            return taglines;
        }

        public String[] getTips() {
            return tips;
        }

        public String[] getTitles() {
            return titles;
        }

        public String getPlayersTag(boolean verbose) {
            if (verbose) {
                if (playersMin != playersMax) {
                    return (playersMin + "-" + playersMax + " Players");
                } else {
                    return (playersMax + " Players");
                }
            } else {
                if (playersMin != playersMax) {
                    return (playersMin + "-" + playersMax + "players");
                } else {
                    return (playersMax + "players");
                }
            }
        }

        public boolean isPlayerInRange(int player) {
            return (player <= playersMax || player >= playersMin);
        }

        public String[] getTags() {
            ArrayList<String> tagsList = new ArrayList<>();
            tagsList.add(length);
            tagsList.add(getPlayersTag(false));
            tagsList.add(titles[0]);
            return tagsList.toArray(new String[0]);
        }

        public String getTagsText() {

            String[] tags = getTags();
            StringBuilder ret = new StringBuilder();

            for (String s : tags) {
                ret.append("#").append(s).append(", ");
            }
            return ret.toString().substring(0, ret.toString().length() - 2);
        }

        public String getRandomTitle() {
            return titles[(int) (Math.random() * titles.length)];
        }

        public String getRandomTagline() {
            return taglines[(int) (Math.random() * taglines.length)];
        }
    }


    Application a;
    HashMap<Integer, ClamatoGame> games = new HashMap<>();
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

    private void generateGames() {
        if (games.size() == 0) {
            ClamatoGame game = new ClamatoGame();

            game.id = 1;

            game.titles = new String[1];
            game.titles[0] = "Half Life";

            game.instructions = "Perform a short scene in 60 seconds, then perform the \"exact\" same scene in 30 seconds, then 15, and so on and so forth.";

            game.tips = new String[1];
            game.tips[0] = "Try and be very physical while performing the scene! It's much easier to remember big physical events than exact lines of dialogue, and the audience will catch on with the gimmick quicker!";

            game.facts = new String[1];
            game.facts[0] = "Uranium-234's half life is the shortest of all time, clocking in at 245,500 years.";

            game.variants = new String[1];
            game.variants[0] = "A good way to play this game with three players is by having the third player start off on the sidelines, and then enter the scene at about halfway through the scene. That way, you can easily define a pivotal moment in the scene, for free!";

            game.playersMin = 2;
            game.playersMax = 3;

            game.length = "Short Form";

            game.skills = new String[1];
            game.skills[0] = "Physicality";

            game.taglines = new String[1];
            game.taglines[0] = "Now let's do it in 0.00732421975 seconds!";

            games.put(game.id, game);
        }
    }

    public ClamatoGame getGameByID(int id) {
        generateGames();
        return games.get(id);
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
