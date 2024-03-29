package com.example.attendancesystem.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Student implements Serializable {
    private String name;
  private   String id;
  private   String year;
  private   String semester;
  private   String department;
  private   String batch;
  private   String section;
  private   String email;
  private   String phone;
  private   String gender;
  private   String course;
  private   String course_code;
  private   String shift;
  private   String password;

  private String pathImage;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }
    public  Student (){

    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public Student(String name, String id, String year, String semester, String department, String batch, String section, String email, String phone, String gender, String course, String course_code, String shift, String password, String pathImage) {
        this.name = name;
        this.id = id;
        this.year = year;
        this.semester = semester;
        this.department = department;
        this.batch = batch;
        this.section = section;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.course = course;
        this.course_code = course_code;
        this.shift=shift;
        this.password=password;
        this.pathImage = pathImage;


    }

    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("name",name);
        result.put("id",id);
        result.put("year",year);
        result.put("semester",semester);
        result.put("department",department);
        result.put("batch",batch);
        result.put("section",section);
        result.put("email",email);
        result.put("phone",phone);
        result.put("gender",gender);
        result.put("course",course);
        result.put("course_code",course_code);
        result.put("shift",shift);
        result.put("password",password);
        result.put("pathImage",pathImage);
        return  result;
    }
}
