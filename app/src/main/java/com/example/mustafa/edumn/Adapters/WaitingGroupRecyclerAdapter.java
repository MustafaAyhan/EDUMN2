package com.example.mustafa.edumn.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mustafa.edumn.CustomClasses.ApiClient;
import com.example.mustafa.edumn.CustomClasses.MyApplication;
import com.example.mustafa.edumn.InterFaces.ApiInterface;
import com.example.mustafa.edumn.InterFaces.CustomClickListener;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.MyGroupsActivity;
import com.example.mustafa.edumn.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Mustafa on 8.04.2018.
 */

public class WaitingGroupRecyclerAdapter extends RecyclerView.Adapter<WaitingGroupRecyclerAdapter.ViewHolder> implements Filterable {

    ArrayList<Group> groups;
    CustomClickListener listener;
    Context context;
    private ArrayList<Group> filteredGroups;

    public WaitingGroupRecyclerAdapter(ArrayList<Group> groups, Context context, CustomClickListener listener) {

        this.groups = groups;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public WaitingGroupRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_waiting_groups, parent, false);
        final WaitingGroupRecyclerAdapter.ViewHolder view_holder = new WaitingGroupRecyclerAdapter.ViewHolder(v);

        v.setOnClickListener(v1 -> listener.onItemClick(v1, view_holder.getPosition()));

        return view_holder;
    }

    @Override
    public void onBindViewHolder(WaitingGroupRecyclerAdapter.ViewHolder holder, int position) {
        holder.groupTitle.setText(groups.get(position).getGroupTitle());
        holder.groupUserName.setText(groups.get(position).getAdminUserName());
        holder.groupUserSurname.setText(groups.get(position).getAdminSurname());
        holder.groupDescription.setText(groups.get(position).getGroupDescription());
        if (groups.get(position).getGroupQuestionCount() > 0)
            holder.questionCount.setText(groups.get(position).getGroupQuestionCount() + " Questions");
        else
            holder.questionCount.setText("0 Question");
        if (groups.get(position).getGroupAnswerCount() > 0)
            holder.answerCount.setText(groups.get(position).getGroupAnswerCount() + " Answers");
        else
            holder.answerCount.setText("0 Answer");
        holder.userCount.setText(groups.get(position).getGroupUserCount() + " Users");

        holder.joinButton.setOnClickListener(v -> joinToGroup(groups.get(position).getWaitingGroupID()));
        holder.rejectButton.setOnClickListener(view -> rejectGroup(groups.get(position).getWaitingGroupID()));
    }

    private void joinToGroup(int WaitingGroupID) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.joinGroup(WaitingGroupID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Toast.makeText(MyApplication.getAppContext(), "Successfully joined to the group", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("MainActivity", t.toString());
            }
        });

        context.startActivity(new Intent(context, MyGroupsActivity.class));
    }

    private void rejectGroup(int WaitingGroupID) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ResponseBody> call = apiService.rejectGroup(WaitingGroupID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                Toast.makeText(MyApplication.getAppContext(), "Group has been rejected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log error here since request failed
                Log.e("MainActivity", t.toString());
            }
        });
        context.startActivity(new Intent(context, MyGroupsActivity.class));
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    filteredGroups = groups;

                } else {

                    ArrayList<Group> tempFilteredList = new ArrayList<>();

                    for (Group group : groups) {

                        // search for topic name
                        if (group.getGroupTitle().toLowerCase().contains(searchString)) {

                            tempFilteredList.add(group);
                        }
                    }

                    filteredGroups = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredGroups;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredGroups = (ArrayList<Group>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView card_view;
        public Button joinButton, rejectButton;
        private TextView groupTitle, groupUserName, groupUserSurname,
                questionCount, answerCount, userCount, groupDescription;

        private ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.waiting_group_card_view);
            groupTitle = view.findViewById(R.id.waiting_group_title);
            groupUserName = view.findViewById(R.id.waiting_group_user_name);
            groupUserSurname = view.findViewById(R.id.waiting_group_user_surname);
            groupDescription = view.findViewById(R.id.waiting_group_description);
            questionCount = view.findViewById(R.id.waiting_group_question_count);
            answerCount = view.findViewById(R.id.waiting_group_answer_count);
            userCount = view.findViewById(R.id.waiting_group_user_count);
            joinButton = view.findViewById(R.id.join_waiting_group);
            rejectButton = view.findViewById(R.id.reject_waiting_group);

        }
    }
}
