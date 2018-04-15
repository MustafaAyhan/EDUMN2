package com.example.mustafa.edumn.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mustafa.edumn.CustomClasses.MyApplication;
import com.example.mustafa.edumn.InterFaces.CustomClickListener;
import com.example.mustafa.edumn.Models.Answer;
import com.example.mustafa.edumn.R;

import java.util.ArrayList;

/**
 * Created by Mustafa on 12.03.2018.
 */

public class MyAnswersRecyclerAdapter extends RecyclerView.Adapter<MyAnswersRecyclerAdapter.ViewHolder> {

    ArrayList<Answer> answers;
    CustomClickListener listener;
    private ArrayList<Answer> filteredQuestions;

    public MyAnswersRecyclerAdapter(ArrayList<Answer> answers, CustomClickListener listener) {

        this.answers = answers;
        this.listener = listener;
    }

    @Override
    public MyAnswersRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_my_answers, parent, false);
        final MyAnswersRecyclerAdapter.ViewHolder view_holder = new MyAnswersRecyclerAdapter.ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final MyAnswersRecyclerAdapter.ViewHolder holder, int position) {
        holder.questionTitle.setText(answers.get(position).getQuestionTitle());
        holder.questionUserName.setText(answers.get(position).getUserName());
        holder.answerContext.setText(answers.get(position).getAnswerContext());
        holder.questionDate.setText(answers.get(position).getAnswerDate());

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(MyApplication.getAppContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_question, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyAnswersRecyclerAdapter.MyMenuItemClickListener());
        popup.show();
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTitle, questionUserName, answerContext, questionDate;
        public CardView card_view;
        public ImageView overflow;

        public ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            questionTitle = view.findViewById(R.id.topic_title);
            questionUserName = view.findViewById(R.id.question_user_name);
            answerContext = view.findViewById(R.id.answer_context);
            questionDate = view.findViewById(R.id.question_date);
            overflow = view.findViewById(R.id.overflow);
        }
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    Toast.makeText(MyApplication.getAppContext(), "Edit Question", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_delete:
                    Toast.makeText(MyApplication.getAppContext(), "Delete", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }

    }
}