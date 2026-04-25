package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class TOMedicalDAO {

    // ── INSERT ────────────────────────────────────────────────────────
    public boolean addMedical(TOMedical m) throws SQLException {
        String sql = "INSERT INTO medical (medical_id,submission_date,description," +
                "affectted_start_date,affectted_end_date,status,reg_no,session_id) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getMedicalId());
            ps.setDate(2, m.getSubmissionDate());
            ps.setString(3, m.getDescription());
            ps.setDate(4, m.getAffectedStartDate());
            ps.setDate(5, m.getAffectedEndDate());
            ps.setString(6, m.getStatus());
            ps.setString(7, m.getRegNo());
            ps.setString(8, m.getSessionId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── UPDATE full record ────────────────────────────────────────────
    public boolean updateMedical(TOMedical m) throws SQLException {
        String sql = "UPDATE medical SET submission_date=?,description=?," +
                "affectted_start_date=?,affectted_end_date=?,status=?," +
                "reg_no=?,session_id=? WHERE medical_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, m.getSubmissionDate());
            ps.setString(2, m.getDescription());
            ps.setDate(3, m.getAffectedStartDate());
            ps.setDate(4, m.getAffectedEndDate());
            ps.setString(5, m.getStatus());
            ps.setString(6, m.getRegNo());
            ps.setString(7, m.getSessionId());
            ps.setString(8, m.getMedicalId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── UPDATE status only ────────────────────────────────────────────
    public boolean updateStatus(String medicalId, String status) throws SQLException {
        String sql = "UPDATE medical SET status=? WHERE medical_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, medicalId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────
    public boolean deleteMedical(String medicalId) throws SQLException {
        String sql = "DELETE FROM medical WHERE medical_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, medicalId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── GET ALL ───────────────────────────────────────────────────────
    public List<TOMedical> getAll() throws SQLException {
        List<TOMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical ORDER BY submission_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // ── GET BY STUDENT ────────────────────────────────────────────────
    public List<TOMedical> getByStudent(String regNo) throws SQLException {
        List<TOMedical> list = new ArrayList<>();
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

    // ── GET BY STATUS ─────────────────────────────────────────────────
    public List<TOMedical> getByStatus(String status) throws SQLException {
        List<TOMedical> list = new ArrayList<>();
        String sql = "SELECT * FROM medical WHERE status=? ORDER BY submission_date DESC";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── CHECK EXISTS ──────────────────────────────────────────────────
    public boolean existsById(String medicalId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM medical WHERE medical_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, medicalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    // ── PRIVATE MAPPER ────────────────────────────────────────────────
    private TOMedical map(ResultSet rs) throws SQLException {
        return new TOMedical(
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