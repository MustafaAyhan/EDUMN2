package com.example.mustafa.edumn.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mustafa.edumn.FullScreenImage;
import com.example.mustafa.edumn.InterFaces.CustomClickListener;
import com.example.mustafa.edumn.Models.Answer;
import com.example.mustafa.edumn.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mustafa on 16.03.2018.
 */

public class SingleAnswerRecyclerAdapter extends RecyclerView.Adapter<SingleAnswerRecyclerAdapter.ViewHolder> {

    ArrayList<Answer> answers;
    CustomClickListener listener;
    private ArrayList<Answer> filteredQuestions;
    private Context context;

    public SingleAnswerRecyclerAdapter(ArrayList<Answer> answers, CustomClickListener listener, Context context) {

        this.answers = answers;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public SingleAnswerRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_single_answer, parent, false);
        final SingleAnswerRecyclerAdapter.ViewHolder view_holder = new SingleAnswerRecyclerAdapter.ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, view_holder.getPosition());
            }
        });
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final SingleAnswerRecyclerAdapter.ViewHolder holder, int position) {
        holder.answerUserName.setText(answers.get(position).getUserName());
        holder.answerUserSurname.setText(answers.get(position).getUserSurname());
        holder.answerContext.setText(answers.get(position).getAnswerContext());
        holder.answerDate.setText(answers.get(position).getAnswerDate());
        holder.answerRating.setText("Rating: " + answers.get(position).getAnswerRating());
        holder.linearLayout.removeAllViews();
        for (int i = 0; i < answers.get(position).getImagePaths().size(); i++) {
            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 10, 0);
            imageView.setLayoutParams(lp);
            String path = context.getString(R.string.server_connection) + "/Files/" + answers.get(position).getImagePaths().get(i);
            Picasso.get().load(path).into(imageView);
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context.getApplicationContext(), FullScreenImage.class);

                imageView.buildDrawingCache();
                Bitmap image = imageView.getDrawingCache();

                Bundle extras = new Bundle();
                extras.putParcelable("imagebitmap", image);
                intent.putExtras(extras);
                context.startActivity(intent);
            });
            holder.linearLayout.addView(imageView);
        }
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

        public TextView answerUserName, answerUserSurname, answerContext, answerDate, answerRating;
        public CardView card_view;
        public LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.card_view_single_answer);
            answerUserName = view.findViewById(R.id.answer_user_name);
            answerUserSurname = view.findViewById(R.id.answer_user_surname);
            answerContext = view.findViewById(R.id.answer_context);
            answerDate = view.findViewById(R.id.answer_date);
            answerRating = view.findViewById(R.id.answer_rating);
            linearLayout = view.findViewById(R.id.selected_photos_container_answer);
        }
    }
}