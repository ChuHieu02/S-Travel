package com.cheaptravel.model;

import java.util.List;
import java.util.List;

public class Post {
    private String avatarUser;
    private String nameUser;
    private String content;
    private String date;
    private String location;
    private List<String> Images;
    private List<Comment> comments;
    private List<Like> likes;
    private String idPost;
    private String totalLike;
    private String totaComment;
    private String idUser;
    private String homestay;

    public Post(String avatarUser, String nameUser, String content, String date, String location, List<String> images) {
        this.avatarUser = avatarUser;
        this.nameUser = nameUser;
        this.content = content;
        this.date = date;
        this.location = location;
        Images = images;
    }


    public Post(){}

    public String getHomestay() {
        return homestay;
    }

    public void setHomestay(String homestay) {
        this.homestay = homestay;
    }

    public String getTotaComment() {
        return totaComment;
    }

    public void setTotaComment(String totaComment) {
        this.totaComment = totaComment;
    }

    public String getAvatarUser() {
        return avatarUser;
    }

    public void setAvatarUser(String avatarUser) {
        this.avatarUser = avatarUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getImages() {
        return Images;
    }

    public void setImages(List<String> images) {
        Images = images;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(String totalLike) {
        this.totalLike = totalLike;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
}
