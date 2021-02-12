package com.qr.shimloaf.spicyclamatapp.GameActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.chip.Chip;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.util.ArrayList;
import java.util.Collections;

public class GamesList extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.games_list;
    }

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
    TextView gamesFooter;
    LinearLayout chipList;
    HorizontalScrollView chipListParent;

    ArrayList<String> tags;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mode = getIntent().getStringExtra("mode");
        filter = getIntent().getIntExtra("filter", 0);

        filterMenu = findViewById(R.id.filter_menu);
        gamesFooter = findViewById(R.id.games_footer);
        historyButton = findViewById(R.id.history_filter);
        searchBar = findViewById(R.id.search_bar);
        chipListParent = findViewById(R.id.chip_list);
        chipList = findViewById(R.id.chip_list_layout);

        gameIds = getGamesList(filter);
        tags = new ArrayList<>();

        if (mode.equals("none")) {
            gameIds = c.sortIdList(1, gameIds);
            filterMenu.setVisibility(View.VISIBLE);
        } else if (mode.equals("search")) {
            gameIds = c.sortIdList(1, gameIds);
            searchBar.setVisibility(View.VISIBLE);
            chipListParent.setVisibility(View.VISIBLE);
            filterMenu.setVisibility(View.VISIBLE);
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
                    setSortedGameList(-1);
                    sortMode = -1;
                } else {
                    setSortedGameList(1);
                    sortMode = 1;
                }
            }
        });

        shuffleButton = findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSortedGameList(2);
                sortMode = 2;
            }
        });

        playersButton = findViewById(R.id.player_filter);
        playersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sortMode == 3) {
                    setSortedGameList(-3);
                    sortMode = -3;
                } else {
                    setSortedGameList(3);
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

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                if (!tags.contains(s)) {
                    tags.add(s);

                    final Chip chip = new Chip(chipList.getContext(), null, R.attr.ChipStyle);
                    chip.setText(s);
                    chipList.addView(chip);
                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tags.remove(chip.getText());
                            chipList.removeView(chip);
                            String currentQuery = String.valueOf(searchBar.getQuery());
                            if (currentQuery.length() > 0 && currentQuery.charAt(0) != '#') {
                                setFilteredGameList(currentQuery);
                            } else if (currentQuery.length() == 0) {
                                setFilteredGameList("");
                            }
                        }
                    });
                }
                searchBar.setQuery("", false);
                searchBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 0 && s.charAt(0) != '#') {
                    setFilteredGameList(s);
                } else if (s.length() == 0) {
                    setFilteredGameList("");
                }
                return true;
            }
        });

    }

    private void setFilteredGameList(String query) {
        gameIds = c.getFilteredGameIds(query, "");
        for (String tag : tags) {
            gameIds = c.filterIds(tag, gameIds);
        }
        setSortedGameList(1);
        if (gameIds.size() < 3) {
            gamesFooter.setText(". . .");
            gamesFooter.setVisibility(View.VISIBLE);
        } else {
            gamesFooter.setVisibility(View.INVISIBLE);
        }
    }

    private void setSortedGameList(int filterMode) {
        gameIds = c.sortIdList(filterMode, gameIds);
        gAdapter = new GamesAdapter(gameIds);
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

            return new GameHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(final GameHolder holder, final int position) {

            ClamatoUtils.ClamatoGame game = c.getGameByID(idDatabase[position]);

            holder.curated = game.getCurated();

            holder.gameTitle.setText(game.getTitles()[0]);

            int nightModeFlags =
                    getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;

            if (!holder.curated) {
                if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
                    holder.cardBackground.setImageResource(R.drawable.game_background2);
                } else {
                    holder.cardBackground.setImageResource(R.drawable.game_background2_dark);
                }
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
                if (nightModeFlags != Configuration.UI_MODE_NIGHT_YES) {
                    holder.cardBackground.setImageResource(R.drawable.game_background);
                } else {
                    holder.cardBackground.setImageResource(R.drawable.game_background_dark);
                }
                holder.gameTagline.setVisibility(View.VISIBLE);
                holder.tags.setVisibility(View.VISIBLE);
                holder.gameTagline.setText("\"" + game.getRandomTagline() + "\"");
                holder.tags.setText(game.getPlayersTag(true) + " | " + game.getLength());
            }

            holder.gameCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gamesList = new Intent(getApplicationContext(), GameDisplay.class);
                    gamesList.putExtra("id", idDatabase[position].intValue());
                    gamesList.putExtra("fromFav", fromFav);
                    startActivity(gamesList);
                }
            });

            holder.gameCard.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        c.quickVibe(50);
                        holder.cardBackground.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                    } else if (action == MotionEvent.ACTION_UP) {
                        holder.cardBackground.clearColorFilter();
                    } else if (action == MotionEvent.ACTION_CANCEL) {
                        holder.cardBackground.clearColorFilter();
                    }

                    return holder.gameCard.onTouchEvent(event);
                }
            });

        }

        @Override
        public int getItemCount() {
            return idDatabase.length;
        }
    }
}




