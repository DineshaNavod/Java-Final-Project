package dao;

public class LecCourseMaterial {
    private String matId;
    private String cCode;
    private String title;
    private String link;

    public LecCourseMaterial() {}

    public LecCourseMaterial(String matId, String cCode, String title, String link) {
        this.matId = matId;
        this.cCode = cCode;
        this.title = title;
        this.link  = link;
    }

    public String getMatId() { return matId; }
    public String getCCode() { return cCode; }
    public String getTitle() { return title; }
    public String getLink()  { return link; }

    public void setMatId(String v) { this.matId = v; }
    public void setCCode(String v) { this.cCode = v; }
    public void setTitle(String v) { this.title = v; }
    public void setLink(String v)  { this.link = v; }
}
