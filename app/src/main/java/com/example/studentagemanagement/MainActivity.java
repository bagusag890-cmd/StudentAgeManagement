package com.example.studentagemanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editStudentName;
    private EditText editStudentId;
    private SeekBar seekBarAge;
    private TextView textAgeValue;
    private TextView textEmpty;
    private Button buttonAdd;
    private Button buttonUpdate;
    private Button buttonCancel;
    private RecyclerView recyclerStudents;

    private DatabaseHelper databaseHelper;
    private StudentAdapter studentAdapter;
    private final List<Student> studentList = new ArrayList<>();

    private int editingId = -1;
    private int selectedAge = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editStudentName = findViewById(R.id.editStudentName);
        editStudentId = findViewById(R.id.editStudentId);
        seekBarAge = findViewById(R.id.seekBarAge);
        textAgeValue = findViewById(R.id.textAgeValue);
        textEmpty = findViewById(R.id.textEmpty);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonCancel = findViewById(R.id.buttonCancel);
        recyclerStudents = findViewById(R.id.recyclerStudents);

        databaseHelper = new DatabaseHelper(this);

        seekBarAge.setMax(33);
        seekBarAge.setProgress(0);
        updateAgeLabel();

        seekBarAge.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar, int progress,
                            boolean fromUser) {

                        selectedAge = progress + 17;
                        updateAgeLabel();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                }
        );

        recyclerStudents.setLayoutManager(
                new LinearLayoutManager(this));

        studentAdapter = new StudentAdapter(
                this,
                studentList,
                new StudentAdapter.OnStudentActionListener() {
                    @Override
                    public void onEdit(Student student) {
                        startEdit(student);
                    }

                    @Override
                    public void onDelete() {
                        refreshStudents();
                        clearForm();
                    }
                }
        );

        recyclerStudents.setAdapter(studentAdapter);

        buttonAdd.setOnClickListener(v -> addStudent());
        buttonUpdate.setOnClickListener(v -> updateStudent());
        buttonCancel.setOnClickListener(v -> clearForm());

        refreshStudents();
    }

    private void updateAgeLabel() {
        textAgeValue.setText("Age: " + selectedAge + " years");
    }

    private void addStudent() {
        String name = editStudentName.getText().toString().trim();
        String studentId = editStudentId.getText().toString().trim();

        if (name.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in Student Name and Student ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAge < 17 || selectedAge > 50) {
            Toast.makeText(this,
                    "Age must be between 17 and 50",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.isStudentIdUsed(studentId, -1)) {
            Toast.makeText(this,
                    "Student ID already exists",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(name, studentId, selectedAge);
        boolean success = databaseHelper.addStudent(student);

        if (success) {
            Toast.makeText(this,
                    "Student added successfully",
                    Toast.LENGTH_SHORT).show();

            clearForm();
            refreshStudents();
        } else {
            Toast.makeText(this,
                    "Failed to add student. Check the Student ID.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void startEdit(Student student) {
        editingId = student.getId();

        editStudentName.setText(student.getStudentName());
        editStudentId.setText(student.getStudentId());

        selectedAge = student.getAge();
        seekBarAge.setProgress(selectedAge - 17);
        updateAgeLabel();

        buttonAdd.setEnabled(false);
        buttonUpdate.setEnabled(true);
        buttonCancel.setVisibility(View.VISIBLE);

        editStudentName.requestFocus();

        Toast.makeText(this,
                "Change the data, then press UPDATE STUDENT",
                Toast.LENGTH_SHORT).show();
    }

    private void updateStudent() {
        if (editingId == -1) {
            return;
        }

        String name = editStudentName.getText().toString().trim();
        String studentId = editStudentId.getText().toString().trim();

        if (name.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in Student Name and Student ID",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedAge < 17 || selectedAge > 50) {
            Toast.makeText(this,
                    "Age must be between 17 and 50",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.isStudentIdUsed(studentId, editingId)) {
            Toast.makeText(this,
                    "Student ID already belongs to another student",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Student student = new Student(
                editingId, name, studentId, selectedAge);

        boolean success = databaseHelper.updateStudent(student);

        if (success) {
            Toast.makeText(this,
                    "Student updated successfully",
                    Toast.LENGTH_SHORT).show();

            clearForm();
            refreshStudents();
        } else {
            Toast.makeText(this,
                    "No changes saved or update failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshStudents() {
        studentList.clear();
        studentList.addAll(databaseHelper.getAllStudents());
        studentAdapter.notifyDataSetChanged();

        if (studentList.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
    }

    private void clearForm() {
        editStudentName.setText("");
        editStudentId.setText("");

        selectedAge = 17;
        seekBarAge.setProgress(0);
        updateAgeLabel();

        editingId = -1;

        buttonAdd.setEnabled(true);
        buttonUpdate.setEnabled(false);
        buttonCancel.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        super.onDestroy();
    }
}
