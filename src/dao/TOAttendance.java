package dao;

import java.sql.Date;

public class TOAttendance {
    private String attId;
    private String type;       // theory / practical
    private Date   attenDate;
    private String status;     // absent / present
    private String regNo;
    private String sessionId;

    public TOAttendance() {}

    public TOAttendance(String attId, String type, Date attenDate,
                        String status, String regNo, String sessionId) {
        this.attId     = attId;
        this.type      = type;
        this.attenDate = attenDate;
        this.status    = status;
        this.regNo     = regNo;
        this.sessionId = sessionId;
    }

    public String getAttId()      { return attId; }
    public String getType()       { return type; }
    public Date   getAttenDate()  { return attenDate; }
    public String getStatus()     { return status; }
    public String getRegNo()      { return regNo; }
    public String getSessionId()  { return sessionId; }

    public void setAttId(String v)     { this.attId = v; }
    public void setType(String v)      { this.type = v; }
    public void setAttenDate(Date v)   { this.attenDate = v; }
    public void setStatus(String v)    { this.status = v; }
    public void setRegNo(String v)     { this.regNo = v; }
    public void setSessionId(String v) { this.sessionId = v; }
}
