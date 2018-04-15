package com.example.mustafa.edumn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
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

import com.example.mustafa.edumn.Adapters.QuestionsRecyclerAdapter;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Question;
import com.example.mustafa.edumn.Models.QuestionResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class QuestionsWithGroupIDActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PrefManager prefManager;
    private RecyclerView recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_with_group_id);

        initiate();
    }

    private void initiate() {
        commonViews();
        setTitle(getIntent().getStringExtra("GroupTitle"));

        FloatingActionButton fab = findViewById(R.id.fab);
        prefManager = new PrefManager(this);
        if (prefManager.isLogged()) {
            fab.setOnClickListener(view -> {
                Intent intent = new Intent(getBaseContext(), CreateGroupQuestionActivity.class);
                String groupTitle = getIntent().getStringExtra("GroupTitle");
                intent.putExtra("GroupTitle", groupTitle);
                intent.putExtra("GroupAdminUserName", getIntent().getStringExtra("GroupAdminUserName"));
                intent.putExtra("GroupAdminSurname", getIntent().getStringExtra("GroupAdminSurname"));
                intent.putExtra("GroupUserCount", getIntent().getIntExtra("GroupUserCount", 0));
                intent.putExtra("GroupQuestionCount", getIntent().getIntExtra("GroupQuestionCount", 0));
                intent.putExtra("GroupAnswerCount", getIntent().getIntExtra("GroupAnswerCount", 0));
                startActivity(intent);
            });
        } else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }

        TextView groupTitle = findViewById(R.id.group_title);
        TextView userName = findViewById(R.id.group_user_name);
        TextView surName = findViewById(R.id.group_user_surname);
        TextView userCount = findViewById(R.id.group_user_count);
        TextView questionCount = findViewById(R.id.question_count);
        TextView answerCount = findViewById(R.id.answer_count);
        TextView description = findViewById(R.id.group_description);

        groupTitle.setText(getIntent().getStringExtra("GroupTitle"));
        userName.setText(getIntent().getStringExtra("GroupAdminUserName"));
        surName.setText(getIntent().getStringExtra("GroupAdminSurname"));
        if (getIntent().getIntExtra("GroupUserCount", 0) > 0)
            userCount.setText(getIntent().getIntExtra("GroupUserCount", 0) + " Users");
        else
            userCount.setText(getIntent().getIntExtra("GroupUserCount", 0) + " User");
        if (getIntent().getIntExtra("GroupQuestionCount", 0) > 0)
            questionCount.setText(getIntent().getIntExtra("GroupQuestionCount", 0) + " Questions");
        else {
            questionCount.setText("0 Question");
            TextView noQuestion = findViewById(R.id.no_group_text);
            noQuestion.setVisibility(View.VISIBLE);
        }
        if (getIntent().getIntExtra("GroupAnswerCount", 0) > 0)
            answerCount.setText(getIntent().getIntExtra("GroupAnswerCount", 0) + " Answers");
        else
            answerCount.setText("0 Answer");
        description.setText(getIntent().getStringExtra("GroupDescription"));

        setQuestionList();
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

    private void setQuestionList() {
        recycler_view = findViewById(R.id.recycler_view_questions);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);

        questionData();

        recycler_view.setHasFixedSize(true);

        recycler_view.setItemAnimator(new DefaultItemAnimator());
    }

    private void questionData() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Questions loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();

        Call<QuestionResponse> call = apiService.getGroupQuestions(getIntent().getIntExtra("GroupID", 0));

        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, retrofit2.Response<QuestionResponse> response) {
                progressDialog.dismiss();
                final ArrayList<Question> questions = response.body().getQuestions();

                if (questions.size() > 0) {
                    for (int i = 0; i < questions.size(); i++) {
                        questions.get(i).setQuestionTopicColor("#4eb5a2");
                    }
                    QuestionsRecyclerAdapter adapter_items = new QuestionsRecyclerAdapter(questions, (v, position) -> {
                        Question question = questions.get(position);
                        Intent intent = new Intent(getBaseContext(), SingleQuestionActivity.class);
                        intent.putExtra("QuestionID", question.getQuestionID());
                        intent.putExtra("QuestionTitle", question.getQuestionTitle());
                        intent.putExtra("QuestionContext", question.getQuestionContext());
                        intent.putExtra("QuestionAskedUserName", question.getQuestionAskedUserName());
                        intent.putExtra("QuestionAskedUserSurname", question.getQuestionAskedUserSurName());
                        intent.putExtra("QuestionDate", question.getQuestionDate());
                        intent.putExtra("QuestionIsClosed", question.getQuestionIsClosed());
                        intent.putExtra("QuestionIsPrivate", question.getQuestionIsPrivate());
                        intent.putExtra("QuestionColor", question.getQuestionTopicColor());
                        intent.putExtra("TopicName", getIntent().getStringExtra("GroupTitle"));
                        intent.putExtra("AnswerCount", question.getAnswerCount());
                        if (question.getImagePaths().size() > 0)
                            intent.putExtra("Images", question.getImagePaths());
                        startActivity(intent);
                    });
                    recycler_view.setAdapter(adapter_items);
                } else {
                    TextView textView = findViewById(R.id.no_group_text);
                    textView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("QuestionWithTopicID: ", t.toString());
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
                    prefManager.setLogged(false);
                    prefManager.setLogout(true);
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}
