package com.example.mustafa.edumn;

/**
 * Created by Mustafa on 26.01.2018.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String IS_LOGGED = "IsLogged";
    private static final String IS_LOGGEDOUT = "IsLoggedOut";

    private static final String USER_ID = "User_ID";
    private static final String USER_NAME = "User_Name";
    private static final String USER_SURNAME = "User_Surname";
    private static final String USER_EMAIL = "User_Email";
    private static final String USER_PASSWORD = "User_Password";
    private static final String USER_RATING = "User_Rating";
    private static final String USER_BIRTHDATE = "User_Birthdate";
    private static final String USER_PHONENUMBER = "User_PhoneNumber";
    private static final String USER_LASTEMAIL = "User_LastEmail";
    private static final String USER_LASTPASSWORD = "User_LastPassword";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public void setLogged(boolean isLogged) {
        editor.putBoolean(IS_LOGGED, isLogged);
        editor.commit();
    }

    public void setUserID(String id) {
        editor.putString(USER_ID, id);
        editor.commit();
    }

    public void setUserName(String name) {
        editor.putString(USER_NAME, name);
        editor.commit();
    }

    public void setUserSurname(String surname) {
        editor.putString(USER_SURNAME, surname);
        editor.commit();
    }

    public void setUserEmail(String email) {
        editor.putString(USER_EMAIL, email);
        editor.commit();
    }

    public void setUserPassword(String password) {
        editor.putString(USER_PASSWORD, password);
        editor.commit();
    }

    public void setUserRating(String rating) {
        editor.putString(USER_RATING, rating);
        editor.commit();
    }

    public void setUserBirthDate(String birthDate) {
        editor.putString(USER_BIRTHDATE, birthDate);
        editor.commit();
    }

    public void setUserPhonenumber(String phonenumber) {
        editor.putString(USER_PHONENUMBER, phonenumber);
        editor.commit();
    }

    public void setLastUserEmail(String email) {
        editor.putString(USER_LASTEMAIL, email);
        editor.commit();
    }

    public void setLastUserPassword(String password) {
        editor.putString(USER_LASTPASSWORD, password);
        editor.commit();
    }

    public void setLogout(boolean isLoggedOut){
        setLastUserEmail(getUserEmail());
        setLastUserPassword(getUserPassword());
        editor.putString(USER_NAME, "NoName");
        editor.putString(USER_ID, "0");
        editor.putString(USER_NAME, "NoName");
        editor.putString(USER_SURNAME, "NoSurname");
        editor.putString(USER_RATING, "NoRating");
        editor.putString(USER_BIRTHDATE, "NoBirthDate");
        editor.putString(USER_PHONENUMBER, "NoSurname");
        editor.putBoolean(IS_LOGGEDOUT, isLoggedOut);
        editor.commit();
    }


    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean isLogged() {
        return pref.getBoolean(IS_LOGGED, false);
    }

    public String getUserID() {
        return pref.getString(USER_ID, "0");
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "NoName");
    }

    public String getUserSurname() {
        return pref.getString(USER_SURNAME, "NoSurname");
    }

    public String getUserEmail() {
        return pref.getString(USER_EMAIL, "NoEMail");
    }

    public String getUserPassword() {
        return pref.getString(USER_PASSWORD, "NoPassword");
    }

    public String getUserRating() {
        return pref.getString(USER_RATING, "NoRating");
    }

    public String getUserBirthdate() {
        return pref.getString(USER_BIRTHDATE, "NoBirthDate");
    }

    public String getUserPhonenumber() {
        return pref.getString(USER_PHONENUMBER, "NoPhoneNumber");
    }

    public String getUserLastEmail() {
        return pref.getString(USER_LASTEMAIL, "NoEmail");
    }

    public String getUserLastPassword() {
        return pref.getString(USER_LASTPASSWORD, "NoPassword");
    }

    public boolean isLoggedOut(){
        return pref.getBoolean(IS_LOGGEDOUT, false);
    }

}