package dao;

public class Lecturer {
    private String lecId;
    private String name;
    private String phone;
    private String address;
    private String image;
    private String profilePic;
    private String email;

    public Lecturer() {}

    public Lecturer(String lecId, String name, String phone, String address,
                    String image, String profilePic, String email) {
        this.lecId      = lecId;
        this.name       = name;
        this.phone      = phone;
        this.address    = address;
        this.image      = image;
        this.profilePic = profilePic;
        this.email      = email;
    }

    public String getLecId()      { return lecId; }
    public String getName()       { return name; }
    public String getPhone()      { return phone; }
    public String getAddress()    { return address; }
    public String getImage()      { return image; }
    public String getProfilePic() { return profilePic; }
    public String getEmail()      { return email; }

    public void setLecId(String v)      { this.lecId = v; }
    public void setName(String v)       { this.name = v; }
    public void setPhone(String v)      { this.phone = v; }
    public void setAddress(String v)    { this.address = v; }
    public void setImage(String v)      { this.image = v; }
    public void setProfilePic(String v) { this.profilePic = v; }
    public void setEmail(String v)      { this.email = v; }
}
