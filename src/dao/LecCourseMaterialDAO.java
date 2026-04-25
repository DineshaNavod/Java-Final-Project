package dao;

import db.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class LecCourseMaterialDAO {

    public boolean addMaterial(LecCourseMaterial m) throws SQLException {
        String sql = "INSERT INTO materials (mat_id,c_code,title,link) VALUES(?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, m.getMatId()); ps.setString(2, m.getCCode());
            ps.setString(3, m.getTitle()); ps.setString(4, m.getLink());
            return ps.executeUpdate() > 0;
        }
    }

    public List<LecCourseMaterial> getByCourse(String cCode) throws SQLException {
        List<LecCourseMaterial> list = new ArrayList<>();
        String sql = "SELECT * FROM materials WHERE c_code=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cCode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new LecCourseMaterial(
                        rs.getString("mat_id"), rs.getString("c_code"),
                        rs.getString("title"),  rs.getString("link")));
                }
            }
        }
        return list;
    }

    public boolean deleteMaterial(String matId) throws SQLException {
        String sql = "DELETE FROM materials WHERE mat_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, matId);
            return ps.executeUpdate() > 0;
        }
    }
}
