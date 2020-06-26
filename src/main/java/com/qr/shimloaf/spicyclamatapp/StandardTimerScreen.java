package com.qr.shimloaf.spicyclamatapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StandardTimerScreen extends AppCompatActivity {


    long millis = 0;
    boolean shouldSave = true;
    boolean clockRunning = false;
    long savedMillis = 0;
    CountDownTimer clock;
    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.default_timer);
        c = new ClamatoUtils(this.getApplication());

        ImageView oneMinuteButton = findViewById(R.id.oneMinuteButton);
        ImageView twoMinuteButton = findViewById(R.id.twoMinuteButton);
        ImageView fiveMinuteButton = findViewById(R.id.fiveMinuteButton);
        ImageView cancelTimerButton = findViewById(R.id.cancelTimerButton);
        ImageView resetTimerButton = findViewById(R.id.resetTimerButton);
        final ImageView clockToggleButton = findViewById(R.id.startClock);

        oneMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementClock(1);
                c.quickVibe(50);
            }
        });
        twoMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementClock(2);
                c.quickVibe(50);
            }
        });

        fiveMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementClock(5);
                c.quickVibe(50);
            }
        });

        cancelTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldSave = true;
                millis = 0;
                changeClock();
                clockToggle(false);
                c.quickVibe(50);
            }
        });

        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldSave = true;
                millis = savedMillis;
                changeClock();
                clockToggle(false);
                c.quickVibe(50);
            }
        });

        clockToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shouldSave = false;
                c.quickVibe(100);
                TextView clockDisplay = findViewById(R.id.clockDisplay);
                if (millis == 0) {
                    clockDisplay.setText("OO:OO");
                } else {
                    clockToggle(!clockRunning);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void incrementClock(int value) {

        if (clockRunning) {
            stopClock();
        }
        millis += value * 60000;
        if (shouldSave) {
           savedMillis = millis;
        }
        changeClock();
        if (clockRunning) {
            startClock();
        }
    }

    private void changeClock() {
        TextView clockDisplay = findViewById(R.id.clockDisplay);

        long seconds = millis / 1000;
        long minutes = seconds / 60;

        String s = Long.toString(minutes);

        if (minutes < 10) {
            s = "0" + s;
        }

        if (seconds % 60 == 0) {
            s = s + ":00";
        } else {
            long remainingSecs = seconds % 60;
            if (remainingSecs < 10) {
                s = s + ":0" + Long.toString(remainingSecs);
            } else {
                s = s + ":" + Long.toString(remainingSecs);
            }
        }

        clockDisplay.setText(s);
    }

    private void clockToggle(boolean on) {

        clockRunning = on;

        ImageView clockToggleButton = findViewById(R.id.startClock);
        if (clockRunning) {
            clockToggleButton.setImageResource(R.drawable.pause_button);
            startClock();
        } else {
            clockToggleButton.setImageResource(R.drawable.play_button);
            stopClock();
        }

    }

    private void startClock() {
        clock = new CountDownTimer(millis, 1000) {
            ImageView clockToggleButton = findViewById(R.id.startClock);

            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                changeClock();
            }

            public void onFinish() {
                clockToggle(false);
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibe.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        }.start();
    }

    private void stopClock() {
        if (clock != null) {
            clock.cancel();
        }
    }
}




