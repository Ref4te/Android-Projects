package com.server.provider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.StudentViewHolder> {

    private ArrayList<Student> StudentList;
    public CardAdapter(ArrayList<Student> studentList) {
        this.StudentList = studentList;
    }


//    public void setNoteList(ArrayList<Note> noteList) {
//        this.noteList = noteList;
//        notifyDataSetChanged();
//    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = StudentList.get(position);

        holder.tvName.setText(student.getStudent_name());

        String group = student.getStudent_group();
        if (group == null || group.trim().isEmpty()) {
            holder.tvGroup.setText("Группа: -");
        } else {
            holder.tvGroup.setText("Группа: " + group);
        }

        String phone = student.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            holder.tvPhone.setText("Номер телефона: -");
        } else {
            holder.tvPhone.setText("Номер телефона: " + phone);
        }
    }

    @Override
    public int getItemCount() {
        return StudentList.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvName, tvGroup, tvPhone;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardViewStudent);
            tvName = itemView.findViewById(R.id.tvName);
            tvGroup = itemView.findViewById(R.id.tvGroup);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }
}