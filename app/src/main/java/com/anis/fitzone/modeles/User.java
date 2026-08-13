package com.anis.fitzone.modeles;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;
    private String email;
    private String password;
    private String nom;
    private String prenom;
    private String telephone;
    private String photoUrl;
    private List<String> enrolledProgramIds = new ArrayList<>();
    private List<QuizResult> quizResults = new ArrayList<>();
    private List<String> completedSeanceIds = new ArrayList<>();

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getEnrolledProgramIds() {
        return enrolledProgramIds;
    }

    public void setEnrolledProgramIds(List<String> enrolledProgramIds) {
        this.enrolledProgramIds = enrolledProgramIds;
    }

    public List<QuizResult> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(List<QuizResult> quizResults) {
        this.quizResults = quizResults;
    }

    public List<String> getCompletedSeanceIds() {
        return completedSeanceIds;
    }

    public void setCompletedSeanceIds(List<String> completedSeanceIds) {
        this.completedSeanceIds = completedSeanceIds;
    }

    public String getFullName() {
        return (prenom == null ? "" : prenom) + " " + (nom == null ? "" : nom);
    }
}
