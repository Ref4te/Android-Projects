package com.server.provider;

public class Student {
    private int id;
    private String student_name;
    private String student_group;
    private String phone;

    public Student(int id, String student_name, String student_group, String phone) {
        this.id = id;
        this.student_name = student_name;
        this.student_group = student_group;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }
    public String getStudent_name() {
        return student_name;
    }
    public String getStudent_group() {
        return student_group;
    }
    public String getPhone() {
        return phone;
    }
}