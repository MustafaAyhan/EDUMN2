package com.example.mustafa.edumn.Models;

/**
 * Created by Mustafa on 9.03.2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topic {

    @SerializedName("TopicID")
    @Expose
    private Integer topicID;
    @SerializedName("TopicName")
    @Expose
    private String topicName;
    @SerializedName("TopicDescription")
    @Expose
    private String topicDescription;
    @SerializedName("TopicCreationDate")
    @Expose
    private String topicCreationDate;
    @SerializedName("TopicUserID")
    @Expose
    private Integer topicUserID;
    @SerializedName("TopicUserName")
    @Expose
    private String topicUserName;
    @SerializedName("TopicUserSurname")
    @Expose
    private String topicUserSurname;
    @SerializedName("TopicBackgroundColor")
    @Expose
    private String topicBackgroundColor;
    @SerializedName("TopicQuestionNumber")
    @Expose
    private int questionCount;
    @SerializedName("TopicAnswerNumber")
    @Expose
    private int answerCount;

    public Integer getTopicID() {
        return topicID;
    }

    public void setTopicID(Integer topicID) {
        this.topicID = topicID;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicDescription() {
        return topicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        this.topicDescription = topicDescription;
    }

    public String getTopicCreationDate() {
        return topicCreationDate;
    }

    public void setTopicCreationDate(String topicCreationDate) {
        this.topicCreationDate = topicCreationDate;
    }

    public Integer getTopicUserID() {
        return topicUserID;
    }

    public void setTopicUserID(Integer topicUserID) {
        this.topicUserID = topicUserID;
    }

    public String getTopicUserName() {
        return topicUserName;
    }

    public void setTopicUserName(String topicUserName) {
        this.topicUserName = topicUserName;
    }

    public String getTopicUserSurname() {
        return topicUserSurname;
    }

    public void setTopicUserSurname(String topicUserSurname) {
        this.topicUserSurname = topicUserSurname;
    }

    public String getTopicBackgroundColor() {
        return topicBackgroundColor;
    }

    public void setTopicBackgroundColor(String topicBackgroundColor) {
        this.topicBackgroundColor = topicBackgroundColor;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }
}
