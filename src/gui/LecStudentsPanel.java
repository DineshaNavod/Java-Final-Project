package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LecStudentsPanel extends JPanel {

    private final LecUndergraduateDAO ugDAO     = new LecUndergraduateDAO();
    private final LecMarksDAO marksDAO  = new LecMarksDAO();
    private final LecAttendanceDAO attDAO    = new LecAttendanceDAO();
    private final CourseUnitDAO    courseDAO = new CourseUnitDAO();

    private JTable  table;
    private DefaultTableModel model;
    private JComboBox<String> courseBox;

    public LecStudentsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadStudents();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🎓 Undergraduate Details"), BorderLayout.WEST);

        // FILTER ROW
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        JTextField searchField = UIComponents.styledField("Search by reg no or name…");
        searchField.setPreferredSize(new Dimension(220, 36));

        courseBox = new JComboBox<>();
        courseBox.setFont(AppTheme.FONT_BODY);
        courseBox.setPreferredSize(new Dimension(250, 36));
        courseBox.addItem("-- Select Course for Eligibility --");
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                courseBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        JButton eligBtn = UIComponents.outlineButton("✅ Check Eligibility");
        eligBtn.addActionListener(e -> checkEligibility());
        JButton refBtn  = UIComponents.outlineButton("↻ Refresh");
        refBtn.addActionListener(e -> loadStudents());

        filterRow.add(searchField); filterRow.add(courseBox);
        filterRow.add(eligBtn); filterRow.add(refBtn);


        String[] cols = {"Reg No", "Full Name", "Email", "Phone"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(cols);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);


        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton detailBtn = UIComponents.outlineButton("📋 Full Details");
        detailBtn.addActionListener(e -> showStudentFull());
        btnRow.add(detailBtn);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow, BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void loadStudents() {
        model.setRowCount(0);
        try {
            for (LecUndergraduate u : ugDAO.getAll())
                model.addRow(new Object[]{u.getRegNo(), u.getFullName(), u.getEmail(), u.getPhone()});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void checkEligibility() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Select a course for eligibility check."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Eligibility Check – " + cCode, true);
        dlg.setSize(720, 480);
        dlg.setLocationRelativeTo(this);

        String[] cols = {"Reg No", "Name", "CA/40", "Att%", "CA Eligible", "Att Eligible", "FINAL ELIGIBLE"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = UIComponents.styledTable(cols);
        tbl.setModel(dm);


        javax.swing.table.TableCellRenderer eligRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                if (!sel && col >= 4) {
                    String val = v == null ? "" : v.toString();
                    c.setForeground(val.contains("✅") ? AppTheme.SUCCESS : AppTheme.DANGER);
                    ((JLabel)c).setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    c.setBackground(row%2==0?Color.WHITE:new Color(0xF7FBF9));
                }
                return c;
            }
        };
        for (int i = 4; i <= 6; i++) tbl.getColumnModel().getColumn(i).setCellRenderer(eligRenderer);

        try {
            List<LecUndergraduate> students = ugDAO.getAll();
            for (LecUndergraduate u : students) {
                LecMarks m = marksDAO.getMarks(u.getRegNo(), cCode);
                double caMarks = m != null ? m.getCAMarks() : 0;
                boolean caOk  = m != null && m.isCAEligible();

                double[] pct  = attDAO.getStudentAttendancePct(u.getRegNo(), cCode, null);
                boolean attOk = pct[2] >= 80.0;

                dm.addRow(new Object[]{
                    u.getRegNo(), u.getFullName(),
                    m != null ? String.format("%.2f", caMarks) : "–",
                    String.format("%.1f%%", pct[2]),
                    caOk  ? "✅ YES" : "❌ NO",
                    attOk ? "✅ YES" : "❌ NO",
                    (caOk && attOk) ? "✅ ELIGIBLE" : "❌ NOT ELIGIBLE"
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.add(UIComponents.scrolled(tbl), BorderLayout.CENTER);
        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    private void showStudentFull() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student."); return; }
        String regNo = model.getValueAt(row, 0).toString();
        String name  = model.getValueAt(row, 1).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Details – " + regNo, true);
        dlg.setSize(560, 460);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("" + name);
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(8));
        JLabel regLbl = new JLabel("Reg No: " + regNo);
        regLbl.setFont(AppTheme.FONT_BODY);
        regLbl.setForeground(AppTheme.TEXT_SECONDARY);
        regLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(regLbl);
        content.add(Box.createVerticalStrut(20));

        // Marks per course
        JLabel marksTitle = new JLabel("Marks Summary");
        marksTitle.setFont(AppTheme.FONT_SUBTITLE);
        marksTitle.setForeground(AppTheme.PRIMARY_DARK);
        marksTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(marksTitle);
        content.add(Box.createVerticalStrut(8));

        try {
            List<LecMarks> mList = marksDAO.getByStudent(regNo);
            double totalGP = 0, totalCr = 0;
            for (LecMarks m : mList) {
                CourseUnit cu = courseDAO.getCourseByCode(m.getCCode());
                String cName  = cu != null ? cu.getCName() : m.getCCode();
                int cr        = cu != null ? cu.getCredit() : 0;

                UIComponents.RoundedPanel row2 = new UIComponents.RoundedPanel(8, Color.WHITE);
                row2.setLayout(new BorderLayout(8, 0));
                row2.setBorder(new EmptyBorder(10, 14, 10, 14));
                row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
                row2.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel info = new JPanel(new GridLayout(2, 1));
                info.setOpaque(false);
                JLabel cLbl = new JLabel(m.getCCode() + " – " + cName);
                cLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                cLbl.setForeground(AppTheme.TEXT_PRIMARY);
                JLabel detail = new JLabel(String.format(
                    "CA: %.2f/40  Final: %.2f/60  Total: %.2f  GP: %.1f  Cr: %d",
                    m.getCAMarks(), m.getFinalExamMarks(), m.getTotalMarks(), m.getGradePoint(), cr));
                detail.setFont(AppTheme.FONT_SMALL);
                detail.setForeground(AppTheme.TEXT_SECONDARY);
                info.add(cLbl); info.add(detail);

                JLabel g = new JLabel(m.getGrade());
                g.setFont(new Font("Segoe UI", Font.BOLD, 18));
                Color gc = m.getTotalMarks() >= 45 ? AppTheme.SUCCESS : AppTheme.DANGER;
                g.setForeground(gc);

                row2.add(info, BorderLayout.CENTER);
                row2.add(g,    BorderLayout.EAST);
                content.add(row2);
                content.add(Box.createVerticalStrut(6));
                totalGP += m.getGradePoint() * cr;
                totalCr += cr;
            }

            double sgpa = totalCr > 0 ? Math.round(totalGP/totalCr*100)/100.0 : 0;
            UIComponents.RoundedPanel sgpaBox = new UIComponents.RoundedPanel(10, new Color(0xEAF7F0));
            sgpaBox.setLayout(new FlowLayout(FlowLayout.CENTER, 16, 8));
            sgpaBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            sgpaBox.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel sl = new JLabel("SGPA: " + String.format("%.2f", sgpa));
            sl.setFont(new Font("Segoe UI", Font.BOLD, 15));
            sl.setForeground(AppTheme.PRIMARY_DARK);
            sgpaBox.add(sl);
            content.add(Box.createVerticalStrut(10));
            content.add(sgpaBox);

        } catch (SQLException ex) {
            content.add(new JLabel("Error: " + ex.getMessage()));
        }

        dlg.setContentPane(new JScrollPane(content));
        dlg.setVisible(true);
    }
}
