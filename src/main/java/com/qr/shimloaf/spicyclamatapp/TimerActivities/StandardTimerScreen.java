package com.qr.shimloaf.spicyclamatapp.TimerActivities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

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
            }
        });
        oneMinuteButton.setOnTouchListener((c.setButtonEffectListener(oneMinuteButton)));
        twoMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementClock(2);
            }
        });
        twoMinuteButton.setOnTouchListener((c.setButtonEffectListener(twoMinuteButton)));

        fiveMinuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementClock(5);
            }
        });
        fiveMinuteButton.setOnTouchListener((c.setButtonEffectListener(fiveMinuteButton)));

        cancelTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldSave = true;
                millis = 0;
                changeClock();
                clockToggle(false);
            }
        });
        cancelTimerButton.setOnTouchListener((c.setButtonEffectListener(cancelTimerButton)));

        resetTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shouldSave = true;
                millis = savedMillis;
                changeClock();
                clockToggle(false);
            }
        });
        resetTimerButton.setOnTouchListener((c.setButtonEffectListener(resetTimerButton)));

        clockToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shouldSave = false;
                TextView clockDisplay = findViewById(R.id.clockDisplay);
                if (millis == 0) {
                    clockDisplay.setText("OO:OO");
                } else {
                    clockToggle(!clockRunning);
                }
            }
        });
        clockToggleButton.setOnTouchListener((c.setButtonEffectListener(clockToggleButton)));

    }

    @Override
    public void onBackPressed() {
        stopClock();
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

    private void clockToggle(boolean shouldStart) {
        ImageView clockToggleButton = findViewById(R.id.startClock);
        if (shouldStart) {
            clockToggleButton.setImageResource(R.drawable.pause_button);
            startClock();
            clockRunning = true;
        } else {
            clockToggleButton.setImageResource(R.drawable.play_button);
            stopClock();
            clockRunning = false;
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




