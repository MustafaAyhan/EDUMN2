package com.example.mustafa.edumn.Models;

/**
 * Created by Mustafa on 9.03.2018.
 */

public class Topic {

    private int TopicID;
    private String TopicName;
    private String TopicDescription;
    private String TopicCreationDate;
    private int TopicUserID;
    private String TopicBackgroundColor;
    private String TopicUserName;

    public Topic(int topicID, String topicName, String topicDescription, String topicCreationDate,
                 int topicUserID, String topicBackgroundColor, String topicUserName) {
        TopicID = topicID;
        TopicName = topicName;
        TopicDescription = topicDescription;
        TopicCreationDate = topicCreationDate;
        TopicUserID = topicUserID;
        TopicBackgroundColor = topicBackgroundColor;
        TopicUserName = topicUserName;
    }

    public Topic(String topicName, String topicUserName, String topicCreationDate) {
        TopicName = topicName;
        TopicCreationDate = topicCreationDate;
        TopicUserName = topicUserName;
    }

    public Topic() {

    }

    public int getTopicID() {
        return TopicID;
    }

    public void setTopicID(int topicID) {
        TopicID = topicID;
    }

    public String getTopicName() {
        return TopicName;
    }

    public void setTopicName(String topicName) {
        TopicName = topicName;
    }

    public String getTopicDescription() {
        return TopicDescription;
    }

    public void setTopicDescription(String topicDescription) {
        TopicDescription = topicDescription;
    }

    public String getTopicCreationDate() {
        return TopicCreationDate;
    }

    public void setTopicCreationDate(String topicCreationDate) {
        TopicCreationDate = topicCreationDate;
    }

    public int getTopicUserID() {
        return TopicUserID;
    }

    public void setTopicUserID(int topicUserID) {
        TopicUserID = topicUserID;
    }

    public String getTopicBackgroundColor() {
        return TopicBackgroundColor;
    }

    public void setTopicBackgroundColor(String topicBackgroundColor) {
        TopicBackgroundColor = topicBackgroundColor;
    }

    public String getTopicUserName() {
        return TopicUserName;
    }

    public void setTopicUserName(String topicUserName) {
        TopicUserName = topicUserName;
    }

}
