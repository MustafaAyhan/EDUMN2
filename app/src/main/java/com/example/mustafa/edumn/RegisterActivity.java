package com.example.mustafa.edumn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.mustafa.edumn.CustomClasses.PrefManager;
import com.example.mustafa.edumn.CustomClasses.ProgressGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ProgressGenerator.OnCompleteListener {

    static final String REQ_TAG = "RegisterActivity";
    EditText birthday;
    DatePickerDialog datePickerDialog;
    private PrefManager prefManager;
    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputPasswordAgain, inputPhoneNumber;
    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutEmail,
            inputLayoutPassword, inputLayoutPasswordAgain, inputLayoutPhoneNumber;
    private ActionProcessButton btnProcess;
    private String responseMessage = "";
    private boolean loginStatus = false;

    public static boolean isValidPhone(String phone) {
        String expression = "^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$";
        CharSequence inputString = phone;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.matches();
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initiate();
    }

    private void initiate() {
        commonViews();
        setTitle("Register");

        // initiate the birthday picker and a button
        birthday = findViewById(R.id.input_birthday);
        // perform click event on edit text
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current birthday , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // birthday picker dialog
                datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                birthday.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        inputLayoutFirstName = findViewById(R.id.input_layout_first_name);
        inputLayoutLastName = findViewById(R.id.input_layout_last_name);
        inputLayoutEmail = findViewById(R.id.input_layout_email);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputLayoutPasswordAgain = findViewById(R.id.input_layout_password_again);
        inputLayoutPhoneNumber = findViewById(R.id.input_layout_phone);
        inputFirstName = findViewById(R.id.input_first_name);
        inputLastName = findViewById(R.id.input_last_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputPasswordAgain = findViewById(R.id.input_password_again);
        inputPhoneNumber = findViewById(R.id.input_phone);

        inputFirstName.addTextChangedListener(new MyTextWatcher(inputFirstName));
        inputLastName.addTextChangedListener(new MyTextWatcher(inputLastName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputPasswordAgain.addTextChangedListener(new MyTextWatcher(inputPasswordAgain));
        inputPhoneNumber.addTextChangedListener(new MyTextWatcher(inputPhoneNumber));

        // get the button view
        btnProcess = findViewById(R.id.btn_signup);

        //start with progress = 0
        btnProcess.setProgress(0);

        //to test the animations, when we touch the button it will start counting
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

        if (id == R.id.nav_categories) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.nav_contact) {
            startActivity(new Intent(this, ContactUsActivity.class));
        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void haveAnAccount(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_signup:
                submitForm();
                break;
        }
    }

    private void submitForm() {
        if (!validateFirstName() || !validateLastName() || !validateEmail() ||
                !validatePassword() || !validatePasswordAgain() || !validatePhoneNumber()) {
            return;
        }
        buttonProgress();
    }

    private void buttonProgress() {
        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        btnProcess.setMode(ActionProcessButton.Mode.PROGRESS);

        progressGenerator.start(btnProcess);
        btnProcess.setEnabled(false);
        inputFirstName.setEnabled(false);
        inputLastName.setEnabled(false);
        inputEmail.setEnabled(false);
        inputPassword.setEnabled(false);
        inputPasswordAgain.setEnabled(false);
        birthday.setEnabled(false);
        inputPhoneNumber.setEnabled(false);
        sendData(inputFirstName.getText().toString(), inputLastName.getText().toString(),
                inputEmail.getText().toString(), inputPassword.getText().toString(),
                birthday.getText().toString(), inputPhoneNumber.getText().toString());
    }

    private void sendData(String UserName, String UserSurname, String UserEmail,
                          String UserPassword, String UserBirthDate, String UserPhoneNumber) {
        final String url = getString(R.string.server_connection_home) + "account/register";
        RequestQueue queue = Volley.newRequestQueue(this);

        // Post params to be sent to the server
        JSONObject params = new JSONObject();
        try {
            params.put("UserName", UserName);
            params.put("UserSurname", UserSurname);
            params.put("UserEmail", UserEmail);
            params.put("UserPassword", UserPassword);
            params.put("UserBirthDate", UserBirthDate);
            params.put("UserRating", "0");
            params.put("UserPhoneNumber", UserPhoneNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("Status").equals("1")) {
                                prefManager = new PrefManager(RegisterActivity.this);
                                prefManager.setUserID(response.getString("UserID"));
                                prefManager.setUserName(inputFirstName.getText().toString());
                                prefManager.setUserSurname(inputLastName.getText().toString());
                                prefManager.setUserEmail(inputEmail.getText().toString());
                                prefManager.setUserPassword(inputPassword.getText().toString());
                                prefManager.setUserRating("0");
                                prefManager.setUserBirthDate(birthday.getText().toString());
                                prefManager.setUserPhonenumber(inputPhoneNumber.getText().toString());

                                responseMessage = "Welcome! You have signed up.";
                                loginStatus = true;
                                prefManager.setLogged(true);
                            } else {
                                responseMessage = response.getString("Message");
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
    }

    @Override
    public void onComplete() {
        if (!loginStatus) {
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
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }

                public void onTick(long millisUntilFinished) {
                    // millisUntilFinished    The amount of time until finished.
                    btnProcess.setProgress(100);
                }
            }.start();
        }
        btnProcess.setProgress(0);
        btnProcess.setEnabled(true);
        inputFirstName.setEnabled(true);
        inputLastName.setEnabled(true);
        inputEmail.setEnabled(true);
        inputPassword.setEnabled(true);
        inputPasswordAgain.setEnabled(true);
        birthday.setEnabled(true);
        inputPhoneNumber.setEnabled(true);
    }

    private boolean validateFirstName() {
        if (inputFirstName.getText().toString().trim().isEmpty()) {
            inputLayoutFirstName.setError(getString(R.string.err_msg_name));
            requestFocus(inputFirstName);
            return false;
        } else {
            inputLayoutFirstName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (inputLastName.getText().toString().trim().isEmpty()) {
            inputLayoutLastName.setError(getString(R.string.err_msg_last_name));
            requestFocus(inputLastName);
            return false;
        } else {
            inputLayoutLastName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else if (inputPassword.getText().toString().length() < 6) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_length));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordAgain() {
        if (inputPasswordAgain.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordAgain.setError(getString(R.string.err_msg_password));
            requestFocus(inputPasswordAgain);
            return false;
        } else if (!inputPasswordAgain.getText().toString().equals(inputPassword.getText().toString())) {
            inputLayoutPasswordAgain.setError(getString(R.string.err_msg_password_again));
            requestFocus(inputPasswordAgain);
            return false;
        } else {
            inputLayoutPasswordAgain.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhoneNumber() {
        if (inputPhoneNumber.getText().toString().trim().isEmpty()) {
            inputLayoutPhoneNumber.setError(getString(R.string.err_msg_phonenumber));
            requestFocus(inputPhoneNumber);
            return false;
        } else {
            inputLayoutPasswordAgain.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_first_name:
                    validateFirstName();
                    break;
                case R.id.input_last_name:
                    validateLastName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
                case R.id.input_password_again:
                    validatePasswordAgain();
                    break;
                case R.id.input_phone:
                    validatePhoneNumber();
                    break;
            }
        }
    }
}
