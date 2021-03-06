package com.example.mustafa.edumn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.CustomClasses.ProgressGenerator;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Question;
import com.example.mustafa.edumn.Models.QuestionResponse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CreateGroupQuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener {

    private static final int RC_CAMERA = 3000;
    private ActionProcessButton btnProcess;
    private PrefManager prefManager;
    private EditText questionTitle, questionContext;
    private String responseMessage, selectedTopicColor, selectedTopicName, selectedTopicDescription, selectedTopicDate,
            selectedTopicUserName, selectedTopicUserSurName;
    private boolean responseStatus = false;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_question);

        initiate();
    }

    private void initiate() {
        commonViews();
        setTitle("Create a Group Question");

        TextView title = findViewById(R.id.makeQuestionTitle2);
        title.setText(getIntent().getStringExtra("GroupTitle"));

        questionTitle = findViewById(R.id.question_title);
        questionContext = findViewById(R.id.question_context);

        findViewById(R.id.button_pick_image).setOnClickListener(view -> start());

        btnProcess = findViewById(R.id.send_question_btn);
        btnProcess.setProgress(0);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_question_btn:
                submitForm();
                break;
        }
    }

    private boolean validateTitle() {
        if (questionTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a title for the question", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateContext() {
        //if (answerContext.getContentAsHTML().length() == 0) {
        if (questionContext.getText().length() == 0) {
            Toast.makeText(this, "Please enter a context for the question", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

    private void submitForm() {
        if (!validateTitle() || !validateContext())
            return;
        buttonProgress();
    }

    private void buttonProgress() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        btnProcess.setMode(ActionProcessButton.Mode.PROGRESS);

        progressGenerator.start(btnProcess);
        questionTitle.setEnabled(false);
        questionContext.setEnabled(false);
        uploadMultiFile(questionTitle.getText().toString(), questionContext.getText().toString());
    }

    @Override
    public void onComplete() {
        if (!responseStatus) {
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
                    Intent intent = new Intent(getBaseContext(), QuestionsWithGroupIDActivity.class);
                    intent.putExtra("GroupTitle", getIntent().getStringExtra("GroupTitle"));
                    intent.putExtra("GroupAdminUserName", getIntent().getStringExtra("GroupAdminUserName"));
                    intent.putExtra("GroupAdminSurname", getIntent().getStringExtra("GroupAdminSurname"));
                    intent.putExtra("GroupUserCount", getIntent().getIntExtra("GroupUserCount", 0));
                    intent.putExtra("GroupQuestionCount", getIntent().getIntExtra("GroupQuestionCount", 0));
                    intent.putExtra("GroupAnswerCount", getIntent().getIntExtra("GroupAnswerCount", 0));
                    startActivity(intent);
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                    btnProcess.setProgress(100);
                }
            }.start();
        }
        btnProcess.setProgress(0);
        btnProcess.setEnabled(true);
        questionTitle.setEnabled(true);
        questionContext.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_CAMERA) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void captureImage() {
        ImagePicker.cameraOnly().start(this);
    }

    public void start() {
        ImagePicker imagePicker = ImagePicker.create(this)
                .theme(R.style.ImagePickerTheme) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
                .folderMode(true) // set folder mode (false by default)
                .toolbarArrowColor(Color.RED) // set toolbar arrow up color
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Tap to select") // image selection title
                .origin(images);

        imagePicker.limit(10) // max images can be selected (99 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .imageFullDirectory(Environment.getExternalStorageDirectory().getPath()) // can be full path
                .start(); // start image picker activity with request code
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = (ArrayList<Image>) ImagePicker.getImages(data);
            printImages(images);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printImages(List<Image> images) {
        if (images == null) return;

        LinearLayout imageGallery = findViewById(R.id.selected_photos_container);
        imageGallery.removeAllViews();
        for (int i = 0, l = images.size(); i < l; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(lp);
            imageView.setImageURI(Uri.parse(images.get(i).getPath()));
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(CreateGroupQuestionActivity.this, FullScreenImage.class);

                imageView.buildDrawingCache();
                Bitmap image = imageView.getDrawingCache();

                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", image);
                intent.putExtras(extras);
                startActivity(intent);
            });
            imageGallery.addView(imageView);
        }
    }

    private void uploadMultiFile(String questionTitle, String questionContext) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading...");
        progressDialog.setTitle("Question is processing");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Question question = new Question();
        question.setQuestionTitle(questionTitle);
        question.setQuestionContext(questionContext);
        question.setQuestionDate(date);
        question.setQuestionAskedUserID(Integer.parseInt(prefManager.getUserID()));
        question.setQuestionIsClosed(false);
        question.setQuestionIsPrivate(true);

        MultipartBody.Part[] parts = new MultipartBody.Part[images.size()];
        for (int index = 0; index < images.size(); index++) {
            File file = new File(images.get(index).getPath());
            RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
            parts[index] = MultipartBody.Part.createFormData("Image" + index, file.getName(), surveyBody);
        }
        RequestBody title = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionTitle());
        RequestBody context = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionContext());
        RequestBody questionDate = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionDate());
        RequestBody isClosed = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionIsClosed().toString());
        RequestBody isPrivate = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionIsPrivate().toString());
        RequestBody askedUserID = RequestBody.create(MediaType.parse("text/plain"), question.getQuestionAskedUserID().toString());
        RequestBody topicID = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody groupID = RequestBody.create(MediaType.parse("text/plain"), getIntent().getStringExtra("GroupID"));

        Call<QuestionResponse> call = apiService.createQuestionImage(parts, title, context, questionDate, isClosed, isPrivate, askedUserID, topicID, groupID);

        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(Call<QuestionResponse> call, retrofit2.Response<QuestionResponse> response) {
                if (response.body().getStatus() == 1) {
                    responseStatus = true;
                    responseMessage = "Question has been created successfully";
                } else {
                    responseMessage = response.body().getMessage();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<QuestionResponse> call, Throwable t) {
                Toast.makeText(CreateGroupQuestionActivity.this, "Failure", Toast.LENGTH_SHORT).show();
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
                    prefManager = new PrefManager(CreateGroupQuestionActivity.this);
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
