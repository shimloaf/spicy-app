package com.qr.shimloaf.spicyclamatapp.ShowbuilderActivities;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class TeamSettings extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.team_settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SwitchCompat logoSwitch = findViewById(R.id.use_logo_switch);
        logoSwitch.setChecked((boolean) c.getSetting(ClamatoUtils.setting.UseLogo));

        logoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if ((boolean) c.getSetting(ClamatoUtils.setting.UseLogo)) {
                c.toggleSetting(ClamatoUtils.setting.UseLogo, "f");
            } else {
                c.toggleSetting(ClamatoUtils.setting.UseLogo, "t");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
