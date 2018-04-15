package com.example.mustafa.edumn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mustafa.edumn.Adapters.MyGroupsRecyclerAdapter;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.Models.GroupResponse;

import org.json.JSONException;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MyGroupsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PrefManager prefManager;
    private RecyclerView recycler_view;
    private String responseMessage;
    private boolean invitationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        initiate();

        setTitle("My Groups");
    }

    private void initiate() {
        commonViews();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> startActivity(new Intent(this, CreateGroupActivity.class)));

        setGroupList();
        checkWaitingGroupCount();
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
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            View headerView = navigationView.getHeaderView(0);
            LinearLayout layoutUserInfo = headerView.findViewById(R.id.nav_header_user_info);
            layoutUserInfo.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_waiting_groups) {
            if (invitationStatus) {
                startActivity(new Intent(this, WaitingGroupsActivity.class));
            } else
                Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
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

    private void checkWaitingGroupCount() {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.server_connection_home) + "group/getwaitinggroupcount/" + prefManager.getUserID();

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    // display response
                    try {

                        if (response.getString("Status").equals("1")) {
                            int invitationCount = Integer.parseInt(response.getString("Data"));
                            if (invitationCount > 0) {
                                invitationStatus = true;
                            } else {
                                invitationStatus = false;
                                responseMessage = "There isn' t any invited group";
                            }
                        } else if (response.getString("Status").equals("0")) {
                            responseMessage = response.getString("Message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }

    private void setGroupList() {
        recycler_view = findViewById(R.id.recycler_view_groups);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);

        groupData();

        recycler_view.setHasFixedSize(true);

        recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    private void groupData() {
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

        Call<GroupResponse> call = apiService.getAllGroups(Integer.parseInt(prefManager.getUserID()));
        call.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, retrofit2.Response<GroupResponse> response) {
                progressDialog.dismiss();
                final ArrayList<Group> groupArrayList = response.body().getData();
                MyGroupsRecyclerAdapter adapter_items = new MyGroupsRecyclerAdapter(groupArrayList, (v, position) -> {
                    Group group = groupArrayList.get(position);
                    Intent intent = new Intent(getBaseContext(), QuestionsWithGroupIDActivity.class);
                    intent.putExtra("GroupID", group.getGroupID());
                    intent.putExtra("GroupTitle", group.getGroupTitle());
                    intent.putExtra("GroupDescription", group.getGroupDescription());
                    intent.putExtra("GroupAdminUserName", group.getAdminUserName());
                    intent.putExtra("GroupAdminSurname", group.getAdminSurname());
                    intent.putExtra("GroupQuestionCount", group.getGroupQuestionCount());
                    intent.putExtra("GroupAnswerCount", group.getGroupAnswerCount());
                    intent.putExtra("GroupUserCount", group.getGroupUserCount());
                    startActivity(intent);
                });
                recycler_view.setAdapter(adapter_items);
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
                    prefManager = new PrefManager(MyGroupsActivity.this);
                    prefManager.setLogged(false);
                    prefManager.setLogout(true);
                    finish();
                    startActivity(new Intent(MyGroupsActivity.this, MainActivity.class));
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
