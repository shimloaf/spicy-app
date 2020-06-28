package com.qr.shimloaf.spicyclamatapp.TimerActivities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class TenInSixtyTimerScreen extends AppCompatActivity {

    long millis = 60000;
    boolean clockRunning = false;
    CountDownTimer clock;
    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gauntlet_timer);
        c = new ClamatoUtils(this.getApplication());
    }

    @Override
    public void onBackPressed() {
        clockToggle(false);
        super.onBackPressed();
        this.finish();
    }

    private void updateClock() {
        TextView clockDisplay = findViewById(R.id.hClockDisplay);

        long seconds = millis / 1000;

        String time = Long.toString(millis);
        if (millis >= 10000) {
           time = time.substring(0, 2) + "." + time.substring(2, 4);
        } else if (millis > 1000){
           time = "0" + time.substring(0, 1) + "." + time.substring(1, 3);
        } else if (millis > 100){
           time = "00." + time.substring(0, 2);
        } else {
            time = "TIME UP!!!";
        }

        clockDisplay.setText(time);
    }

    private void clockToggle(boolean shouldStart) {
       if (shouldStart) {
            startClock();
            clockRunning = true;
        } else {
            stopClock();
            clockRunning = false;
        }

    }

    private void startClock() {
        clock = new CountDownTimer(millis, 10) {

            public void onTick(long millisUntilFinished) {
                millis = millisUntilFinished;
                updateClock();
            }

            public void onFinish() {
                clockToggle(false);
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibe.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
        }.start();
    }

    private void stopClock() {
        if (clock != null) {
            clock.cancel();
        }
    }

    private void resetClock() {
        millis = 60000;
        updateClock();
        clockToggle(false);
    }

}




