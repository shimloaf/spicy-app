package com.qr.shimloaf.spicyclamatapp.Utility;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.qr.shimloaf.spicyclamatapp.MenuActivities.CreditsScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.GamerScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.HomeScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.SettingsScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.ShowScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.SuggestionScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.TimerScreen;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.ToolsScreen;
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
import java.util.Comparator;
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
                tagsList.add("ImprovEncyclopedia");
            } else {
                tagsList.add(length);
                tagsList.add(getPlayersTag(false));
                tagsList.add(titles[0]);
                tagsList.addAll(Arrays.asList(tags));
                tagsList.add("SpicyCurated");
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

        //Retrieve value from ar != s
        //Return first value in the array if ar.length = 1
        //That will either be s or if s isnt in ar, ar[0]
        private String getNewInfo(String s, String[] ar) {
            String ret = s;
            ret = ar[(int) (Math.random() * ar.length)];
            if (ar.length > 1) {
                do {
                    ret = ar[(int) (Math.random() * ar.length)];
                } while (ret.equals(s));
            }
            return ret;
        }

        public String getNewTitle(String s) {
            return getNewInfo(s, titles);
        }

        public String getNewTagline(String s) {
            return getNewInfo(s, taglines);
        }

        public String getNewBlurb(String s, int mightyNumberNine) {
            if (mightyNumberNine == 0) {
                return getNewInfo(s, tips);
            } else if (mightyNumberNine == 1) {
                return getNewInfo(s, variants);
            } else {
                return getNewInfo(s, facts);
            }
        }

        public String getRandomBlurb() {

            int mightyNumberNine = (int) (Math.random() * 3);

            if (mightyNumberNine == 0) {
                return tips[(int) (Math.random() * tips.length)];
            } else if (mightyNumberNine == 1) {
                return variants[(int) (Math.random() * variants.length)];
            } else {
                return facts[(int) (Math.random() * facts.length)];
            }
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

    public void fadeSwitchText(final TextView t, final String s) {

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        final Animation out = new AlphaAnimation(1.0f, 0.0f);

        in.setDuration(500);
        out.setDuration(500);

        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                t.setText(s);
                t.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        t.startAnimation(out);
    }

    public void quickVibe(int n) {
        Vibrator vibe = (Vibrator) a.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibe.vibrate(VibrationEffect.createOneShot(n, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public void quickRotateImageView(final ImageView toRotate, final int duration, boolean fillAfter) {

        final RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(duration);
        rotateAnimation.setFillAfter(fillAfter);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation a) {
                //Nothing
            }

            public void onAnimationRepeat(Animation a) {
                //Nothing
            }

            public void onAnimationEnd(Animation a) {
                //Nothing
            }
        });

        toRotate.startAnimation(rotateAnimation);
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
                } else if (desc.toLowerCase().contains("short form") || desc.toLowerCase().contains("short-form")) {
                    game.length = "Short Form";
                } else if (desc.toLowerCase().contains("long form") || desc.toLowerCase().contains("long-form")) {
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

    public ArrayList<Integer> sortIdList(int mode, final ArrayList<Integer> idList) {
        if (mode == 1) {
            Collections.sort(idList, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {

                    if (curatedGameList.containsKey(lhs) && curatedGameList.containsKey(rhs)) {
                        return (curatedGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(curatedGameList.get(rhs).getTitles()[0].toLowerCase()));
                    } else {
                        if (curatedGameList.containsKey(lhs)) {
                            return -1;
                        } else if (curatedGameList.containsKey(rhs)) {
                            return 1;
                        } else {
                            return (encyclopediaGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(encyclopediaGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    }
                }
            });
        } else if (mode == -1) {
            Collections.sort(idList, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {

                    if (curatedGameList.containsKey(lhs) && curatedGameList.containsKey(rhs)) {
                        return -1 * (curatedGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(curatedGameList.get(rhs).getTitles()[0].toLowerCase()));
                    } else {
                        if (curatedGameList.containsKey(lhs)) {
                            return -1;
                        } else if (curatedGameList.containsKey(rhs)) {
                            return 1;
                        } else {
                            return -1 * (encyclopediaGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(encyclopediaGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    }
                }
            });
        } else if (mode == 2) {
            Collections.shuffle(idList);
            Collections.sort(idList, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    if (curatedGameList.containsKey(lhs)) {
                        return -1;
                    } else if (curatedGameList.containsKey(rhs)) {
                        return 1;
                    }
                    return 0;
                }
            });
        } else if (mode == 3) {
            Collections.sort(idList, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {

                    if (curatedGameList.containsKey(lhs) && curatedGameList.containsKey(rhs)) {
                        int lhsPlayers = curatedGameList.get(lhs).playersMin + curatedGameList.get(lhs).playersMax;
                        int rhsPlayers = curatedGameList.get(rhs).playersMin + curatedGameList.get(rhs).playersMax;
                        if (lhsPlayers > rhsPlayers) {
                            return 1;
                        } else if (lhsPlayers < rhsPlayers) {
                            return -1;
                        } else {
                            return (curatedGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(curatedGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    } else {
                        if (curatedGameList.containsKey(lhs)) {
                            return -1;
                        } else if (curatedGameList.containsKey(rhs)) {
                            return 1;
                        } else {
                            return (encyclopediaGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(encyclopediaGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    }
                }
            });
        } else if (mode == -3) {
            Collections.sort(idList, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {

                    if (curatedGameList.containsKey(lhs) && curatedGameList.containsKey(rhs)) {
                        int lhsPlayers = curatedGameList.get(lhs).playersMin + curatedGameList.get(lhs).playersMax;
                        int rhsPlayers = curatedGameList.get(rhs).playersMin + curatedGameList.get(rhs).playersMax;
                        if (lhsPlayers > rhsPlayers) {
                            return -1;
                        } else if (lhsPlayers < rhsPlayers) {
                            return 1;
                        } else {
                            return (curatedGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(curatedGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    } else {
                        if (curatedGameList.containsKey(lhs)) {
                            return -1;
                        } else if (curatedGameList.containsKey(rhs)) {
                            return 1;
                        } else {
                            return (encyclopediaGameList.get(lhs).getTitles()[0].toLowerCase().compareTo(encyclopediaGameList.get(rhs).getTitles()[0].toLowerCase()));
                        }
                    }
                }
            });
        }
        return idList;
    }

    public ArrayList<Integer> filterIds(String query, ArrayList<Integer> oldIds) {
        ArrayList<Integer> newIds = new ArrayList<>();
        query = query.toLowerCase();
        for (Integer i : oldIds) {
            ClamatoGame curGame = curatedGameList.get(i);
            if (curGame == null) {
                curGame = encyclopediaGameList.get(i);
            }
            if (query.charAt(0) == '#') {
                for (String tag : curGame.getTags()) {
                    if (tag.toLowerCase().equals(query.substring(1))) {
                        newIds.add(i);
                        break;
                    }
                }
            } else if (query.charAt(0) == '"' && query.charAt(query.length() - 1) == '"') {
                if (curGame.getInstructions().toLowerCase().contains(query.substring(1, query.length() - 1))) {
                    newIds.add(i);
                }
            } else {
                for (String title : curGame.getTitles()) {
                    if (title.toLowerCase().contains(query)) {
                        newIds.add(i);
                        break;
                    }
                }
            }
        }
        return newIds;
    }

    public ArrayList<Integer> getFilteredGameIds(String query, String length) {

        ArrayList<Integer> allCuratedGames = new ArrayList<>(curatedGameList.keySet());
        ArrayList<Integer> allEncyclopediaGames = new ArrayList<>(encyclopediaGameList.keySet());

        ArrayList<Integer> filteredCuratedGames = new ArrayList<>();
        ArrayList<Integer> filteredEncyclopediaGames = new ArrayList<>();

        ArrayList<Integer> allGames = new ArrayList<>();

        query = query.toLowerCase();

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
            for (Integer i : allCuratedGames) {
                ClamatoGame curGame = curatedGameList.get(i);
                for (String title : curGame.getTitles()) {
                    if (title.toLowerCase().contains(query)) {
                        filteredCuratedGames.add(i);
                        break;
                    }
                }
            }

            for (Integer i : allEncyclopediaGames) {
                ClamatoGame curGame = encyclopediaGameList.get(i);
                if (curGame.getTitles()[0].toLowerCase().contains(query)) {
                    filteredEncyclopediaGames.add(i);
                }
            }
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

        String[] paths = new String[4];
        paths[0] = "notes.txt";
        paths[1] = "favorites.txt";
        paths[2] = "settings.txt";
        paths[3] = "team.txt";
        for (String p : paths) {
            try {
                InputStream inputStream = context.openFileInput(p);
            } catch (IOException e) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(p, Context.MODE_PRIVATE));
                    outputStreamWriter.write("");
                    outputStreamWriter.close();

                    if (p.equals("favorites.txt")) {
                        writeToFile(",", p, "");
                    } else if (p.equals("notes.txt")) {
                        writeToFile("Initialized", p, "");
                    } else if (p.equals("settings.txt")) {
                        writeToFile(getSettingsDefault(), p, "");
                    } else if (p.equals("team.txt")) {
                        writeToFile("Tap to set Team Name,", p, "");
                    }

                    Toast.makeText(a.getApplicationContext(), "Initialized " + p, Toast.LENGTH_SHORT).show();
                } catch (IOException ex) {
                    Toast.makeText(a.getApplicationContext(), "File Write Failed. AKA David Hopping f**ked up coding.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public boolean isDarkMode() {
        String oldSettings = readFromFile("settings.txt", "");
        if (oldSettings.length() != getSettingsLength()) {
            writeToFile(getSettingsDefault(), "settings.txt", "");
        }
        if (oldSettings.length() == 0) {
            return false;
        } else {
            return oldSettings.charAt(0) == 't';
        }
    }

    public boolean isColorblindMode() {
        String oldSettings = readFromFile("settings.txt", "");
        if (oldSettings.length() != getSettingsLength()) {
            writeToFile(getSettingsDefault(), "settings.txt", "");
        }
        return oldSettings.charAt(1) == 't';
    }

    public int getSettingsLength() {
        return 2;
    }

    public String getSettingsDefault() {
        return "ff";
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

        verifySaveData(a.getApplicationContext());

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

    public View.OnTouchListener setButtonEffectListener(final ImageView toSet) {

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    quickVibe(50);
                    (toSet).setColorFilter(ContextCompat.getColor(a, R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                } else if (action == MotionEvent.ACTION_UP) {
                    (toSet).clearColorFilter();
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    (toSet).clearColorFilter();
                }

                return toSet.onTouchEvent(event);
            }
        };
    }

    public void navigateDrawer(int id, Context c) {
        if (id == R.id.nav_home) {
            Intent appBrowser = new Intent(c, HomeScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(c, GamerScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            Intent appBrowser = new Intent(c, SuggestionScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_timer) {
            Intent appBrowser = new Intent(c, TimerScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_tools) {
            Intent appBrowser = new Intent(c, ToolsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_showbuilder) {
            Intent appBrowser = new Intent(c, ShowScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_credits) {
            Intent appBrowser = new Intent(c, CreditsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        } else if (id == R.id.nav_settings) {
            Intent appBrowser = new Intent(c, SettingsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            c.startActivity(appBrowser);
        }
    }
}
