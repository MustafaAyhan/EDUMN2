package com.example.mustafa.edumn.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mustafa.edumn.InterFaces.CustomClickListener;
import com.example.mustafa.edumn.Models.Group;
import com.example.mustafa.edumn.R;

import java.util.ArrayList;

/**
 * Created by Mustafa on 6.04.2018.
 */

public class MyGroupsRecyclerAdapter extends RecyclerView.Adapter<MyGroupsRecyclerAdapter.ViewHolder> implements Filterable {

    ArrayList<Group> groups;
    CustomClickListener listener;
    private ArrayList<Group> filteredGroups;

    public MyGroupsRecyclerAdapter(ArrayList<Group> groups, CustomClickListener listener) {

        this.groups = groups;
        this.listener = listener;
    }

    @Override
    public MyGroupsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_my_groups, parent, false);
        final MyGroupsRecyclerAdapter.ViewHolder view_holder = new MyGroupsRecyclerAdapter.ViewHolder(v);

        v.setOnClickListener(v1 -> listener.onItemClick(v1, view_holder.getPosition()));

        return view_holder;
    }

    @Override
    public void onBindViewHolder(MyGroupsRecyclerAdapter.ViewHolder holder, int position) {
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
        public RelativeLayout layout;
        private TextView groupTitle, groupUserName, groupUserSurname,
                questionCount, answerCount, userCount, groupDescription;


        private ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            layout = view.findViewById(R.id.relative_layout);
            groupTitle = view.findViewById(R.id.group_title);
            groupUserName = view.findViewById(R.id.group_user_name);
            groupUserSurname = view.findViewById(R.id.group_user_surname);
            groupDescription = view.findViewById(R.id.group_description);
            questionCount = view.findViewById(R.id.question_count);
            answerCount = view.findViewById(R.id.answer_count);
            userCount = view.findViewById(R.id.user_count);

        }
    }
}
