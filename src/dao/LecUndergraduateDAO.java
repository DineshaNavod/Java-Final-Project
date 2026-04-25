package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecUndergraduateDAO {

    public List<LecUndergraduate> getAll() throws SQLException {
        List<LecUndergraduate> list = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, email, phone FROM users WHERE role_id=4 ORDER BY username";
        try (Connection c = DatabaseConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new LecUndergraduate(
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getInt("user_id")
                ));
            }
        }
        return list;
    }

    public LecUndergraduate getByRegNo(String regNo) throws SQLException {
        String sql = "SELECT user_id, username, full_name, email, phone FROM users WHERE username=? AND role_id=4";
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
