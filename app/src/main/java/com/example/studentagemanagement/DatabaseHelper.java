package com.example.studentagemanagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_age.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_STUDENTS = "students";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "student_name";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_AGE = "age";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_STUDENT_ID + " TEXT NOT NULL UNIQUE, " +
                COL_AGE + " INTEGER NOT NULL CHECK (" +
                COL_AGE + " BETWEEN 17 AND 50)" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tidak ada migrasi untuk versi database ini.
    }

    public boolean addStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, student.getStudentName());
        values.put(COL_STUDENT_ID, student.getStudentId());
        values.put(COL_AGE, student.getAge());

        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_STUDENTS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_ID));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_NAME));

                String studentId = cursor.getString(
                        cursor.getColumnIndexOrThrow(COL_STUDENT_ID));

                int age = cursor.getInt(
                        cursor.getColumnIndexOrThrow(COL_AGE));

                students.add(new Student(id, name, studentId, age));
            }
        } finally {
            cursor.close();
        }

        return students;
    }

    public boolean updateStudent(Student student) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, student.getStudentName());
        values.put(COL_STUDENT_ID, student.getStudentId());
        values.put(COL_AGE, student.getAge());

        int result = db.update(
                TABLE_STUDENTS,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(student.getId())}
        );

        return result > 0;
    }

    public boolean deleteStudent(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                TABLE_STUDENTS,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    public boolean isStudentIdUsed(String studentId, int excludedId) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_STUDENTS,
                new String[]{COL_ID},
                COL_STUDENT_ID + " = ? AND " + COL_ID + " != ?",
                new String[]{
                        studentId,
                        String.valueOf(excludedId)
                },
                null,
                null,
                null
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }
}
