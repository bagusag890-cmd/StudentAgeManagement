package com.example.studentagemanagement;

public class Student {

    private int id;
    private String studentName;
    private String studentId;
    private int age;

    public Student(int id, String studentName, String studentId, int age) {
        this.id = id;
        this.studentName = studentName;
        this.studentId = studentId;
        this.age = age;
    }

    public Student(String studentName, String studentId, int age) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentId() {
        return studentId;
    }

    public int getAge() {
        return age;
    }
}
