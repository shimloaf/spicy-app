package com.qr.shimloaf.spicyclamatapp.ToolActivities;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

public class BuzzerScreen extends AppCompatActivity {

    public static class BuzzerSliderFragment extends Fragment {

        ImageView buzzer;
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

        private void init(Drawable imageDrawable, int soundId) {

            c = new ClamatoUtils(getActivity().getApplication());

            try {
                mp = MediaPlayer.create(getActivity().getApplicationContext(), soundId);
            } catch (NullPointerException e) {
                //Don't initialize this fragment not in an outer context, I guess.
            }

            buzzer = getView().findViewById(R.id.buzzer);
            buzzer.setImageDrawable(imageDrawable);

            buzzer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c.quickVibe(50);
                    mp.start();
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
    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzzers);
        c = new ClamatoUtils(this.getApplication());
        mPager = findViewById(R.id.pager);
        BuzzerSliderAdapter pagerAdapter = new BuzzerSliderAdapter(this);
        mPager.setAdapter(pagerAdapter);
        for (int n = 0; n < NUM_PAGES; n++) {
            pagerAdapter.createFragment(n);
        }

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                BuzzerSliderFragment currFragment = (BuzzerSliderFragment) getSupportFragmentManager().findFragmentByTag("f" + mPager.getCurrentItem());
                if (position == 0) {
                    currFragment.init(getDrawable(R.drawable.duo_button), R.raw.buzzer);
                } else if (position == 1) {
                    currFragment.init(getDrawable(R.drawable.clamato_icon), R.raw.ding);
                } else if (position == 2) {
                    currFragment.init(getDrawable(R.drawable.clamato_icon), R.raw.that_was_easy);
                } else if (position == 3) {
                    currFragment.init(getDrawable(R.drawable.clamato_icon), R.raw.air_horn);
                } else if (position == 4) {
                    currFragment.init(getDrawable(R.drawable.clamato_icon), R.raw.fart);
                }
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
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




