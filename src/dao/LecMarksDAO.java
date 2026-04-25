package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecMarksDAO {

    //  insert or update
    public boolean saveMarks(LecMarks m) throws SQLException {
        String sql = "INSERT INTO marks (reg_no,c_code,q1_marks,q2_marks,q3_marks," +
                "assignment_marks,mid_marks,end_marks) VALUES(?,?,?,?,?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE " +
                "q1_marks=VALUES(q1_marks), q2_marks=VALUES(q2_marks), " +
                "q3_marks=VALUES(q3_marks), assignment_marks=VALUES(assignment_marks), " +
                "mid_marks=VALUES(mid_marks), end_marks=VALUES(end_marks)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getRegNo());
            ps.setString(2, m.getCCode());
            ps.setDouble(3, m.getQ1Marks());
            ps.setDouble(4, m.getQ2Marks());
            ps.setDouble(5, m.getQ3Marks());
            ps.setDouble(6, m.getAssignmentMarks());
            ps.setDouble(7, m.getMidMarks());
            ps.setDouble(8, m.getEndMarks());
            return ps.executeUpdate() > 0;
        }
    }

    //Get one student + one course
    public LecMarks getMarks(String regNo, String cCode) throws SQLException {
        String sql = "SELECT * FROM marks WHERE reg_no=? AND c_code=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo); ps.setString(2, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    //  All marks for a course
    public List<LecMarks> getByCourse(String cCode) throws SQLException {
        List<LecMarks> list = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE c_code=? ORDER BY reg_no";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // All marks for a student
    public List<LecMarks> getByStudent(String regNo) throws SQLException {
        List<LecMarks> list = new ArrayList<>();
        String sql = "SELECT * FROM marks WHERE reg_no=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    //  SGPA for a student
    public double calculateSGPA(String regNo) throws SQLException {
        List<LecMarks> marksList = getByStudent(regNo);
        if (marksList.isEmpty()) return 0.0;
        CourseUnitDAO cuDao = new CourseUnitDAO();
        double totalPoints = 0, totalCredits = 0;
        for (LecMarks m : marksList) {
            try {
                CourseUnit cu = cuDao.getCourseByCode(m.getCCode());
                if (cu != null) {
                    double gp      = m.getGradePoint();
                    int    credits = cu.getCredit();
                    totalPoints  += gp * credits;
                    totalCredits += credits;
                }
            } catch (SQLException ignored) {}
        }
        return totalCredits > 0
                ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0
                : 0.0;
    }

    // CGPA for a student (ALL semesters)
    public double calculateCGPA(String regNo) throws SQLException {
        List<LecMarks> marksList = getByStudent(regNo);
        if (marksList.isEmpty()) return 0.0;

        CourseUnitDAO cuDao = new CourseUnitDAO();

        double totalPoints = 0;
        double totalCredits = 0;

        for (LecMarks m : marksList) {
            CourseUnit cu = cuDao.getCourseByCode(m.getCCode());
            if (cu != null) {
                double gp = m.getGradePoint();
                int credits = cu.getCredit();

                totalPoints += gp * credits;
                totalCredits += credits;
            }
        }

        return totalCredits > 0
                ? Math.round((totalPoints / totalCredits) * 100.0) / 100.0
                : 0.0;
    }

    private LecMarks map(ResultSet rs) throws SQLException {
        return new LecMarks(
                rs.getString("reg_no"), rs.getString("c_code"),
                rs.getDouble("q1_marks"), rs.getDouble("q2_marks"),
                rs.getDouble("q3_marks"), rs.getDouble("assignment_marks"),
                rs.getDouble("mid_marks"), rs.getDouble("end_marks")
        );
    }
}
