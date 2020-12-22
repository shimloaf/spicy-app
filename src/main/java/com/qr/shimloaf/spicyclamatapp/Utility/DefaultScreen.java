package com.qr.shimloaf.spicyclamatapp.Utility;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.R;

public class DefaultScreen extends AppCompatActivity {

    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        c = new ClamatoUtils(this.getApplication());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




