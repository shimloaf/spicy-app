package com.qr.shimloaf.spicyclamatapp;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

enum tool {

    screwdriver,
    bbb_list,
    menu

}

public class ToolsScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    boolean usingTool = false;
    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);

         c = new ClamatoUtils(this.getApplication());
         hideEverything();
         setUpMenu();
    }

    private void setUpMenu() {
        ImageView screwdriver_button = findViewById(R.id.screwdriver_button);
        screwdriver_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                pullUpSimpleTool(tool.screwdriver);
            }
        });
        pullUpSimpleTool(tool.menu);
    }

    private void pullUpSimpleTool(tool t) {
        hideEverything();
        usingTool = true;
        if (t == tool.screwdriver) {
            ImageView screwdriver = findViewById(R.id.screwdriver);
            screwdriver.setVisibility(View.VISIBLE);
        } else {
            ScrollView menu = findViewById(R.id.tools_menu);
            menu.setVisibility(View.VISIBLE);
            usingTool = false;
        }
    }

    private void hideEverything() {

        ScrollView menu = findViewById(R.id.tools_menu);
        menu.setVisibility(View.INVISIBLE);

        ImageView screwdriver = findViewById(R.id.screwdriver);
        screwdriver.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (usingTool) {
            pullUpSimpleTool(tool.menu);
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
            startActivity(appBrowser);
        } else if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(this, GamerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            Intent appBrowser = new Intent(this, SuggestionScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_timer) {
            Intent appBrowser = new Intent(this, TimerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_showbuilder) {

        } else if (id == R.id.nav_tools && usingTool) {
            pullUpSimpleTool(tool.menu);
        } else if (id == R.id.nav_credits) {
            Intent appBrowser = new Intent(this, CreditsScreen.class);
            startActivity(appBrowser);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}




