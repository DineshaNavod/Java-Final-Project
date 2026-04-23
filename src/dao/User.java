package dao;

public class User {
    private int userId;
    private String username;
    private String password;
    private int roleId;
    private String fullName;
    private String email;
    private String phone;
    private String profilePic;

    public User() {}

    public User(int userId, String username, String password, int roleId,
                String fullName, String email, String phone, String profilePic) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.profilePic = profilePic;
    }

    // Getters
    public int getUserId()      { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public int getRoleId()      { return roleId; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getProfilePic() { return profilePic; }

    // Setters
    public void setUserId(int userId)         { this.userId = userId; }
    public void setUsername(String username)   { this.username = username; }
    public void setPassword(String password)   { this.password = password; }
    public void setRoleId(int roleId)         { this.roleId = roleId; }
    public void setFullName(String fullName)   { this.fullName = fullName; }
    public void setEmail(String email)         { this.email = email; }
    public void setPhone(String phone)         { this.phone = phone; }
    public void setProfilePic(String pic)     { this.profilePic = pic; }

    public String getRoleName() {
        return switch (roleId) {
            case 1 -> "Admin";
            case 2 -> "Lecturer";
            case 3 -> "Technical Officer";
            case 4 -> "Undergraduate";
            default -> "Unknown";
        };
    }

    @Override
    public String toString() { return fullName + " (" + username + ")"; }
}
