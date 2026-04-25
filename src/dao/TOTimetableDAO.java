package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TOTimetableDAO {

    public boolean addSession(TOTimetable t) throws SQLException {
        String sql = "INSERT INTO timetable (session_id, c_code, session_date, type, duration, lec_hall) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getSessionId());
            ps.setString(2, t.getCCode());
            ps.setDate(3, t.getSessionDate());
            ps.setString(4, t.getType());
            ps.setTime(5, t.getDuration());
            ps.setString(6, t.getLecHall());
            return ps.executeUpdate() > 0;
        }
    }

    public List<TOTimetable> getAllSessions() throws SQLException {
        List<TOTimetable> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable ORDER BY session_date";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapTimetable(rs));
        }
        return list;
    }

    public List<TOTimetable> getSessionsByCourse(String cCode) throws SQLException {
        List<TOTimetable> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE c_code = ? ORDER BY session_date";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTimetable(rs));
            }
        }
        return list;
    }

    public boolean updateSession(TOTimetable t) throws SQLException {
        String sql = "UPDATE timetable SET c_code=?, session_date=?, type=?, duration=?, lec_hall=? WHERE session_id=?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getCCode());
            ps.setDate(2, t.getSessionDate());
            ps.setString(3, t.getType());
            ps.setTime(4, t.getDuration());
            ps.setString(5, t.getLecHall());
            ps.setString(6, t.getSessionId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSession(String sessionId) throws SQLException {
        String sql = "DELETE FROM timetable WHERE session_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Get sessions for a specific date ─────────────────────────────
    public List<TOTimetable> getSessionsByDate(String date) throws SQLException {
        List<TOTimetable> list = new ArrayList<>();
        String sql = "SELECT * FROM timetable WHERE session_date = ? ORDER BY duration";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapTimetable(rs));
            }
        }
        return list;
    }

    public int countSessions() throws SQLException {
        String sql = "SELECT COUNT(*) FROM timetable";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private TOTimetable mapTimetable(ResultSet rs) throws SQLException {
        return new TOTimetable(
            rs.getString("session_id"),
            rs.getString("c_code"),
            rs.getDate("session_date"),
            rs.getString("type"),
            rs.getTime("duration"),
            rs.getString("lec_hall")
        );
    }
}
