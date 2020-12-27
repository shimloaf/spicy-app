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
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.HalfLifeTimerScreen;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.StandardTimerScreen;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.StopwatchTimerScreen;
import com.qr.shimloaf.spicyclamatapp.TimerActivities.TenInSixtyTimerScreen;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class TimerScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);

         c = new ClamatoUtils(this.getApplication());
        setUpMenu();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpMenu() {

        ImageView defaultTimerButton = findViewById(R.id.standard_timer_button);
        ImageView stopwatchTimerButton = findViewById(R.id.stopwatch_timer_button);
        ImageView halfLifeTimerButton = findViewById(R.id.halflife_timer_button);
        ImageView tenin60TimerButton = findViewById(R.id.tenin60_timer_button);
        final TimerScreen t = this;
        defaultTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, StandardTimerScreen.class);
                startActivity(appBrowser);
            }
        });
        defaultTimerButton.setOnTouchListener((c.setButtonEffectListener(defaultTimerButton)));
        stopwatchTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, StopwatchTimerScreen.class);
                startActivity(appBrowser);
            }
        });
        stopwatchTimerButton.setOnTouchListener((c.setButtonEffectListener(stopwatchTimerButton)));
        halfLifeTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, HalfLifeTimerScreen.class);
                startActivity(appBrowser);
            }
        });
        halfLifeTimerButton.setOnTouchListener((c.setButtonEffectListener(halfLifeTimerButton)));
        tenin60TimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appBrowser = new Intent(t, TenInSixtyTimerScreen.class);
                startActivity(appBrowser);
            }
        });
        tenin60TimerButton.setOnTouchListener((c.setButtonEffectListener(tenin60TimerButton)));

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
        } else if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(this, GamerScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            Intent appBrowser = new Intent(this, SuggestionScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        } else if (id == R.id.nav_tools) {
            Intent appBrowser = new Intent(this, ToolsScreen.class);
            appBrowser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(appBrowser);
        }  else if (id == R.id.nav_showbuilder) {

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




