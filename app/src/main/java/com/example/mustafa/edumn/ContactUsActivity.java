package com.example.mustafa.edumn;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.riyagayasen.easyaccordion.AccordionView;

import org.w3c.dom.Text;

public class ContactUsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView user1, user2, user3, user4;
    EditText questionTitle, questionContext;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initiate();
    }

    private void initiate() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Contact Us");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        prefManager = new PrefManager(this);

        if (prefManager.isLogged()) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }

        TextView aboutUs = (TextView) findViewById(R.id.about_us);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        aboutUs.setTypeface(custom_font);

        TextView founders = (TextView) findViewById(R.id.founders);
        custom_font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf");
        founders.setTypeface(custom_font);

        AccordionView accordionView = new AccordionView(this);

        TextView user1 = (TextView) findViewById(R.id.user1_email);
        TextView user2 = (TextView) findViewById(R.id.user2_email);
        TextView user3 = (TextView) findViewById(R.id.user3_email);
        TextView user4 = (TextView) findViewById(R.id.user4_email);
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
        }else if (id == R.id.nav_logout) {
            logOutDialogBox();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void toMailProgram(View view) {
        switch (view.getId()) {
            case R.id.user1_email:
                sendEmail("mustafa.ayhan95@gmail.com");
                break;
            case R.id.user2_email:
                sendEmail("oguzhan.durmus@ceng.deu.edu.tr");
                break;
            case R.id.user3_email:
                sendEmail("aykuttoprak2315@gmail.com");
                break;
            case R.id.user4_email:
                sendEmail("mesut56ozoktem@gmail.com");
                break;
        }
    }

    private void sendEmail(String emailAddress) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{emailAddress});
        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Educational Social Network");
        i.putExtra(android.content.Intent.EXTRA_TEXT, "Contact Mail");
        startActivity(Intent.createChooser(i, "Send email"));
    }

    private void logOutDialogBox(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Are you sure want to logout?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        prefManager = new PrefManager(ContactUsActivity.this);
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
