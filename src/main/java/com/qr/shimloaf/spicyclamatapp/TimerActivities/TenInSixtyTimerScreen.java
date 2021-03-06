package com.qr.shimloaf.spicyclamatapp.TimerActivities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class TenInSixtyTimerScreen extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.gauntlet_timer;
    }

    long millis = 60000;
    boolean clockRunning = false;
    long nextMark;
    int amount;
    CountDownTimer clock;
    TextView switchDisplay;
    ImageView[] pips = new ImageView[9];
    int pipNum = 0;
    ImageView playButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        amount = 10;
        nextMark = 60000 - (60000 / amount);
        playButton = findViewById(R.id.ts_play_button);

        resetClock();

        ImageView resetButton = findViewById(R.id.ts_reset_button);
        switchDisplay = findViewById(R.id.ts_switch_text);
        switchDisplay.setText("BEGIN");

        if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
            switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
            TextView clockDisplay = findViewById(R.id.ts_time);
            clockDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
        }

        pips[0] = findViewById(R.id.pip1);
        pips[1] = findViewById(R.id.pip2);
        pips[2] = findViewById(R.id.pip3);
        pips[3] = findViewById(R.id.pip4);
        pips[4] = findViewById(R.id.pip5);
        pips[5] = findViewById(R.id.pip6);
        pips[6] = findViewById(R.id.pip7);
        pips[7] = findViewById(R.id.pip8);
        pips[8] = findViewById(R.id.pip9);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
                    switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
                } else {
                    switchDisplay.setTextColor(getResources().getColor(R.color.bright_green, getTheme()));
                }
                switchDisplay.setText("BEGIN");
                switchDisplay.setVisibility(View.VISIBLE);

                playButton.setImageDrawable(getDrawable(R.drawable.play_button_small));

                resetClock();
                resetPips();
                nextMark = 60000 - (60000 / amount);

            }
        });
        resetButton.setOnTouchListener((c.setButtonEffectListener(resetButton)));

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (millis < 100) {
                    resetClock();
                    resetPips();

                    if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
                        switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
                    } else {
                        switchDisplay.setTextColor(getResources().getColor(R.color.bright_green, getTheme()));
                    }

                    nextMark = 60000 - (60000 / amount);
                }
                clockToggle(!clockRunning);
                if (clockRunning) {
                    playButton.setImageDrawable(getDrawable(R.drawable.pause_button_small));
                    switchDisplay.setVisibility(View.INVISIBLE);
                } else {
                    playButton.setImageDrawable(getDrawable(R.drawable.play_button_small));

                    if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
                        switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
                    } else {
                        switchDisplay.setTextColor(getResources().getColor(R.color.bright_green, getTheme()));
                    }

                    switchDisplay.setText("RESUME");
                    switchDisplay.setVisibility(View.VISIBLE);
                }
            }
        });
        playButton.setOnTouchListener((c.setButtonEffectListener(playButton)));
    }

    @Override
    public void onBackPressed() {
        clockToggle(false);
        super.onBackPressed();
        this.finish();
    }

    private void updateClock() {
        TextView clockDisplay = findViewById(R.id.ts_time);

        String time = Long.toString(millis);
        if (millis >= 10000) {
            time = time.substring(0, 2) + "." + time.substring(2, 4);
        } else if (millis > 1000) {
            time = "0" + time.substring(0, 1) + "." + time.substring(1, 3);
        } else if (millis > 100) {
            time = "00." + time.substring(0, 2);
        } else {
            time = "TIME!";
        }

        if (millis <= nextMark && millis - (60000 / amount) < 100) {
            updatePips();
            flashMessage("LAST ONE");
            nextMark = millis - (60000 / amount);
        } else if (millis <= nextMark && nextMark > 100) {
            updatePips();
            flashMessage("SWITCH!!!");
            nextMark = millis - (60000 / amount);
        }
        clockDisplay.setText(time);
    }

    private void flashMessage(String s) {
        switchDisplay.setText(s);
        if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
            switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
        } else {
            switchDisplay.setTextColor(getResources().getColor(R.color.bright_red, getTheme()));
        }
        switchDisplay.setVisibility(View.VISIBLE);

        Handler flashHandler = new Handler();
        Runnable flashRun = new Runnable() {
            public void run() {
                if (nextMark != 60000 - (60000 / amount) && clockRunning) {
                    switchDisplay.setVisibility(View.INVISIBLE);
                }
            }
        };
        flashHandler.postDelayed(flashRun, 2500);
    }

    private void updatePips() {
        if (pipNum > 8) {
            pipNum = 0;
        }
        if (millis < 100) {
            for (int n = 0; n < 9; n++) {
                if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
                    pips[n].setImageDrawable(getDrawable(R.drawable.magenta_pip_bright));
                } else {
                    pips[n].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.green_pip_bright));
                }
            }
        } else {
            pips[pipNum].setImageDrawable(getDrawable(R.drawable.red_pip_bright));
        }

        pipNum++;
    }

    private void resetPips() {
        pipNum = 0;
        for (int n = 0; n < 9; n++) {
            pips[n].setImageDrawable(getDrawable(R.drawable.red_pip));
        }
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
                    if ((boolean) c.getSetting(ClamatoUtils.setting.ColorblindMode)) {
                        switchDisplay.setTextColor(getResources().getColor(R.color.magenta_colorblind, getTheme()));
                    } else {
                        switchDisplay.setTextColor(getResources().getColor(R.color.bright_green, getTheme()));
                    }
                    switchDisplay.setText("GOOD JOB");
                    switchDisplay.setVisibility(View.VISIBLE);
                    playButton.setImageDrawable(getDrawable(R.drawable.play_button_small));
                    updatePips();
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




