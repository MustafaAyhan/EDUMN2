package com.example.mustafa.edumn.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mustafa on 11.03.2018.
 */

public class Question {
    @SerializedName("QuestionID")
    @Expose
    private Integer questionID;
    @SerializedName("QuestionTitle")
    @Expose
    private String questionTitle;
    @SerializedName("QuestionContext")
    @Expose
    private String questionContext;
    @SerializedName("QuestionDate")
    @Expose
    private String questionDate;
    @SerializedName("QuestionIsClosed")
    @Expose
    private Boolean questionIsClosed;
    @SerializedName("QuestionIsPrivate")
    @Expose
    private Boolean questionIsPrivate;
    @SerializedName("QuestionAskedUserID")
    @Expose
    private Integer questionAskedUserID;
    @SerializedName("QuestionAskedUserName")
    @Expose
    private String questionAskedUserName;
    @SerializedName("QuestionAskedUserSurName")
    @Expose
    private String questionAskedUserSurName;
    @SerializedName("QuestionTopicColor")
    @Expose
    private String questionTopicColor;
    @SerializedName("QuestionTopicID")
    @Expose
    private int questionTopicID;
    @SerializedName("ImagePaths")
    @Expose
    private ArrayList<String> imagePaths = null;
    @SerializedName("AnswerCount")
    @Expose
    private int answerCount;

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

    public String getQuestionContext() {
        return questionContext;
    }

    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    public String getQuestionDate() {
        return questionDate;
    }

    public void setQuestionDate(String questionDate) {
        this.questionDate = questionDate;
    }

    public Boolean getQuestionIsClosed() {
        return questionIsClosed;
    }

    public void setQuestionIsClosed(Boolean questionIsClosed) {
        this.questionIsClosed = questionIsClosed;
    }

    public Boolean getQuestionIsPrivate() {
        return questionIsPrivate;
    }

    public void setQuestionIsPrivate(Boolean questionIsPrivate) {
        this.questionIsPrivate = questionIsPrivate;
    }

    public Integer getQuestionAskedUserID() {
        return questionAskedUserID;
    }

    public void setQuestionAskedUserID(Integer questionAskedUserID) {
        this.questionAskedUserID = questionAskedUserID;
    }

    public String getQuestionAskedUserName() {
        return questionAskedUserName;
    }

    public void setQuestionAskedUserName(String questionAskedUserName) {
        this.questionAskedUserName = questionAskedUserName;
    }

    public String getQuestionAskedUserSurName() {
        return questionAskedUserSurName;
    }

    public void setQuestionAskedUserSurName(String questionAskedUserSurName) {
        this.questionAskedUserSurName = questionAskedUserSurName;
    }

    public String getQuestionTopicColor() {
        return questionTopicColor;
    }

    public void setQuestionTopicColor(String questionTopicColor) {
        this.questionTopicColor = questionTopicColor;
    }

    public int getQuestionTopicID() {
        return questionTopicID;
    }

    public void setQuestionTopicID(int questionTopicID) {
        this.questionTopicID = questionTopicID;
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }
}
