package com.example.mustafa.edumn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.CustomClasses.ProgressGenerator;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.Models.GroupResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CreateGroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener {

    private PrefManager prefManager;
    private EditText groupTitle, groupDescription;
    private AutoCompleteTextView userEmails;
    private ActionProcessButton btnProcess;
    private boolean validateEmails = false, formStatus = false;
    private String responseMessage = "";

    private List<String> emails = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initiate();

        setTitle("Create A Group");
    }

    private void initiate() {
        commonViews();

        groupTitle = findViewById(R.id.group_title);
        groupDescription = findViewById(R.id.group_description);
        userEmails = findViewById(R.id.user_emails);
        Button checkEmailButton = findViewById(R.id.check_emails);
        checkEmailButton.setOnClickListener(this);

        btnProcess = findViewById(R.id.send_group_btn);
        btnProcess.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_emails:
                if (validateEmails) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("Do you want to edit emails?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            (dialog, id) -> {
                                userEmails.setEnabled(true);
                                validateEmails = false;
                            });

                    builder1.setNegativeButton(
                            "No",
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    checkEmails();
                }
                break;
            case R.id.send_group_btn:
                submitForm();
                break;
            default:
                break;
        }
    }

    @Override
    public void onComplete() {
        if (!formStatus) {
            Toast.makeText(this, "Failed: " + responseMessage, Toast.LENGTH_SHORT).show();
            new CountDownTimer(1200, 500) {
                public void onFinish() {
                    btnProcess.setProgress(0);
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                    btnProcess.setProgress(-1);
                }
            }.start();
        } else {
            Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show();
            new CountDownTimer(2200, 500) {
                public void onFinish() {
                    startActivity(new Intent(CreateGroupActivity.this, MyGroupsActivity.class));
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                    btnProcess.setProgress(100);
                }
            }.start();
        }
        btnProcess.setProgress(0);
        btnProcess.setEnabled(true);
        groupTitle.setEnabled(true);
        groupDescription.setEnabled(true);
        userEmails.setEnabled(true);
    }

    private void checkEmails() {
        emails.clear();
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Emails are checking");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String[] separatedEmails = userEmails.getText().toString().split(",");
        emails.addAll(Arrays.asList(separatedEmails));
        emails.add("");

        Call<GroupResponse> call = apiService.checkEmails(emails);
        call.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, retrofit2.Response<GroupResponse> response) {
                progressDialog.dismiss();
                if (response.body().getStatus() == 1) {
                    validateEmails = true;
                    userEmails.setEnabled(false);
                }
                Toast.makeText(CreateGroupActivity.this, "Message: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                Toast.makeText(CreateGroupActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitForm() {
        if (!validateTitle()) {
            return;
        }
        if (!validateEmails) {
            Toast.makeText(this, "Please check entered emails", Toast.LENGTH_SHORT).show();
            return;
        }
        buttonProgress();
    }

    private boolean validateTitle() {
        if (groupTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a group title!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buttonProgress() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(CreateGroupActivity.this);
        btnProcess.setMode(ActionProcessButton.Mode.PROGRESS);

        progressGenerator.start(btnProcess);

        groupTitle.setEnabled(false);
        groupDescription.setEnabled(false);
        userEmails.setEnabled(false);
        sendData(groupTitle.getText().toString(), groupDescription.getText().toString(),
                Integer.parseInt(prefManager.getUserID()), emails);
    }

    private void sendData(String groupTitle, String groupDescription, int groupAdmin, List<String> emails) {
        Group group = new Group(groupTitle, groupDescription,
                groupAdmin, emails);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Emails are checking");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<GroupResponse> call = apiService.createGroup(group);
        call.enqueue(new Callback<GroupResponse>() {
            @Override
            public void onResponse(Call<GroupResponse> call, retrofit2.Response<GroupResponse> response) {
                progressDialog.dismiss();
                if (response.body().getStatus() == 1)
                    formStatus = true;
                responseMessage = response.body().getMessage();
            }

            @Override
            public void onFailure(Call<GroupResponse> call, Throwable t) {
                Toast.makeText(CreateGroupActivity.this, "Failure", Toast.LENGTH_SHORT).show();
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
                    finish();
                    startActivity(getIntent());
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
