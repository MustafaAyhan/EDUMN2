package com.example.mustafa.edumn;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mustafa.edumn.Models.Topic;

import java.util.ArrayList;

/**
 * Created by Mustafa on 9.03.2018.
 */

public class TopicsRecyclerAdapter extends RecyclerView.Adapter<TopicsRecyclerAdapter.ViewHolder> implements Filterable {

    ArrayList<Topic> topics;
    CustomClickListener listener;
    private ArrayList<Topic> filteredTopic;

    public TopicsRecyclerAdapter(ArrayList<Topic> topics, CustomClickListener listener) {

        this.topics = topics;
        this.listener = listener;
    }

    @Override
    public TopicsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_all_topics, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });

        return view_holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.topicTitle.setText(topics.get(position).getTopicName());
        holder.topicUserName.setText(topics.get(position).getTopicUserName());
        holder.layout.setBackgroundColor(Color.parseColor(topics.get(position).getTopicBackgroundColor()));
        holder.topicDescription.setText(topics.get(position).getTopicDescription());
        holder.topicDate.setText(topics.get(position).getTopicCreationDate());

        PrefManager prefManager = new PrefManager(MyApplication.getAppContext());
        if (!prefManager.isLogged() || topics.get(position).getTopicUserID() != Integer.parseInt(prefManager.getUserID())) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return topics.size();
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

                    filteredTopic = topics;

                } else {

                    ArrayList<Topic> tempFilteredList = new ArrayList<>();

                    for (Topic topic : topics) {

                        // search for topic name
                        if (topic.getTopicName().toLowerCase().contains(searchString)) {

                            tempFilteredList.add(topic);
                        }
                    }

                    filteredTopic = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredTopic;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredTopic = (ArrayList<Topic>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView topicTitle, topicUserName, topicDescription, topicDate;
        public CardView card_view;
        public RelativeLayout layout;
        public Button editButton, deleteButton;


        public ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            layout = view.findViewById(R.id.relative_layout);
            topicTitle = view.findViewById(R.id.question_title);
            topicUserName = view.findViewById(R.id.question_user_name);
            topicDescription = view.findViewById(R.id.topic_description);
            topicDate = view.findViewById(R.id.question_date);
            editButton = view.findViewById(R.id.edit_topic_button);
            deleteButton = view.findViewById(R.id.delete_topic_button);


        }
    }
}
