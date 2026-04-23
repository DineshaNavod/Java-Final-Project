package dao;

public class Notice {
    private int noticeId;
    private String notice;

    public Notice() {}

    public Notice(int noticeId, String notice) {
        this.noticeId = noticeId;
        this.notice = notice;
    }

    public int getNoticeId()   { return noticeId; }
    public String getNotice()  { return notice; }

    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }
    public void setNotice(String notice)  { this.notice = notice; }

    @Override
    public String toString() { return notice; }
}
