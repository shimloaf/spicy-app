package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.util.ArrayList;

public class GamesList extends AppCompatActivity {

    ClamatoUtils c;
    String mode;
    int filter = 0;

    RecyclerView gameListDisplay;
    LinearLayoutManager gamesManager;
    GamesAdapter gAdapter;
    ArrayList<Integer> gameIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_list);
        c = new ClamatoUtils(this.getApplication());
        mode = getIntent().getStringExtra("mode");
        filter = getIntent().getIntExtra("filter", 0);

        FloatingActionButton filterButton = findViewById(R.id.filter_button);
        TextView gamesFooter = findViewById(R.id.games_footer);

        gameIds = getGamesList(filter);

        if (mode.equals("none")) {
            //Not sure what should happen here. Probably Floating action bar too though.
        } else if (mode == "search") {
            // Set the Search View to visible here.
        } else if (mode.equals("master_list")) {
            filterButton.setVisibility(View.VISIBLE);
        } else if (mode.equals("bookmarks")) {

            if (gameIds.size() == 0) {
                gamesFooter.setVisibility(View.VISIBLE);
            } else if (gameIds.size() < 3) {
                gamesFooter.setText(". . .");
                gamesFooter.setVisibility(View.VISIBLE);
            }
        }

        gameListDisplay = (RecyclerView) findViewById(R.id.games_list_display);
        gameListDisplay.setHasFixedSize(true);

        gamesManager = new LinearLayoutManager(this);
        gameListDisplay.setLayoutManager(gamesManager);

        gAdapter = new GamesAdapter(gameIds);
        gameListDisplay.setAdapter(gAdapter);

    }

    private ArrayList<Integer> getGamesList(int filterNum) {

        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(1);
        return ids;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameHolder> {
        private ArrayList<Integer> idDatabase;

        public class GameHolder extends RecyclerView.ViewHolder {

            private ConstraintLayout gameCard;
            private TextView gameTitle;
            private TextView gameTagline;
            private TextView tags;

            public GameHolder(View v) {
                super(v);
                gameCard = v.findViewById(R.id.game_fragment);
                gameTitle = v.findViewById(R.id.game_title);
                gameTagline = v.findViewById(R.id.game_blurb);
                tags = v.findViewById(R.id.game_tags);
            }
        }

        public GamesAdapter(ArrayList<Integer> myDataset) {
            idDatabase = myDataset;
        }

        @Override
        public GameHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.game_fragment, parent, false);

            GameHolder vh = new GameHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final GameHolder holder, final int position) {

            ClamatoUtils.ClamatoGame game = c.getGameByID(idDatabase.get(position).intValue());

            holder.gameTitle.setText(game.getRandomTitle());
            holder.gameTagline.setText("\"" + game.getRandomTagline() + "\"");
            holder.tags.setText(game.getPlayersTag(true) + " | " + game.getLength());

            holder.gameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.quickVibe(50);
                    Intent gamesList = new Intent(getApplicationContext(), GameDisplay.class);
                    gamesList.putExtra("id", idDatabase.get(position).intValue());
                    startActivity(gamesList);
                }
            });

        }

        @Override
        public int getItemCount() {
            return idDatabase.size();
        }
    }
}




