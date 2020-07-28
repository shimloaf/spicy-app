package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.os.Bundle;
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
    TextView blurb;
    TextView blurbTitle;
    TextView tags;
    ClamatoUtils.ClamatoGame game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_info_display);
        c = new ClamatoUtils(this.getApplication());

        id = getIntent().getIntExtra("id", 0);

        game = c.getGameByID(id);

        title = findViewById(R.id.game_title);
        tagline = findViewById(R.id.tagline);
        instructions = findViewById(R.id.instructions_text);
        blurb = findViewById(R.id.blurb_text);
        blurbTitle = findViewById(R.id.blurb_title);
        tags = findViewById(R.id.game_display_tags);

        title.setText(game.getRandomTitle());
        tagline.setText("\"" + game.getTaglines()[0] + "\"");

        instructions.setText(game.getInstructions());

        blurbTitle.setText("Tip:");
        blurb.setText(game.getTips()[0]);

        tags.setText(game.getTagsText());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}



