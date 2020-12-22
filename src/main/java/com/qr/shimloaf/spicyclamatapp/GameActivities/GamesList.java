package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.qr.shimloaf.spicyclamatapp.MenuActivities.SuggestionScreen;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.util.ArrayList;
import java.util.Collections;

public class GamesList extends AppCompatActivity {

    ClamatoUtils c;
    String mode;
    int filter = 0;
    boolean fromFav = false;
    int sortMode = 0;

    RecyclerView gameListDisplay;
    LinearLayoutManager gamesManager;
    GamesAdapter gAdapter;
    ArrayList<Integer> gameIds;
    FloatingActionButton alphabetSortButton;
    FloatingActionButton shuffleButton;
    FloatingActionButton playersButton;
    FloatingActionButton historyButton;
    FloatingActionMenu filterMenu;
    SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_list);
        c = new ClamatoUtils(this.getApplication());
        mode = getIntent().getStringExtra("mode");
        filter = getIntent().getIntExtra("filter", 0);

        filterMenu = findViewById(R.id.filter_menu);
        TextView gamesFooter = findViewById(R.id.games_footer);
        historyButton = findViewById(R.id.history_filter);
        searchBar = findViewById(R.id.search_bar);

        gameIds = getGamesList(filter);

        if (mode.equals("none")) {
            //Not sure what should happen here. Probably Floating action bar too though.
        } else if (mode.equals("search")) {
            searchBar.setVisibility(View.VISIBLE);
        } else if (mode.equals("master_list")) {
            gameIds = c.sortIdList(1, gameIds);
            filterMenu.setVisibility(View.VISIBLE);
        } else if (mode.equals("bookmarks")) {
            sortMode = 4;
            if (gameIds.size() == 0) {
                gamesFooter.setVisibility(View.VISIBLE);
            } else if (gameIds.size() < 3) {
                gamesFooter.setText(". . .");
                gamesFooter.setVisibility(View.VISIBLE);
            } else {
                filterMenu.setVisibility(View.VISIBLE);
                historyButton.setVisibility(View.VISIBLE);
            }
            fromFav = true;
        }

        gameListDisplay = (RecyclerView) findViewById(R.id.games_list_display);
        gameListDisplay.setHasFixedSize(true);

        gamesManager = new LinearLayoutManager(this);
        gameListDisplay.setLayoutManager(gamesManager);

        gAdapter = new GamesAdapter(gameIds);
        gameListDisplay.setAdapter(gAdapter);

        alphabetSortButton = findViewById(R.id.abc_filter);
        alphabetSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortMode == 1) {
                    setFilteredGamesList(-1);
                    sortMode = -1;
                } else {
                    setFilteredGamesList(1);
                    sortMode = 1;
                }
            }
        });

        shuffleButton = findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFilteredGamesList(2);
                sortMode = 2;
            }
        });

        playersButton = findViewById(R.id.player_filter);
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortMode == 3) {
                    setFilteredGamesList(-3);
                    sortMode = -3;
                } else {
                    setFilteredGamesList(3);
                    sortMode = 3;
                }
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortMode == 4) {
                    sortMode = -4;
                    ArrayList<Integer> ids = getGamesList(4);
                    Collections.reverse(ids);
                    gAdapter = new GamesAdapter(ids);
                    gameListDisplay.setAdapter(gAdapter);
                } else {
                    sortMode = 4;
                    gAdapter = new GamesAdapter(getGamesList(4));
                    gameListDisplay.setAdapter(gAdapter);
                }
            }
        });

    }

    private void setFilteredGamesList(int filterMode) {
        ArrayList<Integer> newIds = gameIds;
        c.sortIdList(filterMode, newIds);
        gAdapter = new GamesAdapter(newIds);
        gameListDisplay.setAdapter(gAdapter);
    }

    private ArrayList<Integer> getGamesList(int filterNum) {

        if (filter >= 0 && filter < 4) {
            return c.getAllGameIds(filterNum);
        } else if (filter == 4) { // Bookmarks
            //c.writeToFile("", "favorites.txt", "");
            String bookmarkIds = c.readFromFile("favorites.txt", "");
            ArrayList<Integer> ret = new ArrayList<>();

            while (bookmarkIds.length() > 0) {

                if (bookmarkIds.charAt(0) == ',') {
                    bookmarkIds = bookmarkIds.substring(1);
                }

                if (bookmarkIds.isEmpty()) {
                    break;
                }

                String curId = bookmarkIds.substring(0, bookmarkIds.indexOf(","));
                bookmarkIds = bookmarkIds.substring(bookmarkIds.indexOf(","));

                ret.add(Integer.valueOf(curId));

            }
            return ret;
        }
        return c.getAllGameIds(69);
    }

    @Override
    public void onBackPressed() {
        if (filterMenu.isOpened()) {
            filterMenu.close(true);
        } else {
            super.onBackPressed();
        }
    }

    public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameHolder> {
        private Integer[] idDatabase;

        public class GameHolder extends RecyclerView.ViewHolder {

            private ConstraintLayout gameCard;
            private TextView gameTitle;
            private TextView gameTagline;
            private TextView tags;
            private ImageView cardBackground;
            private boolean curated;

            public GameHolder(View v) {
                super(v);
                gameCard = v.findViewById(R.id.game_fragment);
                gameTitle = v.findViewById(R.id.game_title);
                gameTagline = v.findViewById(R.id.game_blurb);
                tags = v.findViewById(R.id.game_tags);
                cardBackground = v.findViewById(R.id.game_background);
            }
        }

        public GamesAdapter(ArrayList<Integer> myDataset) {
            idDatabase = myDataset.toArray(new Integer[0]);
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

            ClamatoUtils.ClamatoGame game = c.getGameByID(idDatabase[position]);

            holder.curated = game.getCurated();

            holder.gameTitle.setText(game.getTitles()[0]);

            if (!holder.curated) {
                holder.cardBackground.setImageDrawable(getDrawable(R.drawable.game_background2));
                holder.gameTagline.setVisibility(View.GONE);
                if (game.hasCardInfo() == -1) {
                    holder.tags.setVisibility(View.GONE);
                } else if (game.hasCardInfo() == 1) {
                    holder.tags.setVisibility(View.VISIBLE);
                    holder.tags.setText(game.getLength());
                } else if (game.hasCardInfo() == 2) {
                    holder.tags.setVisibility(View.VISIBLE);
                    holder.tags.setText(game.getPlayersTag(true));
                }
            } else {
                holder.cardBackground.setImageDrawable(getDrawable(R.drawable.game_background));
                holder.gameTagline.setVisibility(View.VISIBLE);
                holder.tags.setVisibility(View.VISIBLE);
                holder.gameTagline.setText("\"" + game.getRandomTagline() + "\"");
                holder.tags.setText(game.getPlayersTag(true) + " | " + game.getLength());
            }

            holder.gameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.quickVibe(50);
                    Intent gamesList = new Intent(getApplicationContext(), GameDisplay.class);
                    gamesList.putExtra("id", idDatabase[position].intValue());
                    gamesList.putExtra("fromFav", fromFav);
                    startActivity(gamesList);
                }
            });

        }

        @Override
        public int getItemCount() {
            return idDatabase.length;
        }
    }
}




