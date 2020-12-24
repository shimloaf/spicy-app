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

    final int MAX_SPINS = 20;
    final int BASE_SPEED = 45;
    final int MAX_MOMENTUM = 360 * MAX_SPINS;

    ClamatoUtils c;
    boolean inProgress = false;
    int momentum = 0;
    float curDegree = 0f;
    int presses = 0;
    int prevScore = 0;

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
        presses++;

        // A tap always adds a full spin, unless we have enough momentum for
        // more spins than the spin limit
        if (momentum < MAX_MOMENTUM) {
            momentum += 360;
        } else {
            momentum = MAX_MOMENTUM;
        }

        if (!inProgress) {
            inProgress = true;
            rotateImage(view);
        }
        c.quickVibe(100);
        setTitle(c.generateTitle());
    }


    public void changeTip() {

        TextView tip = findViewById(R.id.tip_message);
        if (presses == 0 && prevScore > 15) {
            tip.setText("Final Score:\n" + prevScore + " Presses!!!");
            prevScore = 0;
        } else if (presses < 15) {
            String[] tips = getResources().getStringArray(R.array.tips_array);
            String prevTip = (String) (tip.getText());
            do {
                tip.setText("Spicy Tip:\n" + tips[(int) (Math.random() * tips.length)]);
            } while ((tip.getText()).equals(prevTip));
        } else if (presses < 25) {
            tip.setText("Spicy Score:\n" + presses + " Presses.");
        } else if (presses < 50) {
            tip.setText("Spicy Score:\n" + presses + " Presses!");
        } else if (presses < 75) {
            tip.setText("Spicy Score:\n" + presses + " Presses!!");
        } else if (presses > 999) {

            StringBuilder awesomeString = new StringBuilder(" Presses!!!!");
            for (int n = 0; n < presses - 1000; n++) {
                awesomeString.append("!");
            }

            tip.setText("Spicy Score:\n" + presses + awesomeString);
        } else {
            tip.setText("Spicy Score:\n" + presses + " Presses!!!");
        }
    }

    public void rotateImage(final View view) {

        int speed = BASE_SPEED + (5 * (momentum / 180));
        if (curDegree + speed > 360 && momentum < 360) {
            speed = 360 - (int) curDegree;
            momentum = speed;
        }

        final RotateAnimation rotateAnimation = new RotateAnimation(curDegree,  curDegree + speed,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(100);
        rotateAnimation.setFillAfter(true);

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

        if (momentum <= 0) {
            prevScore = presses;
            presses = 0;
            if (prevScore > 15) {
                changeTip();
            }
        }

        curDegree = curDegree % 360;
        findViewById(R.id.logo).startAnimation(rotateAnimation);
    }

}
