package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.MenuActivities.ToolsScreen;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.HalfLifeTimerScreen;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class GameDisplay extends AppCompatActivity {

    final int PARTY_COEFFICIENT = 10;

    ClamatoUtils c;
    int id = 0;
    TextView title;
    TextView tagline;
    TextView instructions;
    TextView instructionsTitle;
    TextView blurb;
    TextView blurbTitle;
    TextView tags;
    ImageView favoriteButton;
    ImageView shuffleButton;
    ImageView mysteryButton;
    ClamatoUtils.ClamatoGame game;
    boolean fromFavorites;
    int mightyNumberNine;
    String titleString;
    String taglineString;
    String blurbString;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_info_display);
        c = new ClamatoUtils(this.getApplication());

        id = getIntent().getIntExtra("id", 0);
        fromFavorites = getIntent().getBooleanExtra("fromFav", false);

        game = c.getGameByID(id);

        title = findViewById(R.id.game_title);
        tagline = findViewById(R.id.tagline);
        instructions = findViewById(R.id.instructions_text);
        instructionsTitle = findViewById(R.id.instructions_title);
        blurb = findViewById(R.id.blurb_text);
        blurbTitle = findViewById(R.id.blurb_title);
        tags = findViewById(R.id.game_display_tags);
        favoriteButton = findViewById(R.id.game_bookmark_button);
        shuffleButton = findViewById(R.id.game_shuffle_button);
        mysteryButton = findViewById(R.id.game_link_button);

        titleString = game.getTitles()[0];
        title.setText(titleString);
        taglineString = game.getTaglines()[0];
        tagline.setText("\"" + taglineString + "\"");

        instructions.setText(game.getInstructions());

        blurbString = game.getTips()[0];
        mightyNumberNine = 0; // This variable means if it's a tip variant or fact, 0 = tip
        //This is terrible naming practice but its my app dammit
        blurb.setText(blurbString);

        tags.setText(game.getTagsText());

        if (!game.getCurated()) {

            tagline.setVisibility(View.GONE);
            blurb.setVisibility(View.GONE);

            blurbTitle.setText("Courtesy of Improv Encyclopedia");
            blurbTitle.setTextSize(14);

            instructionsTitle.setText("Description:");

        } else {
            blurbTitle.setText("Tip:");
        }

        if (isInFavorites()) {
            favoriteButton.setImageResource(R.drawable.bookmarked_button);
        } else {
            favoriteButton.setImageResource(R.drawable.unbookmarked_button);
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                toggleNoteFavorite();
                if (isInFavorites()) {
                    favoriteButton.setImageResource(R.drawable.bookmarked_button);
                } else {
                    favoriteButton.setImageResource(R.drawable.unbookmarked_button);
                }
            }
        });

        mysteryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mysteryButton();
            }
        });
        mysteryButton.setOnTouchListener((c.setButtonEffectListener(mysteryButton)));

        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleFacts();
                c.quickRotateImageView(shuffleButton, 100);
            }
        });
        shuffleButton.setOnTouchListener((c.setButtonEffectListener(shuffleButton)));

    }

    private boolean isInFavorites() {
        String oldFavorites = c.readFromFile("favorites.txt", "");
        return oldFavorites.contains("," + id + ",");
    }

    private void toggleNoteFavorite() {
        String favorites = c.readFromFile("favorites.txt", "");

        if (isInFavorites()) {
            favorites = favorites.substring(0, favorites.indexOf("," + id + ",")) + favorites.substring(favorites.indexOf("," + id + ",") + ("," + id + ",").length() - 1);
        } else {
            if (favorites.length() > 0) {
                favorites = favorites.substring(0, favorites.length() - 1) + "," + id + ",";
            } else {
                favorites = favorites + "," + id + ",";
            }
        }

        c.writeToFile(favorites, "favorites.txt", "");
        Log.d("FAVORITES", favorites);

    }

    private void shuffleFacts() {

        String newTitle = game.getNewTitle(titleString);
        if (!((newTitle).equals(titleString))) {
            c.fadeSwitchText(title, newTitle);
            titleString = newTitle;
        }

        String newTagline = game.getNewTagline(taglineString);
        if (!((newTagline).equals(taglineString))) {
            c.fadeSwitchText(tagline, "\"" + newTagline + "\"");
            taglineString = newTagline;
        }

        if (game.getCurated()) {
            int newNum = (int) (Math.random() * 3);
            if (mightyNumberNine != newNum) {
                if (newNum == 0) {
                    c.fadeSwitchText(blurbTitle, "Tip: ");
                } else if (newNum == 1) {
                    c.fadeSwitchText(blurbTitle, "Variant:");
                } else {
                    c.fadeSwitchText(blurbTitle, "Fun Fact:");
                }
                mightyNumberNine = newNum;
            }

            String newBlurb = game.getNewBlurb(blurbString, mightyNumberNine);
            if (!((newBlurb).equals(blurbString))) {
                c.fadeSwitchText(blurb, newBlurb);
                blurbString = newBlurb;
            }
        }

        ScrollView gameScroll = findViewById(R.id.GameScroll);
        gameScroll.scrollTo(0,0);
    }

    @Override
    public void onBackPressed() {
        if (fromFavorites && !isInFavorites()) {
            Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
            gamesList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            gamesList.putExtra("mode", "bookmarks");
            gamesList.putExtra("filter", 4);
            startActivity(gamesList);
        } else {
            super.onBackPressed();
        }
    }

    private void letsParty(int partyNumber) {
        if (partyNumber == 0) {
            c.quickVibe(500);
            c.quickRotateImageView(mysteryButton, 500);
        } else if (partyNumber == 1) {
            Toast.makeText(getApplicationContext(), "Your lucky number is... " + ((int) (Math.random() * 2000000000)), Toast.LENGTH_SHORT).show();
        } else if (partyNumber == 2) {
            Toast.makeText(getApplicationContext(), "I BANISH THEE FROM THIS GAME!!!", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        } else if (partyNumber == 3) {
            Intent appBrowser = new Intent(this, ToolsScreen.class);
            appBrowser.putExtra("tool", 1);
            startActivity(appBrowser);
        } else if (partyNumber == 4) {
            setTitle(c.generateTitle());
        }
    }

    private void mysteryButton() {
        if (game.getId() == 1) { //Half Life
            Intent appBrowser = new Intent(getApplicationContext(), HalfLifeTimerScreen.class);
            startActivity(appBrowser);
        } else if (game.getId() == 2) {

        } else {
            letsParty((int) (Math.random() * PARTY_COEFFICIENT));
        }
    }
}




