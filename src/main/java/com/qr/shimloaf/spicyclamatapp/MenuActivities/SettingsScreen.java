package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;

public class SettingsScreen extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    private enum setting {
        DarkMode,
        ColorblindMode
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

        navigationView.getMenu().getItem(7).setChecked(true);
        c.verifySaveData(this);

        SwitchCompat darkSwitch = findViewById(R.id.dark_mode_switch);
        darkSwitch.setChecked(c.isDarkMode());

        SwitchCompat colorblindSwitch = findViewById(R.id.colorblind_mode_switch);
        colorblindSwitch.setChecked(c.isColorblindMode());

        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSetting(setting.DarkMode);
                if (c.isDarkMode()) {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        colorblindSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleSetting(setting.ColorblindMode);
            }
        });

    }

    private String setCharAt(int pos, String toModify, char toChange) {
        return toModify.substring(0, pos) + toChange + toModify.substring(pos + 1);
    }

    private void toggleSetting(setting s) {
        String settings = c.readFromFile("settings.txt", "");

        if (settings.length() != c.getSettingsLength()) {
            settings = c.getSettingsDefault();
            c.writeToFile(settings, "settings.txt", "");
        }

        if (s == setting.DarkMode) {
            if (c.isDarkMode()) {
                settings = setCharAt(0, settings, 'f');
            } else {
                settings = setCharAt(0, settings, 't');
            }
        } else if (s == setting.ColorblindMode) {
            if (c.isColorblindMode()) {
                settings = setCharAt(1, settings, 'f');
            } else {
                settings = setCharAt(1, settings, 't');
            }
        }

        c.writeToFile(settings, "settings.txt", "");
        Log.d("FAVORITES", settings);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
