package com.example.mustafa.edumn;

import android.graphics.Color;
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

import com.example.mustafa.edumn.Models.Question;

import java.util.ArrayList;

/**
 * Created by Mustafa on 11.03.2018.
 */

public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {

    ArrayList<Question> questions;
    CustomClickListener listener;
    private ArrayList<Question> filteredQuestions;

    public QuestionsRecyclerAdapter(ArrayList<Question> questions, CustomClickListener listener) {

        this.questions = questions;
        this.listener = listener;
    }

    @Override
    public QuestionsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_all_questions, parent, false);
        /*final QuestionsRecyclerAdapter.ViewHolder view_holder = new QuestionsRecyclerAdapter.ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });*/
        return new ViewHolder(v);
        //return view_holder;
    }

    @Override
    public void onBindViewHolder(final QuestionsRecyclerAdapter.ViewHolder holder, int position) {
        holder.questionTitle.setText(questions.get(position).getQuestionTitle());
        holder.questionUserName.setText(questions.get(position).getQuestionUserName());
        holder.questionTitle.setTextColor(Color.parseColor(questions.get(position).getQuestionTitleColor()));
        holder.questionContext.setText(questions.get(position).getQuestionContext());
        holder.questionDate.setText(questions.get(position).getQuestionDate());

        PrefManager prefManager = new PrefManager(MyApplication.getAppContext());
        if (!prefManager.isLogged() || questions.get(position).getQuestionUserID() != Integer.parseInt(prefManager.getUserID())) {
            holder.overflow.setVisibility(View.GONE);
        }

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
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView questionTitle, questionUserName, questionContext, questionDate;
        public CardView card_view;
        public ImageView overflow;

        public ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view);
            questionTitle = view.findViewById(R.id.question_title);
            questionUserName = view.findViewById(R.id.question_user_name);
            questionContext = view.findViewById(R.id.question_context);
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
