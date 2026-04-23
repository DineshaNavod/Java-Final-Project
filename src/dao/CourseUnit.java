package dao;

public class CourseUnit {
    private String cCode;
    private String cName;
    private int credit;
    private String isTheory;
    private String isPractical;

    public CourseUnit() {}

    public CourseUnit(String cCode, String cName, int credit, String isTheory, String isPractical) {
        this.cCode = cCode;
        this.cName = cName;
        this.credit = credit;
        this.isTheory = isTheory;
        this.isPractical = isPractical;
    }

    public String getCCode()      { return cCode; }
    public String getCName()      { return cName; }
    public int getCredit()        { return credit; }
    public String getIsTheory()   { return isTheory; }
    public String getIsPractical(){ return isPractical; }

    public void setCCode(String cCode)          { this.cCode = cCode; }
    public void setCName(String cName)          { this.cName = cName; }
    public void setCredit(int credit)           { this.credit = credit; }
    public void setIsTheory(String isTheory)    { this.isTheory = isTheory; }
    public void setIsPractical(String isPractical) { this.isPractical = isPractical; }

    @Override
    public String toString() { return cCode + " - " + cName; }
}
