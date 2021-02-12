package com.qr.shimloaf.spicyclamatapp.MenuActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.qr.shimloaf.spicyclamatapp.R;
import com.qr.shimloaf.spicyclamatapp.Utility.BaseActivity;
import com.qr.shimloaf.spicyclamatapp.Utility.ClamatoUtils;

import java.util.ArrayList;

public class ShowScreen extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected int getLayoutResourceId() {
        return R.layout.activity_showbuilder;
    }

    public static class Team {
        String title;
        ArrayList<Member> members;
    }

    public static class Member {
        String first;
        String nick;
        String last;
        boolean archive;

        public String getData() {
            String data = first + "," + nick + "," + last + ",";
            if (archive) {
                data += "a,";
            } else {
                data += "c,";
            }
            return data;
        }
    }

    Team team;
    EditText title;

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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(5).setChecked(true);

        c = new ClamatoUtils(getApplication());
        team = new Team();
        title = findViewById(R.id.team_name);

        loadTeam();
        setUpTeam();

        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    team.title = String.valueOf(title.getText());
                    saveTeamInfo();
                    title.clearFocus();

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!backPressed) {
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        backPressed = true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        c.navigateDrawer(item.getItemId(), getApplicationContext());
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadTeam() {
        String teamInfo = c.readFromFile("team.txt", "");

        team.members = new ArrayList<>();
        team.title = teamInfo.substring(0, teamInfo.indexOf(','));
        teamInfo = teamInfo.substring(teamInfo.indexOf(',') + 1);
        while (teamInfo.indexOf(',') != -1) {
            Member m = new Member();
            m.first = teamInfo.substring(0, teamInfo.indexOf(','));
            teamInfo = teamInfo.substring(teamInfo.indexOf(',') + 1);
            m.nick = teamInfo.substring(0, teamInfo.indexOf(','));
            teamInfo = teamInfo.substring(teamInfo.indexOf(',') + 1);
            m.last = teamInfo.substring(0, teamInfo.indexOf(','));
            teamInfo = teamInfo.substring(teamInfo.indexOf(',') + 1);
            m.archive = teamInfo.charAt(0) == 'a';
            teamInfo = teamInfo.substring(teamInfo.indexOf(',') + 1);
            team.members.add(m);
        }
    }

    private void saveTeamInfo() {
        String data = team.title + ",";
        for (Member m : team.members) {
            data += m.getData();
        }

        c.writeToFile(data, "team.txt", "");
    }

    public void setUpTeam() {
        title.setText(team.title);
        for (Member m : team.members) {
            //Append a member to the member list
        }
    }

}
