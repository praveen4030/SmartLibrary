package com.example.smartlibrary.Model;

public class Video {

    String videoID,teacher,subject,topic;

    public Video() {
    }

    public Video(String videoID, String teacher, String subject, String topic) {
        this.videoID = videoID;
        this.teacher = teacher;
        this.subject = subject;
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
