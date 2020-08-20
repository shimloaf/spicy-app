package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class GameDisplay extends AppCompatActivity {

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
    ClamatoUtils.ClamatoGame game;
    boolean fromFavorites;

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

        title.setText(game.getRandomTitle());
        tagline.setText("\"" + game.getTaglines()[0] + "\"");

        instructions.setText(game.getInstructions());

        blurb.setText(game.getTips()[0]);

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
            favoriteButton.setImageDrawable(getDrawable(R.drawable.bookmarked_button));
        } else {
            favoriteButton.setImageDrawable(getDrawable(R.drawable.unbookmarked_button));
        }

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                toggleNoteFavorite();
                if (isInFavorites()) {
                    favoriteButton.setImageDrawable(getDrawable(R.drawable.bookmarked_button));
                } else {
                    favoriteButton.setImageDrawable(getDrawable(R.drawable.unbookmarked_button));
                }
            }
        });


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
}




