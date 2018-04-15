package com.example.mustafa.edumn.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mustafa on 12.03.2018.
 */

public class Answer {
    @SerializedName("AnswerID")
    @Expose
    private Integer answerID;
    @SerializedName("AnswerContext")
    @Expose
    private String answerContext;
    @SerializedName("AnswerRating")
    @Expose
    private Integer answerRating;
    @SerializedName("AnswerDate")
    @Expose
    private String answerDate;
    @SerializedName("UserID")
    @Expose
    private Integer userID;
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("UserSurname")
    @Expose
    private String userSurname;
    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("QuestionTitle")
    @Expose
    private String questionTitle;
    @SerializedName("ImagePaths")
    @Expose
    private ArrayList<String> imagePaths = null;

    public Integer getAnswerID() {
        return answerID;
    }

    public void setAnswerID(Integer answerID) {
        this.answerID = answerID;
    }

    public String getAnswerContext() {
        return answerContext;
    }

    public void setAnswerContext(String answerContext) {
        this.answerContext = answerContext;
    }

    public Integer getAnswerRating() {
        return answerRating;
    }

    public void setAnswerRating(Integer answerRating) {
        this.answerRating = answerRating;
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }

    public Integer getQuestionID() {
        return questionID;
    }

    public void setQuestionID(Integer questionID) {
        this.questionID = questionID;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
