package com.example.mustafa.edumn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.mustafa.edumn.Adapters.WaitingGroupRecyclerAdapter;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.Models.GroupResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class WaitingGroupsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewWaitingGroups;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_groups);

        initiate();
    }

    private void initiate() {
        commonViews();
        setTitle("Waiting Groups");
        setWaitingGroupsList();
    }

    private void commonViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefManager = new PrefManager(this);
        if (prefManager.isLogged()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
            View headerView = navigationView.getHeaderView(0);
            TextView navUserEmail = headerView.findViewById(R.id.nav_header_user_email);
            TextView navUserName = headerView.findViewById(R.id.nav_header_user_name);
            TextView navUserSurName = headerView.findViewById(R.id.nav_header_user_surname);
            navUserName.setText(prefManager.getUserName());
            navUserSurName.setText(prefManager.getUserSurname());
            navUserEmail.setText(prefManager.getUserEmail());
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }

        Menu menu = navigationView.getMenu();

        MenuItem tools = menu.findItem(R.id.tools);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ask_question) {
            startActivity(new Intent(this, CreateQuestionActivity.class));
        } else if (id == R.id.nav_categories) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_meeting) {
            startActivity(new Intent(this, MakeMeetingActivity.class));
        } else if (id == R.id.nav_topics) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (id == R.id.nav_grouping) {
            startActivity(new Intent(this, MyGroupsActivity.class));
        } else if (id == R.id.nav_my_answers) {
            startActivity(new Intent(this, MyAnswersActivity.class));
        } else if (id == R.id.nav_logout) {
            logOutDialogBox();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setWaitingGroupsList() {
        recyclerViewWaitingGroups = findViewById(R.id.recycler_view_waiting_groups);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recyclerViewWaitingGroups.setLayoutManager(layoutManager);

        waitingGroupData();

        recyclerViewWaitingGroups.setHasFixedSize(true);

        recyclerViewWaitingGroups.setItemAnimator(new DefaultItemAnimator());
    }

    private void waitingGroupData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Groups are loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();

        Call<GroupResponse> call = apiService.getWaitingGroups(Integer.parseInt(prefManager.getUserID()));
        call.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, retrofit2.Response<GroupResponse> response) {
                progressDialog.dismiss();
                final ArrayList<Group> groupArrayList = response.body().getData();
                WaitingGroupRecyclerAdapter adapter_items = new WaitingGroupRecyclerAdapter(groupArrayList, WaitingGroupsActivity.this, (v, position) -> {

                });
                recyclerViewWaitingGroups.setAdapter(adapter_items);
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("MainActivity", t.toString());
            }
        });
    }

    private void logOutDialogBox() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    prefManager = new PrefManager(WaitingGroupsActivity.this);
                    prefManager.setLogged(false);
                    prefManager.setLogout(true);
                    finish();
                    startActivity(new Intent(WaitingGroupsActivity.this, MainActivity.class));
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
