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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        String[] tags;
        boolean curated;

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
            if (!curated) {
                if (length != null && !length.equals("")) {
                    tagsList.add(length);
                }
                tagsList.add(titles[0]);
            } else {
                tagsList.add(length);
                tagsList.add(getPlayersTag(false));
                tagsList.add(titles[0]);
                tagsList.addAll(Arrays.asList(tags));
            }

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

        public boolean getCurated() {
            return curated;
        }

        public int hasCardInfo() {
            boolean hasLength = (length != null && !length.equals(""));
            boolean hasPlayer = (playersMin > 0 && playersMax > 0);
            if (curated) {
                return 0;
            } else if (hasLength && hasPlayer) {
                return 0;
            } else if (hasLength) {
                return 1;
            } else if (hasPlayer) {
                return 2;
            }
            return -1;
        }
    }


    Application a;
    HashMap<Integer, ClamatoGame> curatedGameList = new HashMap<>();
    HashMap<Integer, ClamatoGame> encyclopediaGameList = new HashMap<>();
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
        if (curatedGameList.size() == 0 && encyclopediaGameList.size() == 0) {
            String rawGamesJson = loadJSONFromAsset("Games List.json");
            JSONArray gamesArray = new JSONArray();
            try {
                gamesArray = new JSONArray(rawGamesJson);
            } catch (JSONException e) {
                Toast.makeText(a.getApplicationContext(), "David Hopping messed up his JSON formatting. Let him know.", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < gamesArray.length(); i++) {
                try {
                    JSONObject currentGame = gamesArray.getJSONObject(i);
                    ClamatoGame game = new ClamatoGame();

                    game.id = currentGame.getInt("id");

                    game.titles = new String[currentGame.getJSONArray("titles").length()];
                    for (int n = 0; n < currentGame.getJSONArray("titles").length(); n++) {
                        game.titles[n] = currentGame.getJSONArray("titles").getString(n);
                    }

                    game.instructions = currentGame.getString("instructions");

                    game.tips = new String[currentGame.getJSONArray("tips").length()];
                    for (int n = 0; n < currentGame.getJSONArray("tips").length(); n++) {
                        game.tips[n] = currentGame.getJSONArray("tips").getString(n);
                    }

                    game.facts = new String[currentGame.getJSONArray("facts").length()];
                    for (int n = 0; n < currentGame.getJSONArray("facts").length(); n++) {
                        game.facts[n] = currentGame.getJSONArray("facts").getString(n);
                    }

                    game.variants = new String[currentGame.getJSONArray("variants").length()];
                    for (int n = 0; n < currentGame.getJSONArray("variants").length(); n++) {
                        game.variants[n] = currentGame.getJSONArray("variants").getString(n);
                    }

                    game.playersMin = currentGame.getInt("playersMin");
                    game.playersMax = currentGame.getInt("playersMax");

                    game.length = currentGame.getString("length");

                    game.skills = new String[currentGame.getJSONArray("skills").length()];
                    for (int n = 0; n < currentGame.getJSONArray("skills").length(); n++) {
                        game.skills[n] = currentGame.getJSONArray("skills").getString(n);
                    }

                    game.taglines = new String[currentGame.getJSONArray("taglines").length()];
                    for (int n = 0; n < currentGame.getJSONArray("taglines").length(); n++) {
                        game.taglines[n] = currentGame.getJSONArray("taglines").getString(n);
                    }

                    game.tags = new String[currentGame.getJSONArray("tags").length()];
                    for (int n = 0; n < currentGame.getJSONArray("tags").length(); n++) {
                        game.tags[n] = currentGame.getJSONArray("tags").getString(n);
                    }

                    game.curated = true;

                    curatedGameList.put(game.id, game);
                    games.put(game.id, game);

                } catch (JSONException e) {
                    Toast.makeText(a.getApplicationContext(), "David Hopping messed up his JSON formatting. Let him know.", Toast.LENGTH_SHORT).show();
                }
            }

            int encyclopediaId = 10000;
            String[] encyclopediaGames = a.getResources().getStringArray(R.array.encyclopediagames);
            String[] encyclopediaGamesTitles = a.getResources().getStringArray(R.array.encyclopediagamestitles);
            for (int n = 0; n < encyclopediaGames.length; n++) {

                String title = encyclopediaGamesTitles[n].replaceAll(
                        String.format("%s|%s|%s",
                                "(?<=[A-Z])(?=[A-Z][a-z])",
                                "(?<=[^A-Z])(?=[A-Z])",
                                "(?<=[A-Za-z])(?=[^A-Za-z])"
                        ),
                        " "
                );

                String desc = encyclopediaGames[n];

                ClamatoGame game = new ClamatoGame();

                game.id = encyclopediaId;
                encyclopediaId++;

                game.titles = new String[1];
                game.titles[0] = title;

                game.instructions = desc;

                game.curated = false;

                game.tips = new String[1];
                game.tips[0] = "";

                game.facts = new String[1];
                game.facts[0] = "";

                game.variants = new String[1];
                game.variants[0] = "";

                game.playersMin = 0;
                game.playersMax = 0;

                if (desc.toLowerCase().contains("short form") && desc.toLowerCase().contains("long form")) {
                    game.length = "";
                } else if (desc.toLowerCase().contains("short form")) {
                    game.length = "Short Form";
                } else if (desc.toLowerCase().contains("long form")) {
                    game.length = "Long Form";
                } else if (desc.toLowerCase().contains("warmup") || desc.toLowerCase().contains("warm up") || desc.toLowerCase().contains("warm-up")) {
                    game.length = "Warmup";
                }

                game.skills = new String[1];
                game.skills[0] = "";

                game.taglines = new String[1];
                game.taglines[0] = "";

                game.tags = new String[1];
                game.tags[0] = "";

                encyclopediaGameList.put(game.id, game);
                games.put(game.id, game);
            }

        }
    }

    public ArrayList<Integer> sortIdList(int mode, ArrayList<Integer> idList) {
        if (mode == 0) {
            Collections.sort(idList);
        }
        return idList;
    }

    public ArrayList<Integer> getFilteredGameIds(String query, String length) {

        ArrayList<Integer> allCuratedGames = new ArrayList<>(curatedGameList.keySet());
        ArrayList<Integer> allEncyclopediaGames = new ArrayList<>(encyclopediaGameList.keySet());

        ArrayList<Integer> filteredCuratedGames = new ArrayList<>();
        ArrayList<Integer> filteredEncyclopediaGames = new ArrayList<>();

        ArrayList<Integer> allGames = new ArrayList<>();

        if (length.equals("Short Form") || length.equals("Long Form") || length.equals("Warmup")) {
            for (Integer i : allCuratedGames) {
                ClamatoGame curGame = curatedGameList.get(i);
                if (curGame.getLength() != null && curGame.getLength().equals(length)) {
                    filteredCuratedGames.add(i);
                }
            }

            for (Integer i : allEncyclopediaGames) {
                ClamatoGame curGame = encyclopediaGameList.get(i);
                if (curGame.getLength() != null && curGame.getLength().equals(length)) {
                    filteredEncyclopediaGames.add(i);
                }
            }
        } else if (!query.equals("")) {

        } else {
            filteredCuratedGames.addAll(allCuratedGames);
            filteredEncyclopediaGames.addAll(allEncyclopediaGames);
        }

        allGames.addAll(filteredCuratedGames);
        allGames.addAll(filteredEncyclopediaGames);

        return allGames;
    }


    public ArrayList<Integer> getAllGameIds(int mode) {
        generateGames();
        ArrayList<Integer> allGames = new ArrayList<>(games.keySet());

        if (mode == 0) { // All Games

            allGames = getFilteredGameIds("", "");

        } else if (mode == 1) { // Short Form Games, with curated on top.

            allGames = getFilteredGameIds("", "Short Form");

        } else if (mode == 2) { // Long Form Games, with curated on top.

            allGames = getFilteredGameIds("", "Long Form");

        } else if (mode == 3) { // Warmup Games, with curated on top.

            allGames = getFilteredGameIds("", "Warmup");

        }

        return allGames;
    }

    public ClamatoGame getGameByID(int id) {
        generateGames();
        return games.get(id);
    }

    public void verifySaveData(Context context) {

        String[] paths = new String[2];
        paths[0] = "notes.txt";
        paths[1] = "favorites.txt";
        for (String p : paths) {
            try {
                InputStream inputStream = context.openFileInput(p);
            } catch (IOException e) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(p, Context.MODE_PRIVATE));
                    if (p.equals("favorites.txt")) {
                        outputStreamWriter.write(",");
                    } else if (p.equals("notes.txt")) {
                        outputStreamWriter.write("Initialized");
                    }
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

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = a.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
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
            //Toast.makeText(a.getApplicationContext(), "File not found. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
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
            //Toast.makeText(a.getApplicationContext(), "File not found. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(a.getApplicationContext(), "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();

        }

        return ret.toString();
    }
}
