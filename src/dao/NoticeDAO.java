package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public boolean addNotice(Notice notice) throws SQLException {
        String sql = "INSERT INTO notice (notice_id, notice) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notice.getNoticeId());
            ps.setString(2, notice.getNotice());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Notice> getAllNotices() throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT * FROM notice";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notice(rs.getInt("notice_id"), rs.getString("notice")));
            }
        }
        return list;
    }

    public boolean updateNotice(Notice notice) throws SQLException {
        String sql = "UPDATE notice SET notice = ? WHERE notice_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, notice.getNotice());
            ps.setInt(2, notice.getNoticeId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteNotice(int noticeId) throws SQLException {
        String sql = "DELETE FROM notice WHERE notice_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, noticeId);
            return ps.executeUpdate() > 0;
        }
    }

    public int countNotices() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notice";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
