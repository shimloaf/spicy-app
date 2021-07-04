package com.qr.shimloaf.spicyclamatapp.ShowbuilderActivities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class TeamSettings extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.team_settings;
    }

    boolean[] nameValues;

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

        Chip first = findViewById(R.id.first_name_chip);
        Chip nickname = findViewById(R.id.nickname_chip);
        Chip surname = findViewById(R.id.last_name_chip);
        Chip last_initial = findViewById(R.id.initial_chip);

        nameValues = c.decodeNameChips();

        first.setChecked(nameValues[0]);
        nickname.setChecked(nameValues[1]);
        surname.setChecked(nameValues[2]);
        last_initial.setChecked(nameValues[3]);

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameValues[0] = !nameValues[0];
                c.toggleSetting(ClamatoUtils.setting.NameSettings, encodeNameChips());
            }
        });

        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameValues[1] = !nameValues[1];
                c.toggleSetting(ClamatoUtils.setting.NameSettings, encodeNameChips());
            }
        });

        surname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameValues[2] = !nameValues[2];
                nameValues[3] = false;
                last_initial.setChecked(false);
                c.toggleSetting(ClamatoUtils.setting.NameSettings, encodeNameChips());
            }
        });

        last_initial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameValues[3] = !nameValues[3];
                nameValues[2] = false;
                surname.setChecked(false);
                c.toggleSetting(ClamatoUtils.setting.NameSettings, encodeNameChips());
            }
        });

    }

    private String encodeNameChips() {

        StringBuilder ret = new StringBuilder();

        for (int n = 0; n < 4; n++) {
            if (nameValues[n]) {
                ret.append("1");
            } else {
                ret.append("0");
            }
        }
        return ret.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
