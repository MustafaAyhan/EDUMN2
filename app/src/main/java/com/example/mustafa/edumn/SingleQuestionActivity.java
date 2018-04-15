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
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.example.mustafa.edumn.Adapters.SingleAnswerRecyclerAdapter;
import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.CustomClasses.ProgressGenerator;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.Models.Answer;
import com.example.mustafa.edumn.Models.AnswerResponse;
import com.squareup.picasso.Picasso;

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

public class SingleQuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener {

    private static final int RC_CAMERA = 3000;
    private PrefManager prefManager;
    private RecyclerView recycler_view;
    private EditText answerContext;
    private ActionProcessButton btnProcess;
    private String responseMessage;
    private boolean createStatus = false;
    private SingleAnswerRecyclerAdapter adapter_items;
    private ArrayList<Answer> answers;
    private ArrayList<Image> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_question);

        initiate();
    }

    private void initiate() {
        commonViews();

        TextView topicTitle = findViewById(R.id.topic_title);
        TextView userName = findViewById(R.id.question_user_name);
        TextView userSurname = findViewById(R.id.question_user_surname);
        TextView solvedStatus = findViewById(R.id.solved_status);
        TextView questionTitle = findViewById(R.id.question_title);
        TextView questionDate = findViewById(R.id.question_date);
        TextView questionContext = findViewById(R.id.question_context);
        answerContext = findViewById(R.id.your_answer_context);
        TextView answerCount = findViewById(R.id.answer_count);
        HorizontalScrollView horizontalScrollView = findViewById(R.id.hori_scroll_view);
        horizontalScrollView.setVisibility(View.GONE);

        findViewById(R.id.button_pick_image).setOnClickListener(view -> start());

        questionTitle.setTextColor(Color.parseColor(getIntent().getStringExtra("QuestionColor")));
        topicTitle.setTextColor(Color.parseColor(getIntent().getStringExtra("QuestionColor")));
        topicTitle.setText(getIntent().getStringExtra("TopicName"));
        userName.setText(getIntent().getStringExtra("QuestionAskedUserName"));
        userSurname.setText(getIntent().getStringExtra("QuestionAskedUserSurname"));
        if (getIntent().getIntExtra("AnswerCount", 0) > 0)
            answerCount.setText(getIntent().getIntExtra("AnswerCount", 0) + " Answer");
        else
            answerCount.setText("0 answer");

        String status;
        if (getIntent().getBooleanExtra("QuestionIsClosed", false))
            status = "Question is Closed";
        else
            status = "Question is Open";

        solvedStatus.setText(status);
        questionTitle.setText(getIntent().getStringExtra("QuestionTitle"));
        questionDate.setText(getIntent().getStringExtra("QuestionDate"));
        questionContext.setText(getIntent().getStringExtra("QuestionContext"));

        if (getIntent().hasExtra("Images")) {
            horizontalScrollView.setVisibility(View.VISIBLE);
            printImages(getIntent().getStringArrayListExtra("Images"));
        }

        if (!prefManager.isLogged()) {
            RelativeLayout relativeLayout = findViewById(R.id.answer_layout);
            relativeLayout.setVisibility(View.GONE);
            TextView attachment = findViewById(R.id.attachment_textview);
            attachment.setText("Please login to make an answer");
            attachment.setTextSize(12);
            attachment.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            attachment.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));
        }

        recycler_view = findViewById(R.id.recycler_view_answers);
        getAnswers();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());

        btnProcess = findViewById(R.id.send_answer_btn);
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
            case R.id.send_answer_btn:
                submitForm();
                break;
        }
    }

    private void submitForm() {
        if (!validateContext()) {
            return;
        }
        buttonProgress();
    }

    private boolean validateContext() {
        if (answerContext.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter an answer!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buttonProgress() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(SingleQuestionActivity.this);
        btnProcess.setMode(ActionProcessButton.Mode.PROGRESS);

        progressGenerator.start(btnProcess);
        answerContext.setEnabled(false);
        btnProcess.setEnabled(false);
        sendData(answerContext.getText().toString());
    }

    @Override
    public void onComplete() {
        if (!createStatus) {
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
            finish();
            startActivity(getIntent());
        }
        btnProcess.setProgress(0);
        btnProcess.setEnabled(true);
        answerContext.setEnabled(true);
        btnProcess.setEnabled(true);
    }

    private void getAnswers() {
        TextView answerStatus = findViewById(R.id.answers_exist);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Answers loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();

        int id = getIntent().getIntExtra("QuestionID", 0);
        Call<AnswerResponse> call = apiService.getAllAnswers(id);

        call.enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, retrofit2.Response<AnswerResponse> response) {
                progressDialog.dismiss();
                answers = response.body().getAnswerList();
                if (answers.size() > 0) {
                    answerStatus.setVisibility(View.GONE);
                    adapter_items = new SingleAnswerRecyclerAdapter(answers, (v, position) -> {

                    }, SingleQuestionActivity.this);
                    recycler_view.setAdapter(adapter_items);
                }
            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                // Log error here since request failed
                Log.e("SingleQuestionAct: ", t.toString());
            }
        });
    }

    private void printImages(ArrayList<String> images) {
        if (images == null) return;

        LinearLayout imageGallery = findViewById(R.id.selected_photos_container_single_question);
        imageGallery.removeAllViews();
        for (int i = 0; i < images.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(lp);
            String path = getString(R.string.server_connection) + "/Files/" + images.get(i);
            Picasso.get().load(path).into(imageView);
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(SingleQuestionActivity.this, FullScreenImage.class);

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
            printAnswerImages(images);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void printAnswerImages(List<Image> images) {
        if (images == null) return;

        LinearLayout imageGallery = findViewById(R.id.selected_photos_container_answer_respond);
        imageGallery.removeAllViews();
        for (int i = 0, l = images.size(); i < l; i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(lp);
            imageView.setImageURI(Uri.parse(images.get(i).getPath()));
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(SingleQuestionActivity.this, FullScreenImage.class);

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

    private void sendData(String answerContext) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        // Set up progress before call
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setTitle("Answer sending");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // show it
        progressDialog.show();
        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        Answer answer = new Answer();
        answer.setAnswerContext(answerContext);
        answer.setAnswerDate(date);
        answer.setAnswerRating(5);
        answer.setQuestionID(getIntent().getIntExtra("QuestionID", 0));
        answer.setUserID(Integer.parseInt(prefManager.getUserID()));

        MultipartBody.Part[] parts = new MultipartBody.Part[images.size()];
        for (int index = 0; index < images.size(); index++) {
            File file = new File(images.get(index).getPath());
            RequestBody surveyBody = RequestBody.create(MediaType.parse("image/*"), file);
            parts[index] = MultipartBody.Part.createFormData("Image" + index, file.getName(), surveyBody);
        }
        RequestBody context = RequestBody.create(MediaType.parse("text/plain"), answer.getAnswerContext());
        RequestBody answerDate = RequestBody.create(MediaType.parse("text/plain"), answer.getAnswerDate());
        RequestBody rating = RequestBody.create(MediaType.parse("text/plain"), answer.getAnswerRating().toString());
        RequestBody userID = RequestBody.create(MediaType.parse("text/plain"), answer.getUserID().toString());
        RequestBody questionID = RequestBody.create(MediaType.parse("text/plain"), answer.getQuestionID().toString());

        Call<AnswerResponse> call = apiService.createAnswerWithImages(parts, context, answerDate,
                rating, userID, questionID);

        call.enqueue(new Callback<AnswerResponse>() {
            @Override
            public void onResponse(Call<AnswerResponse> call, retrofit2.Response<AnswerResponse> response) {
                progressDialog.dismiss();
                createStatus = Integer.parseInt(response.body().getStatus()) == 1;
                responseMessage = response.body().getMessage();
            }

            @Override
            public void onFailure(Call<AnswerResponse> call, Throwable t) {
                Toast.makeText(SingleQuestionActivity.this, "Failure", Toast.LENGTH_SHORT).show();
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
                    prefManager = new PrefManager(SingleQuestionActivity.this);
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
