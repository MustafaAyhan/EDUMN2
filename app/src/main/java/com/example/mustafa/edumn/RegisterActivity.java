package com.example.mustafa.edumn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    EditText birthday;
    DatePickerDialog datePickerDialog;

    private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputPasswordAgain;
    private TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutEmail,
            inputLayoutPassword, inputLayoutPasswordAgain;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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

        navigationView.setNavigationItemSelectedListener(this);
        setTitle("Register");

        // initiate the birthday picker and a button
        birthday = (EditText) findViewById(R.id.input_birthday);
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
                                birthday.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_first_name);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_last_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutPasswordAgain = (TextInputLayout) findViewById(R.id.input_layout_password_again);
        inputFirstName = (EditText) findViewById(R.id.input_first_name);
        inputLastName = (EditText) findViewById(R.id.input_last_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputPasswordAgain = (EditText) findViewById(R.id.input_password_again);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputFirstName.addTextChangedListener(new RegisterActivity.MyTextWatcher(inputFirstName));
        inputLastName.addTextChangedListener(new RegisterActivity.MyTextWatcher(inputLastName));
        inputEmail.addTextChangedListener(new RegisterActivity.MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new RegisterActivity.MyTextWatcher(inputPassword));
        inputPasswordAgain.addTextChangedListener(new RegisterActivity.MyTextWatcher(inputPasswordAgain));

        btnSignUp.setOnClickListener(this);

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
            // Handle the camera action
        } else if (id == R.id.nav_categories) {

        } else if (id == R.id.nav_meeting) {

        } else if (id == R.id.nav_contact) {

        } else if (id == R.id.nav_login) {
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.nav_register) {
            startActivity(new Intent(this, RegisterActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateFirstName() || !validateLastName() || !validateEmail() ||
                !validatePassword() || !validatePasswordAgain()) {
            return;
        }
        Toast.makeText(this, "First Name: " + inputFirstName.getText().toString() +
                "\nLast Name: " + inputLastName.getText().toString() +
                "\nEmail: " + inputEmail.getText().toString() +
                "\nPassword: " + inputPassword.getText().toString() +
                "\nPasswordAgain: " + inputPasswordAgain.getText().toString(), Toast.LENGTH_SHORT).show();
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
        }
        else if (inputPassword.getText().toString().length() < 6) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password_length));
            requestFocus(inputPassword);
            return false;
        }else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePasswordAgain() {
        if (inputPasswordAgain.getText().toString().trim().isEmpty()) {
            inputLayoutPasswordAgain.setError(getString(R.string.err_msg_password));
            requestFocus(inputPasswordAgain);
            return false;
        }
        else if (!inputPasswordAgain.getText().toString().equals(inputPassword.getText().toString())) {
            inputLayoutPasswordAgain.setError(getString(R.string.err_msg_password_again));
            requestFocus(inputPasswordAgain);
            return false;
        } else {
            inputLayoutPasswordAgain.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
            }
        }
    }
}
