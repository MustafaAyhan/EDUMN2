package com.example.mustafa.edumn.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Mustafa on 12.03.2018.
 */

public class AnswerResponse {
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Data")
    @Expose
    private ArrayList<Answer> answerArrayList = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Answer> getAnswerList() {
        return answerArrayList;
    }

    public void setAnswerArrayList(ArrayList<Answer> answerArrayList) {
        this.answerArrayList = answerArrayList;
    }
}
