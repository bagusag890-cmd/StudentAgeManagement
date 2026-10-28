package com.example.studentagemanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter
        extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface OnStudentActionListener {
        void onEdit(Student student);
        void onDelete();
    }

    private final List<Student> students;
    private final Context context;
    private final OnStudentActionListener listener;

    public StudentAdapter(
            Context context,
            List<Student> students,
            OnStudentActionListener listener) {
        this.context = context;
        this.students = students;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);

        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull StudentViewHolder holder, int position) {

        Student student = students.get(position);

        holder.textStudentName.setText(student.getStudentName());
        holder.textStudentId.setText(
                "Student ID: " + student.getStudentId());
        holder.textStudentAge.setText(
                "Age: " + student.getAge() + " years");

        holder.buttonEdit.setOnClickListener(v ->
                listener.onEdit(student));

        holder.buttonDelete.setOnClickListener(v ->
                new AlertDialog.Builder(context)
                        .setTitle("Delete Student")
                        .setMessage(
                                "Delete " + student.getStudentName() + "?")
                        .setNegativeButton("Cancel", (dialog, which) ->
                                dialog.dismiss())
                        .setPositiveButton("Delete", (dialog, which) -> {
                            DatabaseHelper db =
                                    new DatabaseHelper(context);

                            boolean deleted =
                                    db.deleteStudent(student.getId());

                            db.close();

                            if (deleted) {
                                Toast.makeText(context,
                                        "Student deleted successfully",
                                        Toast.LENGTH_SHORT).show();
                                listener.onDelete();
                            } else {
                                Toast.makeText(context,
                                        "Failed to delete student",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show());
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {

        TextView textStudentName;
        TextView textStudentId;
        TextView textStudentAge;
        Button buttonEdit;
        Button buttonDelete;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            textStudentName =
                    itemView.findViewById(R.id.textStudentName);
            textStudentId =
                    itemView.findViewById(R.id.textStudentId);
            textStudentAge =
                    itemView.findViewById(R.id.textStudentAge);
            buttonEdit =
                    itemView.findViewById(R.id.buttonEdit);
            buttonDelete =
                    itemView.findViewById(R.id.buttonDelete);
        }
    }
}
