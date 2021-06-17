package com.qr.shimloaf.spicyclamatapp.TimerActivities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class HalfLifeTimerScreen extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.half_life_timer;
    }

    final long highestValue = 60000;
    long topValue = highestValue;

    long millis = 0;
    boolean clockRunning = false;
    CountDownTimer clock;
    ObjectAnimator animateBar;
    ObjectAnimator reverseAnimateBar;
    ImageView halfTimeButton;
    MediaPlayer mp;
    ImageView explosion;
    ImageView resetButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
            TextView clockDisplay = findViewById(R.id.hClockDisplay);
            clockDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
        }

        halfTimeButton = findViewById(R.id.hHalfButton);
        resetButton = findViewById(R.id.hReset);
        explosion = findViewById(R.id.massiveExplosion);

        resetClock();

        ImageView playButton = findViewById(R.id.hPlay);
        ImageView abortButton = findViewById(R.id.hAbortButton);
        ImageView meter = findViewById(R.id.hProgressBar);

        reverseAnimateBar = ObjectAnimator.ofFloat(meter, "translationY", 0f);
        animateBar = ObjectAnimator.ofFloat(meter, "translationY", 2000f);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.quickRotateImageView(resetButton, 200, false);
                resetClock();
            }
        });
        resetButton.setOnTouchListener((c.setButtonEffectListener(resetButton)));

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (millis > 100) {
                    clockToggle(true);
                    millis = topValue;
                    animateMeter();
                } else {
                    millis = topValue;
                    updateClock();
                    sendBarToStart();
                }
            }
        });
        playButton.setOnTouchListener((c.setButtonEffectListener(playButton)));

        halfTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halfTime();
                updateClock();
            }
        });

        halfTimeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (topValue / 2 > 100) {
                        halfTimeButton.setImageResource(R.drawable.half_button_depressed);
                    } else {
                        halfTimeButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.light_gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                    c.quickVibe(50);
                } else if (action == MotionEvent.ACTION_UP) {
                    (halfTimeButton).clearColorFilter();
                    if (topValue / 2 > 100) {
                        halfTimeButton.setImageResource(R.drawable.half_button);
                    } else if (topValue / 2 < 100) {
                        halfTimeButton.setImageResource(R.drawable.half_button_danger);
                    }
                } else if (action == MotionEvent.ACTION_CANCEL) {
                    (halfTimeButton).clearColorFilter();
                    if (topValue / 2 > 100) {
                        halfTimeButton.setImageResource(R.drawable.half_button);
                    } else if (topValue / 2 < 100) {
                        halfTimeButton.setImageResource(R.drawable.half_button_danger);
                    }
                }

                return halfTimeButton.onTouchEvent(event);
            }
        });

        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockToggle(false);
                millis = topValue;
                updateClock();
                animateBar.cancel();
                sendBarToStart();
            }
        });
        abortButton.setOnTouchListener((c.setButtonEffectListener(abortButton)));

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
        explosion.setOnTouchListener((c.setButtonEffectListener(explosion)));
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
        if (topValue / 2 < 100) {
            halfTimeButton.setImageResource(R.drawable.half_button_danger);
        }
        millis = topValue;
        sendBarToStart();
    }

    private void explodeScreen() {
        mp = MediaPlayer.create(this.getApplicationContext(), R.raw.explosion);
        mp.start();
        c.quickVibe(1000);
        explosion.setVisibility(View.VISIBLE);
        ImageView playButton = findViewById(R.id.hPlay);
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




