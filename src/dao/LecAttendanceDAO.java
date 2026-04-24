package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecAttendanceDAO {

    // ── INSERT ────────────────────────────────────────────────────────
    public boolean addAttendance(LecAttendance a) throws SQLException {
        String sql = "INSERT INTO attendance (att_id,type,atten_date,status,reg_no,session_id) VALUES(?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getAttId());
            ps.setString(2, a.getType());
            ps.setDate(3, a.getAttenDate());
            ps.setString(4, a.getStatus());
            ps.setString(5, a.getRegNo());
            ps.setString(6, a.getSessionId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── ALL for a student ────────────────────────────────────────────
    public List<LecAttendance> getByStudent(String regNo) throws SQLException {
        List<LecAttendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE reg_no=? ORDER BY atten_date";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── ALL for a student + course (via session) ─────────────────────
    public List<LecAttendance> getByStudentAndCourse(String regNo, String cCode) throws SQLException {
        List<LecAttendance> list = new ArrayList<>();
        String sql = "SELECT a.* FROM attendance a " +
                     "JOIN timetable t ON a.session_id=t.session_id " +
                     "WHERE a.reg_no=? AND t.c_code=? ORDER BY a.atten_date";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo); ps.setString(2, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── ALL for a student + course + type ────────────────────────────
    public List<LecAttendance> getByStudentCourseType(String regNo, String cCode, String type) throws SQLException {
        List<LecAttendance> list = new ArrayList<>();
        String sql = "SELECT a.* FROM attendance a " +
                     "JOIN timetable t ON a.session_id=t.session_id " +
                     "WHERE a.reg_no=? AND t.c_code=? AND a.type=? ORDER BY a.atten_date";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo); ps.setString(2, cCode); ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── All records for a session ────────────────────────────────────
    public List<LecAttendance> getBySession(String sessionId) throws SQLException {
        List<LecAttendance> list = new ArrayList<>();
        String sql = "SELECT * FROM attendance WHERE session_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── Summary: attendance % per student per course ─────────────────
    // Returns map: regNo -> {total, present, percent}
    public Map<String, double[]> getAttendanceSummaryByCourse(String cCode, String type) throws SQLException {
        Map<String, double[]> map = new LinkedHashMap<>();
        String typeFilter = (type == null || type.equalsIgnoreCase("combined")) ? "" : " AND a.type='" + type + "'";
        String sql = "SELECT a.reg_no, " +
                     "COUNT(*) AS total, " +
                     "SUM(CASE WHEN a.status='present' THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance a " +
                     "JOIN timetable t ON a.session_id=t.session_id " +
                     "WHERE t.c_code=?" + typeFilter +
                     " GROUP BY a.reg_no";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String reg   = rs.getString("reg_no");
                    double total   = rs.getDouble("total");
                    double present = rs.getDouble("present");
                    double pct   = total > 0 ? (present / total) * 100 : 0;
                    map.put(reg, new double[]{total, present, pct});
                }
            }
        }
        return map;
    }

    // ── Count present/absent for one student+course ──────────────────
    public double[] getStudentAttendancePct(String regNo, String cCode, String type) throws SQLException {
        String typeFilter = (type == null || type.equalsIgnoreCase("combined")) ? "" : " AND a.type=?";
        String sql = "SELECT COUNT(*) AS total, " +
                     "SUM(CASE WHEN a.status='present' THEN 1 ELSE 0 END) AS present " +
                     "FROM attendance a JOIN timetable t ON a.session_id=t.session_id " +
                     "WHERE a.reg_no=? AND t.c_code=?" + typeFilter;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo); ps.setString(2, cCode);
            if (!typeFilter.isEmpty()) ps.setString(3, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double total   = rs.getDouble("total");
                    double present = rs.getDouble("present");
                    double pct   = total > 0 ? (present / total) * 100.0 : 0;
                    return new double[]{total, present, pct};
                }
            }
        }
        return new double[]{0, 0, 0};
    }

    // ── UPDATE status ────────────────────────────────────────────────
    public boolean updateStatus(String attId, String status) throws SQLException {
        String sql = "UPDATE attendance SET status=? WHERE att_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setString(2, attId);
            return ps.executeUpdate() > 0;
        }
    }

    private LecAttendance map(ResultSet rs) throws SQLException {
        return new LecAttendance(
            rs.getString("att_id"), rs.getString("type"),
            rs.getDate("atten_date"), rs.getString("status"),
            rs.getString("reg_no"), rs.getString("session_id")
        );
    }
}
