package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;


public class SuggestionScreen extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    private void setUpTypes() {

        ButtonData emotionButton = new ButtonData(R.drawable.emotion_button, R.array.emotions, "Emotion");
        ButtonData locationButton = new ButtonData(R.drawable.location_button, R.array.location, "Location");
        ButtonData householdButton = new ButtonData(R.drawable.household_button, R.array.household, "Household Object");
        ButtonData eventButton = new ButtonData(R.drawable.event_button, R.array.event, "Life Event");
        ButtonData grabBagButton = new ButtonData(R.drawable.grab_bag_button, R.array.grab_bag, "Grab Bag");
        ButtonData relationshipButton = new ButtonData(R.drawable.relationship_button, R.array.relationship, "Relationship");
        ButtonData duoButton = new ButtonData(R.drawable.duo_button, R.array.duo, "Famous Duo");
        ButtonData ssugButton = new ButtonData(R.drawable.ssugg_button, R.array.ssuggs, "Suggestion Types");

        types.put(sType.emotion, emotionButton);
        types.put(sType.location, locationButton);
        types.put(sType.household, householdButton);
        types.put(sType.event, eventButton);
        types.put(sType.grab_bag, grabBagButton);
        types.put(sType.relationship, relationshipButton);
        types.put(sType.duo, duoButton);
        types.put(sType.ssugg, ssugButton);
    }

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

        c.navigateDrawer(item.getItemId(), getApplicationContext());
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    / Above is all that needs to be modified to add a new type of suggestion.
    / Below is unlikely to change often.
    */

    static class ButtonData {
        Integer drawableId;
        Integer arrayId;
        String name;

        ButtonData(Integer a_drawable, Integer a_array, String a_name) {
            drawableId = a_drawable;
            arrayId = a_array;
            name = a_name;
        }

    }

    enum screenState {
        prime,
        expanded,
        suggDisplay,
        menuUpPrime,
        menuUpExpanded
    }

    NavigationView navigationView;
    NavigationView fullMenu;
    ConstraintLayout rLayout;
    ImageView suggestionButton;
    ImageView retryButton;
    ImageView resetButton;
    TextView suggestionText;
    TextView palName;

    sType currType = sType.emotion;
    screenState status = screenState.prime;

    boolean shouldRandomize = false;

    HashMap<sType, ButtonData> types = new HashMap<>();
    HashMap<sType, Boolean> used = resetUsed();

    class SuggestionGesture extends GestureDetector.SimpleOnGestureListener {

        public boolean onSingleTapUp(MotionEvent e) {
            c.quickVibe(50);
            if (status == screenState.expanded) {
                shouldRandomize = true;
                changeScreen(screenState.suggDisplay);
            } else if (status == screenState.prime) {
                changeScreen(screenState.expanded);
            }
            return true;
        }

        public boolean onDoubleTap(MotionEvent e) {
            return onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            c.quickVibe(50);
            if (status == screenState.prime) {
                changeScreen(screenState.menuUpPrime);
            } else {
                changeScreen(screenState.menuUpExpanded);
            }
        }

    }

    class smallSuggestionGesture extends GestureDetector.SimpleOnGestureListener {

        Context cText;
        ImageView button;

        smallSuggestionGesture(Context a_cText, ImageView pButton) {
            cText = a_cText;
            button = pButton;
        }

        public boolean onSingleTapUp (MotionEvent e) {
            c.quickVibe(50);
            changeScreen(screenState.suggDisplay);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            String type = types.get(currType).name;
            Toast.makeText(cText, type, Toast.LENGTH_SHORT).show();
            c.quickVibe(50);
        }

    }

    protected int getLayoutResourceId() {
        return R.layout.activity_suggestion;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        fullMenu = findViewById(R.id.full_menu);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);
        fullMenu.setNavigationItemSelectedListener(this);
        setUpTypes();

        suggestionText = findViewById(R.id.suggestion_text);
        palName = findViewById(R.id.pal_name);

        final GestureDetector sButtonHandler = new GestureDetector(this, new SuggestionGesture());

        rLayout = findViewById(R.id.main);
        rLayout.setOnClickListener(new View.OnClickListener() {
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

        suggestionButton = findViewById(R.id.suggestion_button);
        suggestionButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    if (status == screenState.prime) {
                        suggestionButton.setImageResource(R.drawable.question_button_depressed);
                    } else if (status == screenState.expanded) {
                        suggestionButton.setImageResource(R.drawable.happy_button_depressed);
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    if (status == screenState.prime) {
                        suggestionButton.setImageResource(R.drawable.question_button);
                    } else if (status == screenState.expanded) {
                        suggestionButton.setImageResource(R.drawable.happy_button);
                    }
                }
                sButtonHandler.onTouchEvent(event);
                return true;
            }
        });

        retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSuggestionContent();
            }
        });

        retryButton.setOnTouchListener((c.setButtonEffectListener(retryButton)));

        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeScreen(screenState.prime);
            }
        });
        resetButton.setOnTouchListener((c.setButtonEffectListener(resetButton)));

        ImageView[] miniButtons = getMiniButtons();
        for (final ImageView miniButton : miniButtons) {
            final GestureDetector miniHandler = new GestureDetector(this, new smallSuggestionGesture(this, miniButton));
            miniButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        miniButton.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.tan_depressed), android.graphics.PorterDuff.Mode.MULTIPLY);
                    } else if (action == MotionEvent.ACTION_UP) {
                        miniButton.clearColorFilter();
                    }

                    currType = (sType) miniButton.getTag();
                    return miniHandler.onTouchEvent(event);
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (status == screenState.expanded || status == screenState.suggDisplay || status == screenState.menuUpExpanded) {
            changeScreen(screenState.prime);
        } else if (status == screenState.menuUpPrime) {
            changeScreen(screenState.expanded);
        } else if (!backPressed) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        backPressed = true;
    }

    private void setSuggestionContent() {

        if (shouldRandomize) {
            ArrayList<sType> possibleTypes = new ArrayList<>(EnumSet.allOf(sType.class));
            currType = possibleTypes.get((int) (Math.random() * possibleTypes.size()));
        }

        String[] possibleSuggestions = getResources().getStringArray(types.get(currType).arrayId);
        suggestionText.setText(String.format("%s!", possibleSuggestions[(int) (Math.random() * possibleSuggestions.length)]));
    }

    private void changeScreen(screenState next) {

        ImageView[] images = getMiniButtons();

        if (next != screenState.suggDisplay && shouldRandomize) {
            shouldRandomize = false;
        }

        clearFullMenu();
        if (next != screenState.suggDisplay) {
            suggestionButton.setVisibility(View.VISIBLE);
        } else {
            suggestionButton.setVisibility(View.INVISIBLE);
        }

        if (next == screenState.expanded || next == screenState.menuUpExpanded) {
            suggestionButton.setImageResource(R.drawable.happy_button);
            for (ImageView i : images) {
                i.setVisibility(View.VISIBLE);
            }
        } else {
            suggestionButton.setImageResource(R.drawable.question_button);
            animateButtons(false);
            if (next != screenState.prime) {
                for (ImageView i : images) {
                    i.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (next == screenState.suggDisplay) {
            suggestionText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.VISIBLE);
        } else {
            suggestionText.setVisibility(View.INVISIBLE);
            retryButton.setVisibility(View.INVISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
        }

        if (next == screenState.prime || next == screenState.expanded) {
            palName.setVisibility(View.VISIBLE);
            if (next == screenState.expanded) {

                String greeting = "Hi, my name's " + c.getSetting(ClamatoUtils.setting.PalNickname) + "!";
                String tip = "Click me for a random suggestion!";

                int seed = (int) (Math.random() * 4);

                if (seed == 1) {
                    tip = "Click a smaller button for a suggestion of that type!";
                } else if (seed == 2) {
                    tip = "Long press me to choose from a list of all types!";
                } else if (seed == 3) {
                    if (c.getSetting(ClamatoUtils.setting.PalNickname).equals("Suggestion Pal")) {
                        tip = "You can change my name in the settings menu!";
                    }
                }

                seed = (int) (Math.random() * 3);

                if (seed == 1) {
                    greeting = "Howdy, it's me, " + c.getSetting(ClamatoUtils.setting.PalNickname) + "!";
                } else if (seed == 2) {
                    greeting = c.getSetting(ClamatoUtils.setting.PalNickname) + "'s my name, don't wear it out!";
                }

                palName.setText(greeting + "\n" + tip);

            } else {
                palName.setText("Don't be shy, click it!");
            }
        } else {
            palName.setVisibility(View.INVISIBLE);
        }

        if (next == screenState.menuUpExpanded || next == screenState.menuUpPrime) {
            fullMenu.setVisibility(View.VISIBLE);
        } else {
            fullMenu.setVisibility(View.INVISIBLE);
        }

        //Extra state changes
        used = resetUsed();
        if (next == screenState.prime) {
            suggestionButton.setImageResource(R.drawable.question_button);
        } else if (next == screenState.expanded) {
            for (ImageView i : images) {
                assignImageToButton(i);
            }
            if (status == screenState.prime) {
                animateButtons(true);
            }
            suggestionButton.setImageResource(R.drawable.happy_button);
        } else if (next == screenState.suggDisplay) {
            setSuggestionContent();
        }

        status = next;
    }

    private void assignImageToButton(ImageView image) {

        ArrayList<sType> possibleTypes = new ArrayList<>();

        for (sType suggType : EnumSet.allOf(sType.class)) {
            if (used.get(suggType) != null && !used.get(suggType)) {
                possibleTypes.add(suggType);
            }
        }

        int pos = (int) (Math.random() * possibleTypes.size());
        image.setImageResource(types.get(possibleTypes.get(pos)).drawableId);
        image.setTag(possibleTypes.get(pos));
        used.put(possibleTypes.get(pos), true);
    }

    private HashMap<sType,Boolean> resetUsed() {
        HashMap<sType, Boolean> ret = new HashMap<>();
        for (sType suggType : EnumSet.allOf(sType.class)) {
            ret.put(suggType, false);
        }
        return ret;
    }

    private void animateButtons(boolean expand) {

        ImageView[] images = getMiniButtons();

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
                images[n].clearColorFilter();
                AnimatorSet expander = new AnimatorSet();
                ObjectAnimator xAnimation = ObjectAnimator.ofFloat(images[n], "translationX", posX[n]);
                xAnimation.setDuration(500);
                ObjectAnimator yAnimation = ObjectAnimator.ofFloat(images[n], "translationY", posY[n]);
                yAnimation.setDuration(500);
                expander.play(xAnimation).with(yAnimation);
                expander.start();
            }
        } else {
            for (int n = 0; n < 6; n++) {
                images[n].clearColorFilter();
                AnimatorSet closer = new AnimatorSet();
                ObjectAnimator xAnimation = ObjectAnimator.ofFloat(images[n], "translationX", xPos);
                xAnimation.setDuration(500);
                ObjectAnimator yAnimation = ObjectAnimator.ofFloat(images[n], "translationY", yPos);
                yAnimation.setDuration(500);
                closer.play(xAnimation).with(yAnimation);
                closer.start();
            }
        }
    }

    private void clearFullMenu() {
        for (int n = 0; n < used.size(); n++) {
            fullMenu.getMenu().getItem(n).setChecked(false);
        }
    }

    private ImageView[] getMiniButtons() {

        ImageView[] images = new ImageView[6];
        images[0] = findViewById(R.id.option1);
        images[1] = findViewById(R.id.option2);
        images[2] = findViewById(R.id.option3);
        images[3] = findViewById(R.id.option4);
        images[4] = findViewById(R.id.option5);
        images[5] = findViewById(R.id.option6);

        return images;
    }
}




