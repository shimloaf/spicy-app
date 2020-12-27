package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.GameActivities.GamesList;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class GamerScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClamatoUtils c;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        c = new ClamatoUtils(this.getApplication());

        ImageView gamesListButton = findViewById(R.id.games_list_button);
        ImageView shortFormButton = findViewById(R.id.short_form_button);
        ImageView longFormButton = findViewById(R.id.long_form_button);
        ImageView warmupButton = findViewById(R.id.warmup_button);
        ImageView bookmarkButton = findViewById(R.id.bookmark_button);
        ImageView searchButton = findViewById(R.id.search_button);
        gamesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "master_list");
                gamesList.putExtra("filter", 0);
                startActivity(gamesList);
            }
        });
        gamesListButton.setOnTouchListener((c.setButtonEffectListener(gamesListButton)));
        shortFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "none");
                gamesList.putExtra("filter", 1);
                startActivity(gamesList);
            }
        });
        shortFormButton.setOnTouchListener((c.setButtonEffectListener(shortFormButton)));
        longFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "none");
                gamesList.putExtra("filter", 2);
                startActivity(gamesList);
            }
        });
        longFormButton.setOnTouchListener((c.setButtonEffectListener(longFormButton)));
        warmupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "none");
                gamesList.putExtra("filter", 3);
                startActivity(gamesList);
            }
        });
        warmupButton.setOnTouchListener((c.setButtonEffectListener(warmupButton)));
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "bookmarks");
                gamesList.putExtra("filter", 4);
                startActivity(gamesList);
            }
        });
        bookmarkButton.setOnTouchListener((c.setButtonEffectListener(bookmarkButton)));
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gamesList = new Intent(getApplicationContext(), GamesList.class);
                gamesList.putExtra("mode", "search");
                gamesList.putExtra("filter", 0);
                startActivity(gamesList);
            }
        });
        searchButton.setOnTouchListener((c.setButtonEffectListener(searchButton)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent appBrowser = new Intent(this, HomeScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            Intent appBrowser = new Intent(this, SuggestionScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_timer) {
            Intent appBrowser = new Intent(this, TimerScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_tools) {
            Intent appBrowser = new Intent(this, ToolsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_showbuilder) {

        } else if (id == R.id.nav_credits) {
            Intent appBrowser = new Intent(this, CreditsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}




