package com.example.mustafa.edumn;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateTopicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener{

    private Button colorPicker;
    private TextView colorText;
    private EditText topicTitle, topicDescription;
    private PrefManager prefManager;
    private ActionProcessButton btnProcess;
    private boolean formStatus = false;
    private String responseMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_topic);

        initiate();
    }

    private void initiate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem tools = menu.findItem(R.id.tools);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearance44), 0, s.length(), 0);
        tools.setTitle(s);
        navigationView.setNavigationItemSelectedListener(this);

        prefManager = new PrefManager(this);
        if (prefManager.isLogged()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }

        colorPicker = findViewById(R.id.colorPickerButton);
        colorPicker.setOnClickListener(this);

        colorText = findViewById(R.id.colorText);

        topicTitle = (EditText) findViewById(R.id.topic_title);
        topicDescription = (EditText) findViewById(R.id.topic_description);

        // get the button view
        btnProcess = (ActionProcessButton) findViewById(R.id.send_topic_btn);

        //to test the animations, when we touch the button it will start counting
        btnProcess.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        } else if (id == R.id.nav_logout) {
            logOutDialogBox();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logOutDialogBox() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefManager.setLogged(false);
                        prefManager.setLogout(true);
                        finish();
                        startActivity(new Intent(CreateTopicActivity.this, MainActivity.class));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.colorPickerButton:
                colorPick();
                break;
            case R.id.send_topic_btn:
                submitForm();
                break;
        }
    }

    private void colorPick() {
        final ColorPicker cp = new ColorPicker(this, 64, 64, 255);
        /* Show color picker dialog */
        cp.show();

        /* Set a new Listener called when user click "select" */
        cp.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                // Do whatever you want
                // Examples
                Log.d("Red", Integer.toString(Color.red(color)));
                Log.d("Green", Integer.toString(Color.green(color)));
                Log.d("Blue", Integer.toString(Color.blue(color)));

                colorText.setText(String.format("#%06X", (0xFFFFFF & color)));
                ///
                /// colorPicker.setBackgroundColor("#%06X", (0xFFFFFF & color);
                cp.cancel();
            }
        });
    }

    private void submitForm() {
        if (!validateTopic()) {
            return;
        }
        buttonProgress();
    }

    private boolean validateTopic() {
        if (topicTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a topic title!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void buttonProgress() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(CreateTopicActivity.this);
        btnProcess.setMode(ActionProcessButton.Mode.PROGRESS);

        progressGenerator.start(btnProcess);
        btnProcess.setEnabled(false);
        topicDescription.setEnabled(false);
        colorPicker.setEnabled(false);
        sendData(topicTitle.getText().toString(), topicDescription.getText().toString(),
                    colorText.getText().toString());
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
        }
        else {
            Toast.makeText(this, responseMessage, Toast.LENGTH_SHORT).show();
            new CountDownTimer(2200, 500) {
                public void onFinish() {
                    startActivity(new Intent(CreateTopicActivity.this, MainActivity.class));
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                    btnProcess.setProgress(100);
                }
            }.start();
        }
        btnProcess.setProgress(0);
        btnProcess.setEnabled(true);
        topicTitle.setEnabled(true);
        topicDescription.setEnabled(true);
        colorPicker.setEnabled(true);
    }

    private void sendData(String topicName, String description, String topicColor) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = getString(R.string.server_connection_home) + "topic/createnewtopic";

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        JSONObject params = new JSONObject();
        try {
            params.put("TopicName", topicName);
            params.put("TopicDescription", description);
            params.put("TopicCreationDate", date);
            params.put("TopicBackgroundColor", topicColor);
            params.put("TopicUserID", prefManager.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("Message");
                            if (response.getString("Status").equals("1")) {
                                formStatus = true;
                                responseMessage = "Topic has been created";
                            } else if (response.getString("Status").equals("0")) {
                                responseMessage = message;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);
    }
}
