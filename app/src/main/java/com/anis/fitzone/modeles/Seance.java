package com.anis.fitzone.modeles;

public class Seance {

    public static final String STATUT_A_FAIRE = "Non soumis";
    public static final String STATUT_SOUMISE = "Soumise";
    public static final String STATUT_EN_RETARD = "En retard";
    public static final String STATUT_VALIDEE = "Validée";

    private String id;
    private String programId;
    private String title;
    private String description;
    private String dueDate;
    private String instructions;
    private String status;
    private Integer grade;
    private String comment;
    private int totalPoints;
    private String type;
    private String submissionText;
    private String submissionUrl;
    private String submittedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubmissionText() {
        return submissionText;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }

    public String getSubmissionUrl() {
        return submissionUrl;
    }

    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    /**
     * Une séance est en retard si elle n'est pas soumise/validée et que la date
     * limite est dans le passé. Calculé côté client : le serveur ne connaît pas "aujourd'hui".
     */
    public boolean isLateComputed() {
        if (STATUT_SOUMISE.equals(status) || STATUT_VALIDEE.equals(status)) {
            return false;
        }
        if (dueDate == null) {
            return false;
        }
        return dueDate.compareTo(currentIsoDate()) < 0;
    }

    private String currentIsoDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.CANADA);
        return sdf.format(new java.util.Date());
    }

    public String getDisplayStatus() {
        if (STATUT_SOUMISE.equals(status) || STATUT_VALIDEE.equals(status)) {
            return status;
        }
        return isLateComputed() ? STATUT_EN_RETARD : STATUT_A_FAIRE;
    }
}
