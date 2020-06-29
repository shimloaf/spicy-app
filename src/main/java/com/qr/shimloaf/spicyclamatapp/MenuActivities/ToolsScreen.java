package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.HalfLifeTimerScreen;
import com.qr.shimloaf.spicyclamatapp.ToolActivities.BuzzerScreen;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

enum simpleTool {

    screwdriver,
    menu

}

public class ToolsScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    final ToolsScreen t = this;
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
        ImageView screwdriverButton = findViewById(R.id.screwdriver_button);
        ImageView buzzerButton = findViewById(R.id.buzzer_button);
        screwdriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                pullUpSimpleTool(simpleTool.screwdriver);
            }
        });
        buzzerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                Intent appBrowser = new Intent(t, BuzzerScreen.class);
                startActivity(appBrowser);
            }
        });
        pullUpSimpleTool(simpleTool.menu);
    }

    private void pullUpSimpleTool(simpleTool t) {
        hideEverything();
        usingTool = true;
        if (t == simpleTool.screwdriver) {
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
            pullUpSimpleTool(simpleTool.menu);
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
        } else if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(this, GamerScreen.class);
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
        } else if (id == R.id.nav_showbuilder) {

        } else if (id == R.id.nav_tools && usingTool) {
            pullUpSimpleTool(simpleTool.menu);
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




