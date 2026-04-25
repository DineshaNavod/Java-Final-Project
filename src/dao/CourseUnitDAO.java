package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseUnitDAO {

    public boolean addCourse(CourseUnit course) throws SQLException {
        String sql = "INSERT INTO course_unit (c_code, c_name, credit, is_theory, is_practicel) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, course.getCCode());
            ps.setString(2, course.getCName());
            ps.setInt(3, course.getCredit());
            ps.setString(4, course.getIsTheory());
            ps.setString(5, course.getIsPractical());
            return ps.executeUpdate() > 0;
        }
    }

    public List<CourseUnit> getAllCourses() throws SQLException {
        List<CourseUnit> list = new ArrayList<>();
        String sql = "SELECT * FROM course_unit";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapCourse(rs));
            }
        }
        return list;
    }

    public CourseUnit getCourseByCode(String code) throws SQLException {
        String sql = "SELECT * FROM course_unit WHERE c_code = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCourse(rs);
            }
        }
        return null;
    }

    public boolean updateCourse(CourseUnit course, String oldCode) throws SQLException {
        String sql = "UPDATE course_unit SET c_code=?, c_name=?, credit=?, is_theory=?, is_practicel=? WHERE c_code=?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, course.getCCode());
            ps.setString(2, course.getCName());
            ps.setInt(3, course.getCredit());
            ps.setString(4, course.getIsTheory());
            ps.setString(5, course.getIsPractical());
            ps.setString(6, oldCode);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCourse(String code) throws SQLException {
        String sql = "DELETE FROM course_unit WHERE c_code = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            return ps.executeUpdate() > 0;
        }
    }

    public int countCourses() throws SQLException {
        String sql = "SELECT COUNT(*) FROM course_unit";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    private CourseUnit mapCourse(ResultSet rs) throws SQLException {
        return new CourseUnit(
            rs.getString("c_code"),
            rs.getString("c_name"),
            rs.getInt("credit"),
            rs.getString("is_theory"),
            rs.getString("is_practicel")
        );
    }
}
