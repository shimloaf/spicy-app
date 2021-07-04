package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class SettingsScreen extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected int getLayoutResourceId() {
        return R.layout.activity_settings;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
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

        SwitchCompat darkSwitch = findViewById(R.id.dark_mode_switch);
        darkSwitch.setChecked(c.isDarkMode());

        SwitchCompat colorblindSwitch = findViewById(R.id.colorblind_mode_switch);
        colorblindSwitch.setChecked(c.isColorblindMode());

        TextInputEditText palName = findViewById(R.id.suggestion_nickname);
        palName.setText((String) c.getSetting(ClamatoUtils.setting.PalNickname));

        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (c.isDarkMode()) {
                c.toggleSetting(ClamatoUtils.setting.DarkMode, "f");
            } else {
                c.toggleSetting(ClamatoUtils.setting.DarkMode, "t");
            }
            if (c.isDarkMode()) {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        colorblindSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (c.isColorblindMode()) {
                c.toggleSetting(ClamatoUtils.setting.ColorblindMode, "f");
            } else {
                c.toggleSetting(ClamatoUtils.setting.ColorblindMode, "t");
            }
        });

        palName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            c.toggleSetting(ClamatoUtils.setting.PalNickname, String.valueOf(palName.getText()));
                            palName.clearFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(palName.getWindowToken(), 0);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {

        findViewById(R.id.suggestion_nickname).clearFocus();

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
