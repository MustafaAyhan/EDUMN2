package com.example.mustafa.edumn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mustafa.edumn.Models.Topic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String REQ_TAG = "MainActivity";
    private PrefManager prefManager;
    private ArrayList<Topic> topicList = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TopicsRecyclerAdapter adapter_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiate();
    }

    private void initiate() {
        commonViews();

        FloatingActionButton fab = findViewById(R.id.fab);
        prefManager = new PrefManager(this);
        if (prefManager.isLogged()) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, CreateTopicActivity.class));
                    finish();
                }
            });
        } else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutMain);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

        setTopicList();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ask_question) {
            startActivity(new Intent(this, AskQuestionActivity.class));
        } else if (id == R.id.nav_categories) {
            finish();
            startActivity(getIntent());
        } else if (id == R.id.nav_meeting) {
            startActivity(new Intent(this, MakeMeetingActivity.class));
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        } else if (id == R.id.nav_logout) {
            logOutDialogBox();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setTopicList() {
        RecyclerView recycler_view = findViewById(R.id.recycler_view_topics);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);

        topicList = new ArrayList<Topic>();

        topicData();

        adapter_items = new TopicsRecyclerAdapter(topicList, new CustomClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Log.d("position", "Tıklanan Pozisyon:" + position);
                Topic topic = topicList.get(position);
                Intent intent = new Intent(getBaseContext(), QuestionsWithTopicIDActivity.class);
                intent.putExtra("TopicID", topic.getTopicID());
                intent.putExtra("TopicName", topic.getTopicName());
                intent.putExtra("TopicColor", topic.getTopicBackgroundColor());
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "pozisyon:" + " " + position + " " + "Ad:" + topic.getTopicName(), Toast.LENGTH_SHORT).show();
            }
        });
        recycler_view.setHasFixedSize(true);

        recycler_view.setAdapter(adapter_items);

        recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    public void topicData() {
        final String url = getString(R.string.server_connection_home) + "topic/getalltopics";
        RequestQueue queue = Volley.newRequestQueue(this);

        queue.getCache().remove(url);

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject topic = jsonArray.getJSONObject(i);
                                Topic topicToAdd = new Topic();
                                topicToAdd.setTopicID(topic.getInt("TopicID"));
                                topicToAdd.setTopicName(topic.getString("TopicName"));
                                topicToAdd.setTopicDescription(topic.getString("TopicDescription"));
                                topicToAdd.setTopicCreationDate(topic.getString("TopicCreationDate"));
                                topicToAdd.setTopicUserID(topic.getInt("TopicUserID"));
                                topicToAdd.setTopicUserName(topic.getString("TopicUserName"));
                                topicToAdd.setTopicBackgroundColor(topic.getString("TopicBackgroundColor"));
                                topicList.add(topicToAdd);
                            }
                        } catch (JSONException e) {
                            Log.e("Data get issue", e.getMessage(), e);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        req.setTag(REQ_TAG);
        queue.add(req);
    }

    private void refreshItems() {
        topicList.clear();
        topicData();
        adapter_items.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void logOutDialogBox() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefManager = new PrefManager(MainActivity.this);
                        prefManager.setLogged(false);
                        prefManager.setLogout(true);
                        finish();
                        startActivity(getIntent());
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}