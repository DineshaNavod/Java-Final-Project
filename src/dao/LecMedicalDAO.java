package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecMedicalDAO {

    public List<LecMedical> getByStudent(String regNo) throws SQLException {
        List<LecMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE reg_no=? ORDER BY submission_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public List<LecMedical> getAll() throws SQLException {
        List<LecMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical ORDER BY submission_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public boolean updateStatus(String medicalId, String status) throws SQLException {
        String sql = "UPDATE medical SET status=? WHERE medical_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status); ps.setString(2, medicalId);
            return ps.executeUpdate() > 0;
        }
    }

    private LecMedical map(ResultSet rs) throws SQLException {
        return new LecMedical(
            rs.getString("medical_id"),
            rs.getDate("submission_date"),
            rs.getString("description"),
            rs.getDate("affectted_start_date"),
            rs.getDate("affectted_end_date"),
            rs.getString("status"),
            rs.getString("reg_no"),
            rs.getString("session_id")
        );
    }
}
