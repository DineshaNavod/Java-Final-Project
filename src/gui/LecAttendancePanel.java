package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class LecAttendancePanel extends JPanel {

    private final LecAttendanceDAO attDAO   = new LecAttendanceDAO();
    private final CourseUnitDAO  courseDAO= new CourseUnitDAO();
    private final LecUndergraduateDAO ugDAO  = new LecUndergraduateDAO();

    private JComboBox<String> courseBox, typeBox;
    private JTable  batchTable;
    private DefaultTableModel batchModel;

    public LecAttendancePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("📅 Attendance Records"), BorderLayout.WEST);


        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        courseBox = new JComboBox<>();
        courseBox.setFont(AppTheme.FONT_BODY);
        courseBox.setPreferredSize(new Dimension(260, 36));
        courseBox.addItem("-- Select Course --");
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                courseBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        typeBox = new JComboBox<>(new String[]{"Combined", "theory", "practical"});
        typeBox.setFont(AppTheme.FONT_BODY);
        typeBox.setPreferredSize(new Dimension(130, 36));

        JButton loadBtn    = UIComponents.outlineButton("🔍 Load Batch");
        JButton studentBtn = UIComponents.outlineButton("👤 Individual");

        loadBtn.addActionListener(e -> loadBatchSummary());
        studentBtn.addActionListener(e -> showIndividualDialog());

        filterRow.add(new JLabel("Course:")); filterRow.add(courseBox);
        filterRow.add(new JLabel("Type:"));   filterRow.add(typeBox);
        filterRow.add(loadBtn);
        filterRow.add(studentBtn);

        // ── BATCH TABLE ──
        String[] cols = {"Reg No", "Student Name", "Total Sessions", "Present", "Absent", "Att %", "Status"};
        batchModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        batchTable = UIComponents.styledTable(cols);
        batchTable.setModel(batchModel);
        batchTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        batchTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        batchTable.getColumnModel().getColumn(6).setPreferredWidth(120);


        batchTable.getColumnModel().getColumn(6).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                    if (!sel) {
                        String val = v == null ? "" : v.toString();
                        if (val.contains("✅"))       c.setForeground(AppTheme.SUCCESS);
                        else if (val.contains("⚠"))  c.setForeground(AppTheme.WARNING);
                        else                          c.setForeground(AppTheme.DANGER);
                        ((JLabel) c).setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                    }
                    return c;
                }
            });

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow, BorderLayout.NORTH);
        card.add(UIComponents.scrolled(batchTable), BorderLayout.CENTER);
        card.add(buildLegend(), BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 8));
        p.setOpaque(false);
        p.add(legendItem("✅ > 80%",        AppTheme.SUCCESS));
        p.add(legendItem("⚠ = 80%",         AppTheme.WARNING));
        p.add(legendItem("❌ < 80%",         AppTheme.DANGER));
        p.add(legendItem("(M) Has Medical", AppTheme.INFO));
        return p;
    }

    private JLabel legendItem(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(color);
        return l;
    }

    private void loadBatchSummary() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();
        String type  = typeBox.getSelectedItem().toString().equalsIgnoreCase("combined")
                       ? null : typeBox.getSelectedItem().toString();

        batchModel.setRowCount(0);
        try {
            Map<String, double[]> summary = attDAO.getAttendanceSummaryByCourse(cCode, type);
            List<LecUndergraduate> students = ugDAO.getAll();
            Map<String, String> nameMap = new HashMap<>();
            students.forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            for (Map.Entry<String, double[]> e : summary.entrySet()) {
                String reg   = e.getKey();
                double[] arr = e.getValue(); // [total, present, pct]
                double total   = arr[0], present = arr[1], pct = arr[2];
                double absent  = total - present;

                String status;
                if (pct > 80)      status = "Above 80%";
                else if (pct == 80) status = "Exactly 80%";
                else               status = "Below 80%";

                batchModel.addRow(new Object[]{
                    reg, nameMap.getOrDefault(reg, "–"),
                    (int)total, (int)present, (int)absent,
                    String.format("%.1f%%", pct),
                    status
                });
            }
            if (summary.isEmpty())
                JOptionPane.showMessageDialog(this, "No attendance records found for this course/type.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showIndividualDialog() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Individual Attendance", true);
        dlg.setSize(600, 480);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(AppTheme.BG_MAIN);


        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setOpaque(false);
        JComboBox<String> ugBox   = new JComboBox<>();
        JComboBox<String> typeBox2 = new JComboBox<>(new String[]{"combined", "theory", "practical"});
        ugBox.setFont(AppTheme.FONT_BODY);
        ugBox.setPreferredSize(new Dimension(230, 34));
        typeBox2.setFont(AppTheme.FONT_BODY);
        try {
            for (LecUndergraduate u : ugDAO.getAll())
                ugBox.addItem(u.getRegNo() + " | " + u.getFullName());
        } catch (SQLException ignored) {}

        JButton goBtn = UIComponents.primaryButton("Load");

        String[] detCols = {"Date", "Type", "Status", "Session"};
        DefaultTableModel detModel = new DefaultTableModel(detCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable detTable = UIComponents.styledTable(detCols);
        detTable.setModel(detModel);

        JLabel summaryLbl = new JLabel(" ");
        summaryLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        summaryLbl.setForeground(AppTheme.PRIMARY_DARK);
        summaryLbl.setBorder(new EmptyBorder(8, 0, 4, 0));

        goBtn.addActionListener(e -> {
            String regNo = ugBox.getSelectedItem().toString().split("\\|")[0].trim();
            String type2 = typeBox2.getSelectedItem().toString();
            detModel.setRowCount(0);
            try {
                List<LecAttendance> atts = type2.equalsIgnoreCase("combined")
                        ? attDAO.getByStudentAndCourse(regNo, cCode)
                        : attDAO.getByStudentCourseType(regNo, cCode, type2);
                for (LecAttendance a : atts)
                    detModel.addRow(new Object[]{a.getAttenDate(), a.getType(), a.getStatus(), a.getSessionId()});

                double[] pctArr = attDAO.getStudentAttendancePct(regNo, cCode, type2);
                summaryLbl.setText(String.format(
                    "Total: %d  |  Present: %d  |  Absent: %d  |  Attendance: %.1f%%",
                    (int)pctArr[0], (int)pctArr[1],
                    (int)(pctArr[0]-pctArr[1]), pctArr[2]));
                summaryLbl.setForeground(pctArr[2] >= 80 ? AppTheme.SUCCESS : AppTheme.DANGER);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        top.add(new JLabel("Student:")); top.add(ugBox);
        top.add(new JLabel("Type:"));   top.add(typeBox2);
        top.add(goBtn);

        content.add(top,                        BorderLayout.NORTH);
        content.add(UIComponents.scrolled(detTable), BorderLayout.CENTER);
        content.add(summaryLbl,                 BorderLayout.SOUTH);

        dlg.setContentPane(content);
        dlg.setVisible(true);
    }
}
