package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final int START_SPINS = 1;
    final float START_SPEED = 20f;
    final float MAX_SPEED = 180f;
    final int MAX_SPINS = 500;

    ClamatoUtils c;
    boolean inProgress = false;
    float momentum = 0;
    float curDegree = 0f;
    float speed = START_SPEED;
    int spinLimit = START_SPINS;

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
        c.verifySaveData(this);

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

    public void onImageClick(View view) {
        changeTip();

        // A tap always adds a full spin, unless we have enough momentum for
        // more spins than the spin limit
        if (momentum <= 360 * spinLimit) {
            momentum += 360;
        } else {

            //Otherwise, a tap will increase the speed and let the wheel spin one more time before stopping.
            if (speed < MAX_SPEED) {
                speed += 10f;
            }
            if (spinLimit < MAX_SPINS) {
                spinLimit++;
            }
        }

        if (!inProgress) {
            rotateImage(view);
        }
        c.quickVibe(100);
        setTitle(c.generateTitle());
    }


    public void changeTip() {
        TextView tip = findViewById(R.id.tip_message);
        String[] tips = getResources().getStringArray(R.array.tips_array);
        String prevTip = (String)(tip.getText());
        do {
            tip.setText("Spicy Tip:\n" + tips[(int)(Math.random()*tips.length)]);
        } while ((tip.getText()).equals(prevTip));
    }

    public void rotateImage(final View view) {

        if (spinLimit > START_SPINS && momentum <= 360 * (spinLimit - 1)) {
            if (speed > START_SPEED) {
                speed -= 10f;
            }
            spinLimit--;
        }

        final RotateAnimation rotateAnimation = new RotateAnimation(curDegree,  curDegree + speed,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(100);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation a) {
                //Nothing
            }

            public void onAnimationRepeat(Animation a) {
                //Nothing
            }

            public void onAnimationEnd(Animation a) {
                if (momentum > 0) {
                    rotateImage(view);
                } else {
                    inProgress = false;
                }
            }
        });

        curDegree += speed;
        momentum -= speed;

        findViewById(R.id.logo).startAnimation(rotateAnimation);
    }

}
