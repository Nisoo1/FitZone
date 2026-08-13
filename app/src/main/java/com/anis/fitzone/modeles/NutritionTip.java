package com.anis.fitzone.modeles;

public class NutritionTip {

    private String id;
    private String aliment;
    private String emoji;
    private int calories;
    private String description;
    private String moment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAliment() {
        return aliment;
    }

    public void setAliment(String aliment) {
        this.aliment = aliment;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMoment() {
        return moment;
    }

    public void setMoment(String moment) {
        this.moment = moment;
    }
}
