import java.sql.*;

public class CGPAService {

    public static double getCGPA(String reg) {

        double totalPoints = 0;
        int credits = 0;

        try {
            Connection con = DBConnection.getConnection();

            String sql =
                    "SELECT m.q1_marks, m.q2_marks, m.q3_marks, " +
                            "m.assignment_marks, m.mid_marks, m.end_marks, " +
                            "COALESCE(c.credit,0) AS credit " +
                            "FROM mark m " +
                            "LEFT JOIN course_unit c ON m.c_code = c.c_code " +
                            "WHERE m.reg_no=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, reg);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                double total = calculateTotal(rs);
                int credit = rs.getInt("credit");

                if (credit <= 0) continue;

                totalPoints += GPAEngine.point(total) * credit;
                credits += credit;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return credits == 0 ? 0 : totalPoints / credits;
    }

    private static double calculateTotal(ResultSet rs) throws Exception {

        double q1 = rs.getDouble("q1_marks");
        double q2 = rs.getDouble("q2_marks");
        double q3 = rs.getDouble("q3_marks");
        double assignment = rs.getDouble("assignment_marks");
        double mid = rs.getDouble("mid_marks");
        double end = rs.getDouble("end_marks");

        double[] q = {q1, q2, q3};
        java.util.Arrays.sort(q);

        double caRaw = q[2] + q[1] + assignment + mid;
        double ca = (caRaw / 300.0) * 40.0;

        double endScaled = (end / 100.0) * 60.0;

        return ca + endScaled;
    }
}