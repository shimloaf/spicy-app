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

import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;
import com.qr.shimloaf.spicyclamatapp.R;

public class HalfLifeTimerScreen extends AppCompatActivity {


    final long highestValue = 60000;
    long topValue = highestValue;

    long millis = 0;
    boolean clockRunning = false;
    CountDownTimer clock;
    ClamatoUtils c;
    ObjectAnimator animateBar;
    ObjectAnimator reverseAnimateBar;
    ImageView halfTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.half_life_timer);
        c = new ClamatoUtils(this.getApplication());

        halfTimeButton = findViewById(R.id.hHalfButton);

        resetClock();

        ImageView playButton = findViewById(R.id.hPlay);
        ImageView resetButton = findViewById(R.id.hReset);
        ImageView abortButton = findViewById(R.id.hAbortButton);
        ImageView meter = findViewById(R.id.hProgressBar);
        final ImageView explosion = findViewById(R.id.massiveExplosion);

        reverseAnimateBar = ObjectAnimator.ofFloat(meter, "translationY", 0f);
        animateBar = ObjectAnimator.ofFloat(meter, "translationY", 1875f);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetClock();
                c.quickVibe(50);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickVibe(50);
                if (millis > 100) {
                    clockToggle(true);
                    millis = topValue;
                    animateMeter();
                }
            }
        });

        halfTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button_depressed));
                halfTime();
                updateClock();
                c.quickVibe(50);
                Handler buttonHandler = new Handler();
                Runnable undoButton = new Runnable() {
                    public void run() {
                        if (topValue / 2 > 100) {
                            halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button));
                        }
                    }
                };
                buttonHandler.postDelayed(undoButton, 100);
            }
        });

        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockToggle(false);
                millis = topValue;
                updateClock();
                c.quickVibe(50);
                animateBar.cancel();
                sendBarToStart();
            }
        });

        explosion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView blackYellow = findViewById(R.id.blackYellowBar);
                TextView clockDisplay = findViewById(R.id.hClockDisplay);
                ImageView bar = findViewById(R.id.hProgressBar);

                resetClock();
                clockToggle(false);
                c.quickVibe(50);

                explosion.setVisibility(View.INVISIBLE);
                blackYellow.setVisibility(View.VISIBLE);
                clockDisplay.setVisibility(View.VISIBLE);
                bar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        clockToggle(false);
        super.onBackPressed();
        this.finish();
    }

    private void halfTime() {
        if ((topValue / 2) < 100) {
            explodeScreen();
            return;
        }
        topValue = topValue / 2;
        millis = topValue;

        if (topValue / 2 < 100) {
            halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button_danger));
        }

        sendBarToStart();
    }

    private void explodeScreen() {
        ImageView explosion = findViewById(R.id.massiveExplosion);
        c.quickVibe(1000);
        explosion.setVisibility(View.VISIBLE);
        ImageView playButton = findViewById(R.id.hPlay);
        ImageView resetButton = findViewById(R.id.hReset);
        ImageView blackYellow = findViewById(R.id.blackYellowBar);
        ImageView bar = findViewById(R.id.hProgressBar);
        TextView clockDisplay = findViewById(R.id.hClockDisplay);
        halfTimeButton.setVisibility(View.INVISIBLE);
        playButton.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);
        blackYellow.setVisibility(View.INVISIBLE);
        clockDisplay.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
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
        ImageView playButton = findViewById(R.id.hPlay);
        ImageView resetButton = findViewById(R.id.hReset);
        ImageView abortButton = findViewById(R.id.hAbortButton);
        if (shouldStart) {
            startClock();
            halfTimeButton.setVisibility(View.INVISIBLE);
            playButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            abortButton.setVisibility(View.VISIBLE);
            clockRunning = true;
        } else {
            stopClock();
            halfTimeButton.setVisibility(View.VISIBLE);
            halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button));
            playButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.VISIBLE);
            abortButton.setVisibility(View.INVISIBLE);
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
                    if (topValue / 2 > 100) {
                        halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button_please_press_me));
                    } else {
                        halfTimeButton.setImageDrawable(getDrawable(R.drawable.half_button_danger));
                    }
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
        topValue = highestValue;
        millis = topValue;
        updateClock();
        clockToggle(false);
        sendBarToStart();
    }

    private void sendBarToStart() {
        try {
            reverseAnimateBar.setDuration(0);
            reverseAnimateBar.start();
        } catch (Exception e) {
            Log.println(Log.INFO, "Reset Animation", "No animation found");
        }
    }

    private void animateMeter() {
        animateBar.setDuration(topValue * 2);
        animateBar.start();
    }
}




