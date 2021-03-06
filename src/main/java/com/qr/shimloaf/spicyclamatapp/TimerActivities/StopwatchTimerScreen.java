package com.qr.shimloaf.spicyclamatapp.TimerActivities;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;

public class StopwatchTimerScreen extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.stopwatch_timer;
    }

    boolean clockRunning = false;
    Handler clock;
    long millis, start, buffer, newTime = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView stopwatchReset = findViewById(R.id.stopwatchReset);
        final ImageView stopwatchToggle = findViewById(R.id.stopwatchPlayButton);

        clock = new Handler();

        stopwatchToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockToggle(!clockRunning);
            }
        });
        stopwatchToggle.setOnTouchListener((c.setButtonEffectListener(stopwatchToggle)));

        stopwatchReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockToggle(false);
                millis = 0;
                start = 0;
                buffer = 0;
                newTime = 0;
                changeClock();
            }
        });
        stopwatchReset.setOnTouchListener((c.setButtonEffectListener(stopwatchReset)));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void clockToggle(boolean shouldStart) {
        ImageView stopwatchToggle = findViewById(R.id.stopwatchPlayButton);
        if (shouldStart) {
            stopwatchToggle.setImageResource(R.drawable.pause_button);
            startClock();
            clockRunning = true;
        } else {
            stopwatchToggle.setImageResource(R.drawable.play_button);
            stopClock();
            clockRunning = false;
        }

    }

    private void stopClock() {
        clockRunning = false;
        buffer = millis;

        clock.removeCallbacks(runnable);
    }

    private void startClock() {
        clockRunning = true;
        start = SystemClock.uptimeMillis();
        clock.postDelayed(runnable, 0);
    }

    private void changeClock() {
        TextView clockDisplay = findViewById(R.id.stopwatchClockDisplay);

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

    public Runnable runnable = new Runnable() {

        public void run() {

            millis = SystemClock.uptimeMillis() - start;

            millis += buffer;

            changeClock();

            clock.postDelayed(this, 0);
        }

    };

}




