package com.example.mustafa.edumn.Models;

/**
 * Created by Mustafa on 11.03.2018.
 */

public class Question {
    private int QuestionID;
    private String QuestionTitle;
    private String QuestionContext;
    private int QuestionUserID;
    private String QuestionUserName;
    private String QuestionDate;
    private boolean QuestionIsClosed;
    private boolean QuestionIsPrivate;
    private String QuestionTitleColor;

    public Question(int questionID, String questionTitle, String questionContext, String questionDate,
                    boolean questionIsClosed, boolean questionIsPrivate, int questionUserID,
                    String questionUserName, String questionTitleColor) {
        QuestionID = questionID;
        QuestionTitle = questionTitle;
        QuestionContext = questionContext;
        QuestionUserID = questionUserID;
        QuestionUserName = questionUserName;
        QuestionDate = questionDate;
        QuestionIsClosed = questionIsClosed;
        QuestionIsPrivate = questionIsPrivate;
        QuestionTitleColor = questionTitleColor;
    }

    public Question() {

    }

    public int getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(int questionID) {
        QuestionID = questionID;
    }

    public String getQuestionTitle() {
        return QuestionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        QuestionTitle = questionTitle;
    }

    public String getQuestionContext() {
        return QuestionContext;
    }

    public void setQuestionContext(String questionContext) {
        QuestionContext = questionContext;
    }

    public String getQuestionDate() {
        return QuestionDate;
    }

    public void setQuestionDate(String questionDate) {
        QuestionDate = questionDate;
    }

    public boolean isQuestionIsClosed() {
        return QuestionIsClosed;
    }

    public void setQuestionIsClosed(boolean questionIsClosed) {
        QuestionIsClosed = questionIsClosed;
    }

    public boolean isQuestionIsPrivate() {
        return QuestionIsPrivate;
    }

    public void setQuestionIsPrivate(boolean questionIsPrivate) {
        QuestionIsPrivate = questionIsPrivate;
    }

    public int getQuestionUserID() {
        return QuestionUserID;
    }

    public void setQuestionUserID(int questionUserID) {
        QuestionUserID = questionUserID;
    }

    public String getQuestionUserName() {
        return QuestionUserName;
    }

    public void setQuestionUserName(String questionUserName) {
        QuestionUserName = questionUserName;
    }

    public String getQuestionTitleColor() {
        return QuestionTitleColor;
    }

    public void setQuestionTitleColor(String questionTitleColor) {
        QuestionTitleColor = questionTitleColor;
    }
}
