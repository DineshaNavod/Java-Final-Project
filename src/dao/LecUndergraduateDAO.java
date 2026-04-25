package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecUndergraduateDAO {

    public List<LecUndergraduate> getAll() throws SQLException {
        List<LecUndergraduate> list = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, s.status " +
                "FROM users u " +
                "JOIN student s ON u.user_id = s.user_id " +
                "WHERE u.role_id = 4 " +
                "ORDER BY u.username";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {

                LecUndergraduate u = new LecUndergraduate(
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getInt("user_id")
                );

                u.setStatus(rs.getString("status"));

                list.add(u);
            }
        }
        return list;
    }

    public LecUndergraduate getByRegNo(String regNo) throws SQLException {
        String sql = "SELECT user_id, username, full_name, email, phone, status \n" +
                "FROM users WHERE username=? AND role_id=4";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, regNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return new LecUndergraduate(
                    rs.getString("username"), rs.getString("full_name"),
                    rs.getString("email"), rs.getString("phone"), rs.getInt("user_id"));
            }
        }
        return null;
    }

    public int count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id=4";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
}
