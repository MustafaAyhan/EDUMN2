package com.example.mustafa.edumn;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SingleSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.dd.processbutton.iml.ActionProcessButton;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorTextStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskQuestionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener {

    private ActionProcessButton btnProcess;
    private PrefManager prefManager;
    private Editor questionContext;
    private EditText questionTitle;
    private SingleSpinnerSearch questionTopics;
    private String responseMessage = "";
    private int selectedTopicInt;
    private boolean responseStatus = false;

    static final String REQ_TAG = "AskQuestionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        initiate();
    }

    private void initiate() {
        //list = Arrays.asList(getResources().getStringArray(R.array.sports_array));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        fontChange();

        setTitle("Question");

        prefManager = new PrefManager(this);

        questionTitle = (EditText) findViewById(R.id.question_title);

        // get the button view
        btnProcess = (ActionProcessButton) findViewById(R.id.send_question_btn);

        //start with progress = 0
        btnProcess.setProgress(0);

        //to test the animations, when we touch the button it will start counting
        btnProcess.setOnClickListener(this);
        showCategories();

        editorRender();
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    private void fontChange() {
        TextView title = (TextView) findViewById(R.id.makeQuestionTitle);
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        TextView textView3 = (TextView) findViewById(R.id.textView3);
        TextView textView4 = (TextView) findViewById(R.id.textView4);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");
        title.setTypeface(custom_font);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        textView.setTypeface(custom_font);
        textView2.setTypeface(custom_font);
        textView3.setTypeface(custom_font);
        textView4.setTypeface(custom_font);
    }

    private void editorRender() {
        questionContext = (Editor) findViewById(R.id.editor);
        findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.H1);
            }
        });

        findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.H2);
            }
        });

        findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.H3);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.insertList(false);
            }
        });

        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.insertList(true);
            }
        });

        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.insertDivider();
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.openImagePicker();
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.insertLink();
            }
        });

        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questionContext.clearAllContents();
            }
        });
        //questionContext.dividerBackground=R.drawable.divider_background_dark;
        //questionContext.setFontFace(R.string.fontFamily__serif);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        questionContext.setHeadingTypeface(headingTypeface);
        questionContext.setContentTypeface(contentTypeface);
        questionContext.setDividerLayout(R.layout.tmpl_divider_layout);
        questionContext.setEditorImageLayout(R.layout.tmpl_image_view);
        questionContext.setListItemLayout(R.layout.tmpl_list_item);


        //questionContext.StartEditor();
        questionContext.setEditorListener(new EditorListener() {
            @Override
            public void onTextChanged(EditText editText, Editable text) {
                // Toast.makeText(EditorTestActivity.this, text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUpload(Bitmap image, String uuid) {
                Toast.makeText(AskQuestionActivity.this, "Image Bitmap is: " + uuid, Toast.LENGTH_LONG).show();
                questionContext.onImageUploadComplete("http://www.videogamesblogger.com/wp-content/uploads/2015/08/metal-gear-solid-5-the-phantom-pain-cheats-640x325.jpg", uuid);
                // questionContext.onImageUploadFailed(uuid);
            }
        });
        questionContext.render();  // this method must be called to start the questionContext
    }

    private void showCategories() {
        final String url = getString(R.string.server_connection_home) + "topic/getalltopics";
        RequestQueue queue = Volley.newRequestQueue(this);

        final List<KeyPairBoolData> topics = new ArrayList<>();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("Data");
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject topic = jsonArray.getJSONObject(i);
                                KeyPairBoolData h = new KeyPairBoolData();
                                h.setId(Integer.parseInt(topic.getString("TopicID")));
                                h.setName(topic.getString("TopicName"));
                                h.setSelected(false);
                                topics.add(h);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        req.setTag(REQ_TAG);
        queue.add(req);
        questionTopics = (SingleSpinnerSearch) findViewById(R.id.categories_spinner);

        /*final List<KeyPairBoolData> listArray2 = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray2.add(h);
        }

        questionTopics.setItems(listArray2, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        selectedTopic = items.get(i).getName();
                    }
                }
            }
        });*/

        questionTopics.setItems(topics, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> list) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelected()) {
                        selectedTopicInt = (int) list.get(i).getId();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == questionContext.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                questionContext.insertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            // questionContext.RestoreState();
        } else if (requestCode == questionContext.MAP_MARKER_REQUEST) {
            questionContext.insertMap(data.getStringExtra("cords"));
        }
    }

    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Raleway-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Raleway-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Raleway-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Raleway-BoldItalic.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Raleway-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Raleway-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Raleway-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Raleway-BoldItalic.ttf");
        return typefaceMap;
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
        if (questionContext.getContentAsHTML().length() == 0) {
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
        questionTopics.setEnabled(false);
        sendData(questionTitle.getText().toString(), questionContext.getContentAsHTML(), selectedTopicInt);
    }

    private void sendData(String questionTitle, String questionContext, int selectedTopic) {
        final String url = getString(R.string.server_connection_home) + "question/createnewquestion";
        RequestQueue queue = Volley.newRequestQueue(this);

        String date = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        // Post params to be sent to the server
        JSONObject params = new JSONObject();
        try {
            params.put("QuestionTitle", questionTitle);
            params.put("QuestionContext", questionContext);
            params.put("QuestionDate", date);
            params.put("QuestionIsClosed", "False");
            params.put("QuestionIsPrivate", "False");
            params.put("QuestionTopicID", selectedTopic);
            params.put("QuestionAskedUserID", prefManager.getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("Status").equals("1"))
                                responseStatus = true;
                            else
                                responseStatus = false;

                            responseMessage = response.getString("Message");
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                    startActivity(new Intent(AskQuestionActivity.this, MainActivity.class));
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
        questionTopics.setEnabled(true);
    }

    private void logOutDialogBox() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefManager = new PrefManager(AskQuestionActivity.this);
                        prefManager.setLogged(false);
                        prefManager.setLogout(true);
                        finish();
                        startActivity(new Intent(AskQuestionActivity.this, MainActivity.class));
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
