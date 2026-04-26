package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UGGradesPanel extends JPanel {

    private final User          currentUser;
    private final LecMarksDAO      marksDAO  = new LecMarksDAO();
    private final CourseUnitDAO courseDAO = new CourseUnitDAO();

    public UGGradesPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("📊 Grades & GPA"), BorderLayout.WEST);

        // ── SGPA BANNER ──
        UIComponents.RoundedPanel banner = buildSGPABanner();
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // ── MARKS TABLE ──
        String[] cols = {"Course Code", "Course Name", "Q1", "Q2", "Q3",
                         "Best Quiz Avg", "Assignment", "Mid", "CA/40",
                         "End Mark", "Final/60", "Total", "Grade", "CA Eligible"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = UIComponents.styledTable(cols);
        table.setModel(model);

        // Column widths
        int[] widths = {90, 200, 45, 45, 45, 75, 75, 55, 60, 70, 65, 55, 55, 80};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Grade + CA Eligible colour renderer
        TableCellRenderer colRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    if (col == 12) { // Grade
                        setForeground(gradeColor(val));
                        setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    } else if (col == 13) { // CA Eligible
                        setForeground("YES".equals(val) ? AppTheme.SUCCESS : AppTheme.DANGER);
                        setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    } else {
                        setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY);
                    }
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(colRenderer);

        // Load data
        String regNo = currentUser.getUsername();
        try {
            List<LecMarks> marksList = marksDAO.getByStudent(regNo);
            for (LecMarks m : marksList) {
                CourseUnit cu = courseDAO.getCourseByCode(m.getCCode());
                String cName = cu != null ? cu.getCName() : "–";
                model.addRow(new Object[]{
                    m.getCCode(), cName,
                    fmt(m.getQ1Marks()), fmt(m.getQ2Marks()), fmt(m.getQ3Marks()),
                    fmt(m.getBestTwoQuizAvg()),
                    fmt(m.getAssignmentMarks()), fmt(m.getMidMarks()),
                    fmt(m.getCAMarks()), fmt(m.getEndMarks()),
                    fmt(m.getFinalExamMarks()), fmt(m.getTotalMarks()),
                    m.getGrade(), m.isCAEligible() ? "YES" : "NO"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading grades: " + e.getMessage());
        }

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);

        // Info note at bottom
        UIComponents.RoundedPanel infoBar = new UIComponents.RoundedPanel(8, new Color(0xEAF7F0));
        infoBar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 8));
        infoBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel infoLbl = new JLabel(
            "CA = Best 2 of 3 quiz avg + assignment + mid → scaled to 40  |  Final = end marks → scaled to 60  |  CA Eligible: CA ≥ 16/40");
        infoLbl.setFont(AppTheme.FONT_SMALL);
        infoLbl.setForeground(AppTheme.PRIMARY_DARK);
        infoBar.add(infoLbl);
        card.add(infoBar, BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(banner);
        center.add(Box.createVerticalStrut(16));
        center.add(card);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private UIComponents.RoundedPanel buildSGPABanner() {
        UIComponents.RoundedPanel banner = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        banner.setLayout(new GridLayout(1, 4, 0, 0));

        String regNo = currentUser.getUsername();
        double sgpa  = 0;
        int    totalCourses = 0;
        int    eligible = 0;
        double totalPct = 0;

        try {
            List<LecMarks> mList = marksDAO.getByStudent(regNo);
            totalCourses = mList.size();
            for (LecMarks m : mList) {
                if (m.isCAEligible()) eligible++;
            }
            sgpa = marksDAO.calculateSGPA(regNo);
        } catch (SQLException ignored) {}

        banner.add(buildBannerItem("SGPA",          String.format("%.2f", sgpa),    AppTheme.PRIMARY));
        banner.add(buildBannerItem("Courses Taken",  String.valueOf(totalCourses),   AppTheme.INFO));
        banner.add(buildBannerItem("CA Eligible",    eligible + " / " + totalCourses, AppTheme.SUCCESS));
        banner.add(buildBannerItem("Grade System",  "UGC No. 12-2024",              AppTheme.WARNING));
        return banner;
    }

    private JPanel buildBannerItem(String label, String value, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(accent);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel keyLbl = new JLabel(label);
        keyLbl.setFont(AppTheme.FONT_SMALL);
        keyLbl.setForeground(AppTheme.TEXT_MUTED);
        keyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(valLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(keyLbl);
        return p;
    }

    private String fmt(double v) { return String.format("%.2f", v); }

    private Color gradeColor(String g) {
        if (g == null) return AppTheme.TEXT_MUTED;
        return switch (g) {
            case "A+", "A"  -> new Color(0x1A7A3C);
            case "A-", "B+" -> AppTheme.SUCCESS;
            case "B", "B-"  -> AppTheme.INFO;
            case "C+", "C"  -> new Color(0xF39C12);
            case "C-", "D+","D" -> new Color(0xE67E22);
            default         -> AppTheme.DANGER;
        };
    }
}
