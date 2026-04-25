package dao;

public class LecUndergraduate {
    private String regNo;      // username in users table
    private String fullName;
    private String email;
    private String phone;
    private int    userId;

    public LecUndergraduate() {}

    public LecUndergraduate(String regNo, String fullName, String email, String phone, int userId) {
        this.regNo    = regNo;
        this.fullName = fullName;
        this.email    = email;
        this.phone    = phone;
        this.userId   = userId;
    }

    public String getRegNo()    { return regNo; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public int    getUserId()   { return userId; }

    public void setRegNo(String v)    { this.regNo = v; }
    public void setFullName(String v) { this.fullName = v; }
    public void setEmail(String v)    { this.email = v; }
    public void setPhone(String v)    { this.phone = v; }
    public void setUserId(int v)      { this.userId = v; }

    @Override
    public String toString() { return regNo + " – " + fullName; }
}
