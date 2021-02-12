package com.qr.shimloaf.spicyclamatapp.ToolActivities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.io.IOException;

public class BuzzerScreen extends BaseActivity {

    protected int getLayoutResourceId() {
        return R.layout.buzzers;
    }

    public static class BuzzerSliderFragment extends Fragment {

        ImageView buzzer;
        TextView caption;
        ClamatoUtils c;
        MediaPlayer mp;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(
                    R.layout.buzzer_fragment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }

        private void init(final Drawable imageDrawable, final Drawable imageDrawableDepressed, final int soundId, String captionText) {

            c = new ClamatoUtils(getActivity().getApplication());

            try {
                mp = MediaPlayer.create(getActivity().getApplicationContext(), soundId);
            } catch (NullPointerException e) {
                //Don't initialize this fragment not in an outer context, I guess.
            }

            caption = getView().findViewById(R.id.buzzer_text);
            caption.setText("\"" + captionText + "\"");

            buzzer = getView().findViewById(R.id.buzzer);
            buzzer.setImageDrawable(imageDrawable);

            buzzer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buzzer.setImageDrawable(imageDrawableDepressed);
                    mp.stop();
                    try {
                        mp.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    c.quickVibe(50);
                    mp.start();

                    Handler buttonHandler = new Handler();
                    Runnable depressed = new Runnable() {
                        public void run() {
                            buzzer.setImageDrawable(imageDrawable);
                        }
                    };
                    buttonHandler.postDelayed(depressed, 100);

                }
            });
        }
    }

    private class BuzzerSliderAdapter extends FragmentStateAdapter {
        public BuzzerSliderAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return new BuzzerSliderFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

    }

    final int NUM_PAGES = 5;
    ViewPager2 mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPager = findViewById(R.id.pager);
        BuzzerSliderAdapter pagerAdapter = new BuzzerSliderAdapter(this);
        mPager.setAdapter(pagerAdapter);
        for (int n = 0; n < NUM_PAGES; n++) {
            pagerAdapter.createFragment(n);
        }

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                BuzzerSliderFragment currFragment = (BuzzerSliderFragment) getSupportFragmentManager().findFragmentByTag("f" + mPager.getCurrentItem());

                if (currFragment != null) {
                    if (position == 0) {
                        currFragment.init(getDrawable(R.drawable.big_red_button), getDrawable(R.drawable.big_red_button_depressed), R.raw.air_horn, "The Classic Buzzer");
                    } else if (position == 1) {
                        currFragment.init(getDrawable(R.drawable.big_red_button), getDrawable(R.drawable.big_red_button_depressed), R.raw.ding, "For Whom the Bell Tolls");
                    } else if (position == 2) {
                        currFragment.init(getDrawable(R.drawable.big_red_button), getDrawable(R.drawable.big_red_button_depressed), R.raw.buzzer, "Whose Buzzer Is This Anyway?");
                    } else if (position == 3) {
                        currFragment.init(getDrawable(R.drawable.big_red_button), getDrawable(R.drawable.big_red_button_depressed), R.raw.that_was_easy, "Not Sponsored...(Yet)?");
                    } else if (position == 4) {
                        currFragment.init(getDrawable(R.drawable.big_red_button), getDrawable(R.drawable.big_red_button_depressed), R.raw.fart, "The Finest Vintage");
                    }
                }

                super.onPageSelected(position);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }
}




