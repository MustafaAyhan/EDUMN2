package com.example.mustafa.edumn.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mustafa on 14.03.2018.
 */

public class TopicResponse {

    @SerializedName("Status")
    @Expose
    private Integer status;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private ArrayList<Topic> topicArrayList = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Topic> getTopicArrayList() {
        return topicArrayList;
    }

    public void setTopicArrayList(ArrayList<Topic> topicArrayList) {
        this.topicArrayList = topicArrayList;
    }
}
