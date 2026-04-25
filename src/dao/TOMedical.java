package dao;

import java.sql.Date;

public class TOMedical {
    private String medicalId;
    private Date   submissionDate;
    private String description;
    private Date   affectedStartDate;
    private Date   affectedEndDate;
    private String status;   // approved / not approved
    private String regNo;
    private String sessionId;

    public TOMedical() {}

    public TOMedical(String medicalId, Date submissionDate, String description,
                     Date affectedStartDate, Date affectedEndDate,
                     String status, String regNo, String sessionId) {
        this.medicalId         = medicalId;
        this.submissionDate    = submissionDate;
        this.description       = description;
        this.affectedStartDate = affectedStartDate;
        this.affectedEndDate   = affectedEndDate;
        this.status            = status;
        this.regNo             = regNo;
        this.sessionId         = sessionId;
    }

    public String getMedicalId()           { return medicalId; }
    public Date   getSubmissionDate()      { return submissionDate; }
    public String getDescription()         { return description; }
    public Date   getAffectedStartDate()   { return affectedStartDate; }
    public Date   getAffectedEndDate()     { return affectedEndDate; }
    public String getStatus()              { return status; }
    public String getRegNo()               { return regNo; }
    public String getSessionId()           { return sessionId; }

    public void setMedicalId(String v)          { this.medicalId = v; }
    public void setSubmissionDate(Date v)        { this.submissionDate = v; }
    public void setDescription(String v)         { this.description = v; }
    public void setAffectedStartDate(Date v)     { this.affectedStartDate = v; }
    public void setAffectedEndDate(Date v)       { this.affectedEndDate = v; }
    public void setStatus(String v)              { this.status = v; }
    public void setRegNo(String v)               { this.regNo = v; }
    public void setSessionId(String v)           { this.sessionId = v; }
}
