package com.example.smartlibrary.Model;

public class Papers {

    String subject,semester,exam,url;

    public Papers() {

    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Papers(String subject, String semester, String exam, String url) {
        this.subject = subject;
        this.semester = semester;
        this.exam = exam;
        this.url = url;
    }


}
