package dao;

import java.sql.Date;
import java.sql.Time;

public class Timetable {
    private String sessionId;
    private String cCode;
    private Date sessionDate;
    private String type;
    private Time duration;
    private String lecHall;

    public Timetable() {}

    public Timetable(String sessionId, String cCode, Date sessionDate,
                     String type, Time duration, String lecHall) {
        this.sessionId = sessionId;
        this.cCode = cCode;
        this.sessionDate = sessionDate;
        this.type = type;
        this.duration = duration;
        this.lecHall = lecHall;
    }

    public String getSessionId()   { return sessionId; }
    public String getCCode()       { return cCode; }
    public Date getSessionDate()   { return sessionDate; }
    public String getType()        { return type; }
    public Time getDuration()      { return duration; }
    public String getLecHall()     { return lecHall; }

    public void setSessionId(String sessionId)   { this.sessionId = sessionId; }
    public void setCCode(String cCode)           { this.cCode = cCode; }
    public void setSessionDate(Date sessionDate) { this.sessionDate = sessionDate; }
    public void setType(String type)             { this.type = type; }
    public void setDuration(Time duration)       { this.duration = duration; }
    public void setLecHall(String lecHall)       { this.lecHall = lecHall; }
}
