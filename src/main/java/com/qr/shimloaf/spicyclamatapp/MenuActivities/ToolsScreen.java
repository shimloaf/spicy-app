package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.ToolActivities.BuzzerScreen;
import com.qr.shimloaf.spicyclamatapp.ToolActivities.NotesScreen;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;

public class ToolsScreen extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    enum simpleTool {
        screwdriver,
        menu
    }

    final ToolsScreen t = this;
    boolean usingTool = false;
    int menuItem = 0;

    protected int getLayoutResourceId() {
        return R.layout.activity_tools;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

         menuItem = getIntent().getIntExtra("tool", 0);
         hideEverything();
         setUpMenu();

         simpleTool t = simpleTool.menu;
         if (menuItem == 1) {
             t = simpleTool.screwdriver;
             pullUpSimpleTool(t);
             usingTool = false;
         } else {
             pullUpSimpleTool(t);
         }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpMenu() {
        ImageView screwdriverButton = findViewById(R.id.screwdriver_button);
        ImageView buzzerButton = findViewById(R.id.buzzer_button);
        ImageView notesButton = findViewById(R.id.notepad_button);
        screwdriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullUpSimpleTool(simpleTool.screwdriver);
            }
        });
        screwdriverButton.setOnTouchListener((c.setButtonEffectListener(screwdriverButton)));
        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, NotesScreen.class);
                startActivity(appBrowser);
            }
        });
        notesButton.setOnTouchListener((c.setButtonEffectListener(notesButton)));
        buzzerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, BuzzerScreen.class);
                startActivity(appBrowser);
            }
        });
        buzzerButton.setOnTouchListener((c.setButtonEffectListener(buzzerButton)));
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
        } else if (!backPressed) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        backPressed = true;

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        c.navigateDrawer(item.getItemId(), getApplicationContext());
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }


}




