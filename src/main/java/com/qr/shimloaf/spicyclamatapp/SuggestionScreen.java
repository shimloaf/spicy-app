package com.qr.shimloaf.spicyclamatapp;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;


enum sType {
    ssugg,
    location,
    household,
    emotion,
    event,
    grab_bag,
    relationship,
    duo

}

enum screenState {
    prime,
    expanded,
    suggDisplay,
    menuUpPrime,
    menuUpExpanded
}

public class SuggestionScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClamatoUtils c;
    sType currType = sType.emotion;

    static class ButtonData {
        Integer drawableId;
        Integer arrayId;
        String name;
        ButtonData(Integer a_drawable, Integer a_array, String a_name) {
            drawableId = a_drawable;
            arrayId = a_array;
            name = a_name;
        }

        ButtonData(Integer a_array, String a_name) {
            arrayId = a_array;
            name = a_name;
            drawableId = -1;
        }

    }

    /*
    // Managing suggestion types
    */
    HashMap<sType, ButtonData> types = new HashMap<>();
    private void setUpTypes() {

        ButtonData emotionButton = new ButtonData(R.drawable.emotion_button, R.array.emotions, "Emotion");
        ButtonData locationButton = new ButtonData(R.drawable.location_button, R.array.location, "Location");
        ButtonData householdButton = new ButtonData(R.drawable.household_button, R.array.household, "Household Object");
        ButtonData eventButton = new ButtonData(R.drawable.event_button, R.array.event, "Life Event");
        ButtonData grabBagButton = new ButtonData(R.drawable.grab_bag_button, R.array.grab_bag, "Grab Bag");
        ButtonData relationshipButton = new ButtonData(R.drawable.relationship_button, R.array.relationship, "Relationship");
        ButtonData duoButton = new ButtonData(R.drawable.duo_button, R.array.duo, "Famous Duo");
        ButtonData ssugButton = new ButtonData(R.array.ssuggs, "Suggest a Suggestion");

        types.put(sType.emotion, emotionButton);
        types.put(sType.location, locationButton);
        types.put(sType.household, householdButton);
        types.put(sType.event, eventButton);
        types.put(sType.grab_bag, grabBagButton);
        types.put(sType.relationship, relationshipButton);
        types.put(sType.duo, duoButton);
        types.put(sType.ssugg, ssugButton);
    }

    private void setSuggestionContent() {
        TextView suggestion = findViewById(R.id.suggestion_text);
        String c = "";
        String[] possibleSuggestions;
        try {
            possibleSuggestions = getResources().getStringArray(types.get(currType).arrayId);

        } catch (NullPointerException e) {
            Toast.makeText(this, "David Hopping messed up. Tell him that. Code's buggy as f***.", Toast.LENGTH_SHORT).show();
            possibleSuggestions = getResources().getStringArray(R.array.duo);
        }

        if (currType == sType.ssugg) {
            c = "Ask for a suggestion of: ";
        }

        c = c + (possibleSuggestions[(int)(Math.random()*possibleSuggestions.length)]);
        c = c + "!";
        suggestion.setText(c);
    }

    /*
    // State manipulation
    */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationView full_nav = findViewById(R.id.full_menu);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);
        full_nav.setNavigationItemSelectedListener(this);
        c = new ClamatoUtils(this.getApplication());
        setUpTypes();

        final GestureDetector sButtonHandler = new GestureDetector(this, new SuggestionGesture());
        ConstraintLayout rlayout = findViewById(R.id.main);
        rlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == screenState.expanded) {
                    changeScreen(screenState.prime);
                } else if (status == screenState.menuUpPrime) {
                    changeScreen(screenState.prime);
                } else if (status == screenState.menuUpExpanded) {
                    changeScreen(screenState.expanded);
                }
            }
        });
        
        ImageView image = findViewById(R.id.suggestion_button);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return sButtonHandler.onTouchEvent(event);
            }
        });

        ImageView retry = findViewById(R.id.retry_button);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSuggestionContent();
            }
        });

        ImageView reset = findViewById(R.id.reset_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScreen(screenState.prime);
            }
        });

        ImageView[] miniButtons = getButtons();
        final GestureDetector miniHandler = new GestureDetector(this, new smallSuggestionGesture(this));

        for (final ImageView miniButton : miniButtons) {
             miniButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    currType = (sType) miniButton.getTag();
                    return miniHandler.onTouchEvent(event);
                }
            });
        }

    }

    //Back button behavior
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (status == screenState.expanded || status == screenState.suggDisplay || status == screenState.menuUpExpanded){
            changeScreen(screenState.prime);
        } else if (status == screenState.menuUpPrime) {
            changeScreen(screenState.expanded);
        } else {
            super.onBackPressed();
        }
    }

    //Provides full menu interaction and drawer nav
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        //Full Menu Nav
        if (status == screenState.menuUpPrime || status == screenState.menuUpExpanded) {
            if (id == R.id.sugg_duo) {
                currType = sType.duo;
            } else if (id == R.id.sugg_emotion) {
                currType = sType.emotion;
            } else if (id == R.id.sugg_event) {
                currType = sType.event;
            } else if (id == R.id.sugg_grab_bag) {
                currType = sType.grab_bag;
            } else if (id == R.id.sugg_household) {
                currType = sType.household;
            } else if (id == R.id.sugg_location) {
                currType = sType.location;
            } else if (id == R.id.sugg_ssuggs) {
                currType = sType.ssugg;
            } else if (id == R.id.sugg_relationship) {
                currType = sType.relationship;
            }
            changeScreen(screenState.suggDisplay);
        }

        //Nav Drawer
        if (id == R.id.nav_home) {
            Intent appBrowser = new Intent(this, HomeScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_games) {
            Intent appBrowser = new Intent(this, GamerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_timer) {
            Intent appBrowser = new Intent(this, TimerScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_suggestion) {
            changeScreen(screenState.prime);
        } else if (id == R.id.nav_tools) {
            Intent appBrowser = new Intent(this, ToolsScreen.class);
            startActivity(appBrowser);
        } else if (id == R.id.nav_showbuilder) {

        } else if (id == R.id.nav_credits) {
            Intent appBrowser = new Intent(this, CreditsScreen.class);
            startActivity(appBrowser);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Controls main button input
    class SuggestionGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            ImageView suggestionButton = findViewById(R.id.suggestion_button);
            if (status == screenState.prime){
                suggestionButton.setImageResource(R.drawable.question_button_depressed);
            } else if (status == screenState.expanded) {
                suggestionButton.setImageResource(R.drawable.ssugg_button_depressed);
            }
            return true;
        }

        public boolean onFling (MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            ImageView suggestionButton = findViewById(R.id.suggestion_button);
            if (status == screenState.prime) {
                suggestionButton.setImageResource(R.drawable.question_button);
            } else if (status == screenState.expanded) {
                suggestionButton.setImageResource(R.drawable.ssugg_button);
            }
            return true;
        }

        public boolean onSingleTapUp (MotionEvent e) {
            if (status == screenState.expanded) {
                currType = sType.ssugg;
                changeScreen(screenState.suggDisplay);
            } else if (status == screenState.prime){
                changeScreen(screenState.expanded);
            }
            return true;
        }

        public boolean onDoubleTap (MotionEvent e) {

            ArrayList<sType> possibleTypes = new ArrayList<>(EnumSet.allOf(sType.class));
            currType = possibleTypes.get((int)(Math.random() * possibleTypes.size()));
            changeScreen(screenState.suggDisplay);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (status == screenState.prime) {
                changeScreen(screenState.menuUpPrime);
            } else {
                changeScreen(screenState.menuUpExpanded);
            }
        }

    }

    //Controls Small Button Input
    class smallSuggestionGesture extends GestureDetector.SimpleOnGestureListener {

        Context cText;

        smallSuggestionGesture(Context a_cText) {
            cText = a_cText;
        }

        public boolean onSingleTapUp (MotionEvent e) {
            c.quickVibe(50);
            changeScreen(screenState.suggDisplay);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            String type;
            type = types.get(currType).name;
            Toast.makeText(cText, type, Toast.LENGTH_SHORT).show();
            c.quickVibe(50);
        }

    }

    /*
    // State behavior
    */

    //State machine changes
    private void changeScreen(screenState next) {

        NavigationView fullMenu = findViewById(R.id.full_menu);
        ImageView suggestionButton = findViewById(R.id.suggestion_button);
        ImageView[] images = getButtons();
        ImageView retryButton = findViewById(R.id.retry_button);
        ImageView resetButton = findViewById(R.id.reset_button);
        TextView suggestion = findViewById(R.id.suggestion_text);

        //View Visibility Changes and Icon Changes

        clearFullMenu();

        if (next != screenState.suggDisplay) {
            suggestionButton.setVisibility(View.VISIBLE);
        } else {
            suggestionButton.setVisibility(View.INVISIBLE);
        }

        if (next == screenState.expanded || next == screenState.menuUpExpanded) {
            suggestionButton.setImageResource(R.drawable.ssugg_button);
            for (ImageView i : images) {
                i.setVisibility(View.VISIBLE);
            }
        } else if (next != screenState.prime){
            suggestionButton.setImageResource(R.drawable.question_button);
            for (ImageView i : images) {
                i.setVisibility(View.INVISIBLE);
            }
        }

        if (next == screenState.suggDisplay) {
            suggestion.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.VISIBLE);
        } else {
            suggestion.setVisibility(View.INVISIBLE);
            retryButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
        }

        if (next == screenState.menuUpExpanded || next == screenState.menuUpPrime) {
            fullMenu.setVisibility(View.VISIBLE);
        } else {
            fullMenu.setVisibility(View.INVISIBLE);
        }

        //Extra state changes

        if (next == screenState.menuUpExpanded || next == screenState.menuUpPrime) {
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        } else if (next == screenState.expanded && status == screenState.prime) {
            animateButtons(true);
            for (ImageView i: images) {
                assignImageToButton(i);
            }
            suggestionButton.setImageResource(R.drawable.ssugg_button);
        } else if (next == screenState.prime) {
            animateButtons(false);
            suggestionButton.setImageResource(R.drawable.question_button);
            used = resetUsed();
        } else if (next == screenState.suggDisplay) {
            setSuggestionContent();
        }

        status = next;
    }
    screenState status = screenState.prime;

    //Button random selection methods
    HashMap<sType, Boolean> used = resetUsed();
    private ImageView[] getButtons() {

        ImageView[] images = new ImageView[6];
        images[0] = findViewById(R.id.option1);
        images[1] = findViewById(R.id.option2);
        images[2] = findViewById(R.id.option3);
        images[3] = findViewById(R.id.option4);
        images[4] = findViewById(R.id.option5);
        images[5] = findViewById(R.id.option6);

        return images;
    }

    private void assignImageToButton(ImageView image) {

        ArrayList<sType> possibleTypes = new ArrayList<>();

        for (sType suggType : EnumSet.allOf(sType.class)) {
            if (used.get(suggType) != null && !used.get(suggType)) {
                possibleTypes.add(suggType);
            }
        }

        int pos = (int) (Math.random() * possibleTypes.size());
        int imageResource;
        try {
            imageResource = types.get(possibleTypes.get(pos)).drawableId;
        } catch (NullPointerException e) {
            Toast.makeText(this, "David Hopping messed up. Tell him that. Code's buggy as f***.", Toast.LENGTH_SHORT).show();
            imageResource = R.drawable.duo_button;

        }
        image.setImageResource(imageResource);
        image.setTag(possibleTypes.get(pos));
        used.put(possibleTypes.get(pos), true);
    }

    private HashMap<sType,Boolean> resetUsed() {
        HashMap<sType, Boolean> ret = new HashMap<>();
        for (sType suggType : EnumSet.allOf(sType.class)) {
            if (suggType == sType.ssugg) {
                ret.put(suggType, true);
            } else {
                ret.put(suggType, false);
            }
        }
        return ret;
    }

    private void animateButtons(boolean expand) {

        ImageView suggestionButton = findViewById(R.id.suggestion_button);
        ImageView[] images = getButtons();

        float[] posX = new float[6];
        posX[0] = 500f;
        posX[1] = -500f;
        posX[2] = 300f;
        posX[3] = -300f;
        posX[4] = 400f;
        posX[5] = -400f;
        float[] posY = new float[6];
        posY[0] = 0f;
        posY[1] = 0f;
        posY[2] = 350f;
        posY[3] = 350f;
        posY[4] = -350;
        posY[5] = -350f;

        float xPos = suggestionButton.getScaleX();
        float yPos = suggestionButton.getScaleY();

        if (expand) {
            for (int n = 0; n < 6; n++) {
                AnimatorSet expander = new AnimatorSet();
                ObjectAnimator xAnimation = ObjectAnimator.ofFloat(images[n], "translationX", posX[n]);
                xAnimation.setDuration(500);
                ObjectAnimator yAnimation = ObjectAnimator.ofFloat(images[n], "translationY", posY[n]);
                xAnimation.setDuration(500);
                expander.play(xAnimation).with(yAnimation);
                expander.start();
            }
        } else {
            for (ImageView i: images) {
                AnimatorSet closer = new AnimatorSet();
                ObjectAnimator xAnimation = ObjectAnimator.ofFloat(i, "translationX", xPos);
                xAnimation.setDuration(500);
                ObjectAnimator yAnimation = ObjectAnimator.ofFloat(i, "translationY", yPos);
                xAnimation.setDuration(500);
                closer.play(xAnimation).with(yAnimation);
                closer.start();
            }
        }
    }

    private void clearFullMenu() {
        NavigationView full_nav = findViewById(R.id.full_menu);
        for (int n = 0; n < used.size(); n++) {
            full_nav.getMenu().getItem(n).setChecked(false);
        }
    }
}




