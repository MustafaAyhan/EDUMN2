package com.example.mustafa.edumn.InterFaces;

import com.example.mustafa.edumn.Models.AnswerResponse;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.Models.GroupResponse;
import com.example.mustafa.edumn.Models.QuestionResponse;
import com.example.mustafa.edumn.Models.TopicResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Mustafa on 12.03.2018.
 */

public interface ApiInterface {

    @GET("answer/getmyanswers/{id}")
    Call<AnswerResponse> getMyAnswers(@Path("id") int id);

    @GET("answer/getallanswers/{id}")
    Call<AnswerResponse> getAllAnswers(@Path("id") int id);

    @POST("topic/getalltopics/{userID}")
    Call<TopicResponse> getAllTopics(@Path("userID") int userID);

    @GET("question/getquestionswithtopicid/{id}/{userID}")
    Call<QuestionResponse> getQuestionWithTopicID(@Path("id") int id, @Path("userID") int userID);

    @Multipart
    @POST("answer/createanswerwithimages")
    Call<AnswerResponse> createAnswerWithImages(@Part MultipartBody.Part[] answerImages,
                                                @Part("AnswerContext") RequestBody answerContext,
                                                @Part("AnswerDate") RequestBody answerDate,
                                                @Part("AnswerRating") RequestBody answerRating,
                                                @Part("UserID") RequestBody userID,
                                                @Part("QuestionID") RequestBody questionID);

    @Multipart
    @POST("question/createnewquestionimage")
    Call<QuestionResponse> createQuestionImage(@Part MultipartBody.Part[] questionImages,
                                               @Part("QuestionTitle") RequestBody questionTitle,
                                               @Part("QuestionContext") RequestBody questionContext,
                                               @Part("QuestionDate") RequestBody questionDate,
                                               @Part("QuestionIsClosed") RequestBody questionIsClosed,
                                               @Part("QuestionIsPrivate") RequestBody questionIsPrivate,
                                               @Part("QuestionAskedUserID") RequestBody questionAskedUserID,
                                               @Part("QuestionTopicID") RequestBody questionTopicID,
                                               @Part("GroupID") RequestBody groupID);

    @Headers("Content-Type: application/json")
    @POST("group/checkemails")
    Call<GroupResponse> checkEmails(@Body List<String> emails);

    @POST("group/creategroup")
    Call<GroupResponse> createGroup(@Body Group group);

    @GET("group/getallgroups/{id}")
    Call<GroupResponse> getAllGroups(@Path("id") int id);

    @GET("group/getwaitingroups/{id}")
    Call<GroupResponse> getWaitingGroups(@Path("id") int id);

    @POST("group/joingroup")
    Call<ResponseBody> joinGroup(@Body int id);

    @POST("group/rejectgroup")
    Call<ResponseBody> rejectGroup(@Body int id);

    @GET("group/groupquestions/{id}")
    Call<QuestionResponse> getGroupQuestions(@Path("id") int id);

}
