package com.anis.fitzone.modeles;

public class QuizResult {

    private String quizId;
    private int score;
    private int total;

    public QuizResult() {
    }

    public QuizResult(String quizId, int score, int total) {
        this.quizId = quizId;
        this.score = score;
        this.total = total;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
