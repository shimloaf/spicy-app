package com.qr.shimloaf.spicyclamatapp.ToolActivities;

import android.content.Intent;
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
        Drawable imageDrawable;
        int soundId;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(
                    R.layout.buzzer_fragment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            buzzer = getView().findViewById(R.id.buzzer);
            init();
            super.onViewCreated(view, savedInstanceState);
        }

        public void setValues(Drawable image, int sound) {
            imageDrawable = image;
            soundId = sound;
        }

        private void init() {

            c = new ClamatoUtils(getActivity().getApplication());

            try {
                mp = MediaPlayer.create(getActivity().getApplicationContext(), soundId);
            } catch (NullPointerException e) {
                //Don't initialize this fragment not in an outer context, I guess.
            }

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
            BuzzerSliderFragment f = new BuzzerSliderFragment();
            f.setValues(getDrawable(R.drawable.green_pip_bright), R.raw.that_was_easy);
            return f;
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }

    }

    final int NUM_PAGES = 5;

    ClamatoUtils c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buzzers);
        c = new ClamatoUtils(this.getApplication());
        ViewPager2 mPager = findViewById(R.id.pager);
        BuzzerSliderAdapter pagerAdapter = new BuzzerSliderAdapter(this);
        pagerAdapter.createFragment(0);
        pagerAdapter.createFragment(1);
        pagerAdapter.createFragment(2);
        pagerAdapter.createFragment(3);
        pagerAdapter.createFragment(4);
        mPager.setAdapter(pagerAdapter);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}




