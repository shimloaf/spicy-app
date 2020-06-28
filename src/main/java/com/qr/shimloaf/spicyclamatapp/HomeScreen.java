package com.qr.shimloaf.spicyclamatapp;

import android.app.Activity;
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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClamatoUtils c;
    boolean inProgress = false;
    int rotations = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
        changeTip();
        c = new ClamatoUtils(this.getApplication());

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

        if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(this, GamerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            Intent appBrowser = new Intent(this, SuggestionScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_timer) {
            Intent appBrowser = new Intent(this, TimerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_tools) {
            Intent appBrowser = new Intent(this, ToolsScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_showbuilder) {

        } else if (id == R.id.nav_credits) {
            Intent appBrowser = new Intent(this, CreditsScreen.class);
            startActivity(appBrowser);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onImageClick(View view) {
        changeTip();
        if (!inProgress) {
            rotateImage(view);
        } else {
            rotations++;
        }
        c.quickVibe(100);
        setTitle(c.generateTitle());
    }


    public void changeTip() {
        TextView tip = findViewById(R.id.tip_message);
        String[] tips = getResources().getStringArray(R.array.tips_array);
        String prevTip = (String)(tip.getText());
        do {
            tip.setText(tips[(int)(Math.random()*tips.length)]);
        } while ((tip.getText()).equals(prevTip));
    }

    public void rotateImage(View view) {

        final RotateAnimation rotateAnimation = new RotateAnimation(0,  360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(500);
        rotateAnimation.setRepeatCount(Animation.ABSOLUTE);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation a) {
                inProgress = true;
            }

            public void onAnimationRepeat(Animation a) {
                rotations--;
            }

            public void onAnimationEnd(Animation a) {
                inProgress = false;
                if (rotations > 0) {
                    rotateAnimation.setRepeatCount(rotations);
                }
            }
        });

        findViewById(R.id.logo).startAnimation(rotateAnimation);
    }

}
