package com.example.mustafa.edumn.Models;

import java.util.List;

/**
 * Created by Mustafa on 25.03.2018.
 */

public class Group {

    private int GroupID;
    private String GroupTitle;
    private String GroupDescription;
    private int GroupAdmin;
    private int GroupQuestionCount;
    private int GroupAnswerCount;
    private int GroupUserCount;
    private String AdminUserName;
    private String AdminSurname;
    private int WaitingGroupID;
    private List<String> emails = null;

    public Group(String groupTitle, String groupDescription, int groupAdmin, List<String> emails) {
        GroupTitle = groupTitle;
        GroupDescription = groupDescription;
        GroupAdmin = groupAdmin;
        this.emails = emails;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getGroupTitle() {
        return GroupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        GroupTitle = groupTitle;
    }

    public String getGroupDescription() {
        return GroupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        GroupDescription = groupDescription;
    }

    public int getGroupAdmin() {
        return GroupAdmin;
    }

    public void setGroupAdmin(int groupAdmin) {
        GroupAdmin = groupAdmin;
    }

    public int getGroupQuestionCount() {
        return GroupQuestionCount;
    }

    public void setGroupQuestionCount(int groupQuestionCount) {
        GroupQuestionCount = groupQuestionCount;
    }

    public int getGroupAnswerCount() {
        return GroupAnswerCount;
    }

    public void setGroupAnswerCount(int groupAnswerCount) {
        GroupAnswerCount = groupAnswerCount;
    }

    public int getGroupUserCount() {
        return GroupUserCount;
    }

    public void setGroupUserCount(int groupUserCount) {
        GroupUserCount = groupUserCount;
    }

    public String getAdminUserName() {
        return AdminUserName;
    }

    public void setAdminUserName(String adminUserName) {
        AdminUserName = adminUserName;
    }

    public String getAdminSurname() {
        return AdminSurname;
    }

    public void setAdminSurname(String adminSurname) {
        AdminSurname = adminSurname;
    }

    public int getWaitingGroupID() {
        return WaitingGroupID;
    }

    public void setWaitingGroupID(int waitingGroupID) {
        WaitingGroupID = waitingGroupID;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
