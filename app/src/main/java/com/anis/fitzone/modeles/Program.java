package com.anis.fitzone.modeles;

import java.util.ArrayList;
import java.util.List;

public class Program {

    private String id;
    private String code;
    private String title;
    private String description;
    private String coach;
    private String session;
    private String imageUrl;
    private List<String> annonces = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoach() {
        return coach;
    }

    public void setCoach(String coach) {
        this.coach = coach;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<String> getAnnonces() {
        return annonces;
    }

    public void setAnnonces(List<String> annonces) {
        this.annonces = annonces;
    }
}
