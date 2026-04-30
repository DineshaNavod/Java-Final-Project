package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LecMarks {
    private String  regNo;
    private String  cCode;
    private double  q1Marks;
    private double  q2Marks;
    private double  q3Marks;
    private double  assignmentMarks;
    private double  midMarks;
    private double  endMarks;

    public LecMarks() {}

    public LecMarks(String regNo, String cCode, double q1, double q2, double q3,
                    double assignment, double mid, double end) {
        this.regNo          = regNo;
        this.cCode          = cCode;
        this.q1Marks        = q1;
        this.q2Marks        = q2;
        this.q3Marks        = q3;
        this.assignmentMarks= assignment;
        this.midMarks       = mid;
        this.endMarks       = end;
    }

    // Getters
    public String getRegNo()           { return regNo; }
    public String getCCode()           { return cCode; }
    public double getQ1Marks()         { return q1Marks; }
    public double getQ2Marks()         { return q2Marks; }
    public double getQ3Marks()         { return q3Marks; }
    public double getAssignmentMarks() { return assignmentMarks; }
    public double getMidMarks()        { return midMarks; }
    public double getEndMarks()        { return endMarks; }

    // ── Setters ──
    public void setRegNo(String v)            { this.regNo = v; }
    public void setCCode(String v)            { this.cCode = v; }
    public void setQ1Marks(double v)          { this.q1Marks = v; }
    public void setQ2Marks(double v)          { this.q2Marks = v; }
    public void setQ3Marks(double v)          { this.q3Marks = v; }
    public void setAssignmentMarks(double v)  { this.assignmentMarks = v; }
    public void setMidMarks(double v)         { this.midMarks = v; }
    public void setEndMarks(double v)         { this.endMarks = v; }



    public double getBestTwoQuizAvg() {
        double[] quizzes = {q1Marks, q2Marks, q3Marks};
        java.util.Arrays.sort(quizzes);
        return round2((quizzes[1] + quizzes[2]) / 2.0);
    }


    public double getCAMarks() {
        double quizAvg = getBestTwoQuizAvg(); // out of 100

        double quizCA = (quizAvg / 100.0) * 10.0;
        double assignmentCA = (assignmentMarks / 100.0) * 10.0;
        double midCA = (midMarks / 100.0) * 20.0;

        return round2(quizCA + assignmentCA + midCA);
    }

    public double getFinalExamMarks() {
        return round2((endMarks / 100.0) * 60.0);
    }


    public double getTotalMarks() {
        return round2(getCAMarks() + getFinalExamMarks());
    }

    public String getGrade() {
        double total = getTotalMarks();
        if (total >= 85) return "A+";
        if (total >= 75) return "A";
        if (total >= 70) return "A-";
        if (total >= 65) return "B+";
        if (total >= 60) return "B";
        if (total >= 55) return "B-";
        if (total >= 50) return "C+";
        if (total >= 45) return "C";
        if (total >= 40) return "C-";
        if (total >= 35) return "D+";
        if (total >= 30) return "D";
        return "E";
    }


    public double getGradePoint() {
        return switch (getGrade()) {
            case "A+"  -> 4.0;
            case "A"   -> 4.0;
            case "A-"  -> 3.7;
            case "B+"  -> 3.3;
            case "B"   -> 3.0;
            case "B-"  -> 2.7;
            case "C+"  -> 2.3;
            case "C"   -> 2.0;
            case "C-"  -> 1.7;
            case "D+"  -> 1.3;
            case "D"   -> 1.0;
            default    -> 0.0;
        };
    }


    public boolean isCAEligible() {
        return getCAMarks() >= 16.0;
    }

    private double round2(double val) {
        return new BigDecimal(val).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
