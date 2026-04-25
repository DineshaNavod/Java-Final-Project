package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LecMarksPanel extends JPanel {

    private final LecMarksDAO marksDAO  = new LecMarksDAO();
    private final CourseUnitDAO    courseDAO = new CourseUnitDAO();
    private final LecUndergraduateDAO ugDAO     = new LecUndergraduateDAO();

    private JComboBox<String> courseBox;
    private JTable    marksTable;
    private DefaultTableModel tableModel;

    private static final String[] COLS = {
            "Reg No", "Name", "Q1", "Q2", "Q3", "Quiz Avg",
            "Assign", "Mid", "CA/40", "End Marks", "Final/60", "Total", "Grade", "CA Eligible"
    };

    public LecMarksPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("📊 Marks & Grades"), BorderLayout.WEST);

        JButton uploadBtn = UIComponents.primaryButton("⬆ Upload Marks");
        uploadBtn.addActionListener(e -> showUploadDialog());
        header.add(uploadBtn, BorderLayout.EAST);

        // ── FILTER ROW ──
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

        JButton loadBtn = UIComponents.outlineButton("🔍 Load");
        loadBtn.addActionListener(e -> loadMarks());

        JButton batchBtn = UIComponents.outlineButton("📋 Batch Summary");
        batchBtn.addActionListener(e -> showBatchSummary());

        filterRow.add(new JLabel("Course:"));
        filterRow.add(courseBox);
        filterRow.add(loadBtn);
        filterRow.add(batchBtn);

        // TABLE
        tableModel = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        marksTable = UIComponents.styledTable(COLS);
        marksTable.setModel(tableModel);

        // Column widths
        int[] widths = {85, 140, 45, 45, 45, 65, 55, 50, 60, 70, 65, 55, 50, 80};
        for (int i = 0; i < widths.length && i < marksTable.getColumnCount(); i++)
            marksTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Custom cell renderer: colour Grade column, CA Eligible column
        javax.swing.table.TableCellRenderer gradeRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    if (col == 12) { // Grade
                        c.setForeground(gradeColor(val));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (col == 13) { // CA Eligible
                        c.setForeground(val.equals("YES") ? AppTheme.SUCCESS : AppTheme.DANGER);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY);
                    }
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < marksTable.getColumnCount(); i++)
            marksTable.getColumnModel().getColumn(i).setCellRenderer(gradeRenderer);

        // ── STUDENT VIEW BUTTON ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton viewStudentBtn = UIComponents.outlineButton("👤 Student View");
        viewStudentBtn.addActionListener(e -> showStudentDetail());
        JButton editBtn = UIComponents.outlineButton("✏ Edit Marks");
        editBtn.addActionListener(e -> editSelectedMarks());
        btnRow.add(viewStudentBtn);
        btnRow.add(editBtn);

        // ── CARD ──
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                     BorderLayout.NORTH);
        card.add(UIComponents.scrolled(marksTable), BorderLayout.CENTER);
        card.add(btnRow,                        BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void loadMarks() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Please select a course."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();
        tableModel.setRowCount(0);
        try {
            List<LecMarks> list = marksDAO.getByCourse(cCode);
            List<LecUndergraduate> students = ugDAO.getAll();
            java.util.Map<String, String> nameMap = new java.util.HashMap<>();
            java.util.Map<String, String> statusMap = new java.util.HashMap<>();
            for (LecUndergraduate u : students) {
                String status = u.getStatus();
                if (status == null) status = "PROPER";
                statusMap.put(u.getRegNo(), status.toUpperCase());
            }

            for (LecMarks m : list) {

                String status = statusMap.getOrDefault(m.getRegNo(), "PROPER").toUpperCase();

                String realGrade = m.getGrade();

                // ⭐ FINAL RULE: REPEAT = C
                String displayGrade = status.contains("REPEAT")
                        ? "C"
                        : realGrade;

                tableModel.addRow(new Object[]{
                        m.getRegNo(),
                        nameMap.getOrDefault(m.getRegNo(), "–"),
                        fmt(m.getQ1Marks()),
                        fmt(m.getQ2Marks()),
                        fmt(m.getQ3Marks()),
                        fmt(m.getBestTwoQuizAvg()),
                        fmt(m.getAssignmentMarks()),
                        fmt(m.getMidMarks()),
                        fmt(m.getCAMarks()),
                        fmt(m.getEndMarks()),
                        fmt(m.getFinalExamMarks()),
                        fmt(m.getTotalMarks()),
                        displayGrade,   // ⭐ ONLY CHANGE
                        m.isCAEligible() ? "YES" : "NO"
                });
            }

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No marks found for this course.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showUploadDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Upload Marks", true);
        dlg.setSize(500, 520);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("⬆ Upload Student Marks");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Course select
        JComboBox<String> dlgCourseBox = new JComboBox<>();
        dlgCourseBox.setFont(AppTheme.FONT_BODY);
        dlgCourseBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        dlgCourseBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                dlgCourseBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        // Student select
        JComboBox<String> studentBox = new JComboBox<>();
        studentBox.setFont(AppTheme.FONT_BODY);
        studentBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        studentBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            for (LecUndergraduate u : ugDAO.getAll())
                studentBox.addItem(u.getRegNo() + " | " + u.getFullName());
        } catch (SQLException ignored) {}

        // Mark fields
        JTextField q1F   = makeMarkField("0");
        JTextField q2F   = makeMarkField("0");
        JTextField q3F   = makeMarkField("0");
        JTextField assF  = makeMarkField("0");
        JTextField midF  = makeMarkField("0");
        JTextField endF  = makeMarkField("0");

        // Live calculation label
        JLabel calcLbl = new JLabel("CA: –   Final: –   Total: –   Grade: –");
        calcLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        calcLbl.setForeground(AppTheme.PRIMARY);
        calcLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        javax.swing.event.DocumentListener calcListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { recalc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { recalc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalc(); }
            void recalc() {
                try {
                    LecMarks m = new LecMarks("", "",
                            parse(q1F), parse(q2F), parse(q3F),
                            parse(assF), parse(midF), parse(endF));
                    calcLbl.setText(String.format(
                            "CA: %.2f / 40   |   Final: %.2f / 60   |   Total: %.2f   |   Grade: %s   |   Eligible: %s",
                            m.getCAMarks(), m.getFinalExamMarks(), m.getTotalMarks(),
                            m.getGrade(), m.isCAEligible() ? "✅ YES" : "❌ NO"));
                    calcLbl.setForeground(m.isCAEligible() ? AppTheme.SUCCESS : AppTheme.DANGER);
                } catch (Exception ignored) {}
            }
        };
        q1F.getDocument().addDocumentListener(calcListener);
        q2F.getDocument().addDocumentListener(calcListener);
        q3F.getDocument().addDocumentListener(calcListener);
        assF.getDocument().addDocumentListener(calcListener);
        midF.getDocument().addDocumentListener(calcListener);
        endF.getDocument().addDocumentListener(calcListener);

        JButton saveBtn = UIComponents.primaryButton("💾 Save Marks");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String cCode  = dlgCourseBox.getSelectedItem().toString().split("\\|")[0].trim();
                String regNo  = studentBox.getSelectedItem().toString().split("\\|")[0].trim();
                LecMarks m = new LecMarks(regNo, cCode,
                        parse(q1F), parse(q2F), parse(q3F),
                        parse(assF), parse(midF), parse(endF));
                marksDAO.saveMarks(m);
                JOptionPane.showMessageDialog(dlg,
                        String.format("Marks saved!\nCA: %.2f/40  |  Final: %.2f/60  |  Total: %.2f  |  Grade: %s",
                                m.getCAMarks(), m.getFinalExamMarks(), m.getTotalMarks(), m.getGrade()));
                dlg.dispose();
                loadMarks();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dlg, "All marks must be numeric (0–100).");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        addRow(content, "Course",       dlgCourseBox);
        addRow(content, "Student",      studentBox);
        content.add(Box.createVerticalStrut(8));

        JPanel quizRow = new JPanel(new GridLayout(1, 3, 10, 0));
        quizRow.setOpaque(false);
        quizRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        quizRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        addLabeledField(quizRow, "Quiz 1 (/100)", q1F);
        addLabeledField(quizRow, "Quiz 2 (/100)", q2F);
        addLabeledField(quizRow, "Quiz 3 (/100)", q3F);
        content.add(new JLabel("📝 Quiz Marks (Best 2 of 3 will be used):") {{
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(AppTheme.TEXT_SECONDARY);
            setAlignmentX(LEFT_ALIGNMENT);
        }});
        content.add(Box.createVerticalStrut(4));
        content.add(quizRow);
        content.add(Box.createVerticalStrut(10));

        addRow(content, "Assignment (/100)", assF);
        addRow(content, "Mid Exam (/100)", midF);
        addRow(content, "End Exam (/100)", endF);
        content.add(Box.createVerticalStrut(10));

        // Calc box
        UIComponents.RoundedPanel calcBox = new UIComponents.RoundedPanel(8, new Color(0xEAF7F0));
        calcBox.setLayout(new BorderLayout());
        calcBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        calcBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        calcBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        calcBox.add(calcLbl);
        content.add(calcBox);
        content.add(Box.createVerticalStrut(14));
        content.add(saveBtn);

        dlg.setContentPane(new JScrollPane(content));
        dlg.setVisible(true);
    }

    private void editSelectedMarks() {
        int row = marksTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row to edit."); return; }
        String regNo = tableModel.getValueAt(row, 0).toString();
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) return;
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();
        try {
            LecMarks m = marksDAO.getMarks(regNo, cCode);
            if (m == null) { JOptionPane.showMessageDialog(this, "Marks not found."); return; }
            showEditDialog(m);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showEditDialog(LecMarks existing) {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Marks", true);
        dlg.setSize(420, 380);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("✏ Edit Marks — " + existing.getRegNo());
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField q1F  = makeMarkField(fmt(existing.getQ1Marks()));
        JTextField q2F  = makeMarkField(fmt(existing.getQ2Marks()));
        JTextField q3F  = makeMarkField(fmt(existing.getQ3Marks()));
        JTextField assF = makeMarkField(fmt(existing.getAssignmentMarks()));
        JTextField midF = makeMarkField(fmt(existing.getMidMarks()));
        JTextField endF = makeMarkField(fmt(existing.getEndMarks()));

        content.add(title);
        content.add(Box.createVerticalStrut(16));
        addRow(content, "Quiz 1",     q1F);
        addRow(content, "Quiz 2",     q2F);
        addRow(content, "Quiz 3",     q3F);
        addRow(content, "Assignment", assF);
        addRow(content, "Mid Exam",   midF);
        addRow(content, "End Exam",   endF);

        JButton saveBtn = UIComponents.primaryButton("💾 Update");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                existing.setQ1Marks(parse(q1F)); existing.setQ2Marks(parse(q2F));
                existing.setQ3Marks(parse(q3F)); existing.setAssignmentMarks(parse(assF));
                existing.setMidMarks(parse(midF)); existing.setEndMarks(parse(endF));
                marksDAO.saveMarks(existing);
                JOptionPane.showMessageDialog(dlg, "Marks updated!");
                dlg.dispose();
                loadMarks();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        content.add(Box.createVerticalStrut(14));
        content.add(saveBtn);
        dlg.setContentPane(new JScrollPane(content));
        dlg.setVisible(true);
    }

    private void showStudentDetail() {
        int row = marksTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a student row."); return; }
        String regNo = tableModel.getValueAt(row, 0).toString();
        String name  = tableModel.getValueAt(row, 1).toString();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Student: " + regNo, true);
        dlg.setSize(500, 400);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("🎓 " + name + "  (" + regNo + ")");
        title.setFont(AppTheme.FONT_TITLE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(20));

        try {
            List<LecMarks> mList = marksDAO.getByStudent(regNo);
            double totalGPts = 0, totalCreds = 0;
            for (LecMarks m : mList) {
                CourseUnit cu = courseDAO.getCourseByCode(m.getCCode());
                String cName = cu != null ? cu.getCName() : m.getCCode();
                int credits  = cu != null ? cu.getCredit() : 0;

                UIComponents.RoundedPanel row2 = new UIComponents.RoundedPanel(8, Color.WHITE);
                row2.setLayout(new BorderLayout(12, 0));
                row2.setBorder(new EmptyBorder(12, 16, 12, 16));
                row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
                row2.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel left = new JPanel(new GridLayout(2, 1));
                left.setOpaque(false);
                JLabel cn = new JLabel(m.getCCode() + " – " + cName);
                cn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                cn.setForeground(AppTheme.TEXT_PRIMARY);
                JLabel detail = new JLabel(String.format(
                        "CA: %.2f/40  |  Final: %.2f/60  |  Total: %.2f  |  Credits: %d",
                        m.getCAMarks(), m.getFinalExamMarks(), m.getTotalMarks(), credits));
                detail.setFont(AppTheme.FONT_SMALL);
                detail.setForeground(AppTheme.TEXT_SECONDARY);
                left.add(cn); left.add(detail);

                JLabel gradeLbl = new JLabel(m.getGrade());
                gradeLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
                gradeLbl.setForeground(gradeColor(m.getGrade()));

                row2.add(left,     BorderLayout.CENTER);
                row2.add(gradeLbl, BorderLayout.EAST);
                content.add(row2);
                content.add(Box.createVerticalStrut(8));

                totalGPts  += m.getGradePoint() * credits;
                totalCreds += credits;
            }

            double sgpa = 0;
            double cgpa = 0;

            try {
                sgpa = marksDAO.calculateSGPA(regNo);
                cgpa = marksDAO.calculateCGPA(regNo);
            } catch (SQLException ignored) {}

            UIComponents.RoundedPanel gpaBox = new UIComponents.RoundedPanel(10, new Color(0xEAF7F0));
            gpaBox.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
            gpaBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            gpaBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel sgpaLbl = new JLabel("SGPA: " + String.format("%.2f", sgpa));
            sgpaLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));

            JLabel cgpaLbl = new JLabel("CGPA: " + String.format("%.2f", cgpa));
            cgpaLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));

            gpaBox.add(sgpaLbl);
            gpaBox.add(cgpaLbl);

            content.add(Box.createVerticalStrut(10));
            content.add(gpaBox);
        } catch (SQLException ex) {
            content.add(new JLabel("Error: " + ex.getMessage()));
        }

        dlg.setContentPane(new JScrollPane(content));
        dlg.setVisible(true);
    }

    private void showBatchSummary() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Select a course first."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Batch Summary – " + cCode, true);
        dlg.setSize(700, 460);
        dlg.setLocationRelativeTo(this);

        String[] cols = {"Reg No", "Name", "CA/40", "Final/60", "Total", "Grade", "GP", "Eligible"};
        JTable tbl = UIComponents.styledTable(cols);
        DefaultTableModel dm = (DefaultTableModel) tbl.getModel();

        try {
            List<LecMarks> list = marksDAO.getByCourse(cCode);
            List<LecUndergraduate> students = ugDAO.getAll();
            java.util.Map<String, String> nameMap = new java.util.HashMap<>();
            students.forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            long eligible = list.stream().filter(LecMarks::isCAEligible).count();
            for (LecMarks m : list) {
                dm.addRow(new Object[]{
                        m.getRegNo(), nameMap.getOrDefault(m.getRegNo(), "–"),
                        fmt(m.getCAMarks()), fmt(m.getFinalExamMarks()),
                        fmt(m.getTotalMarks()), m.getGrade(),
                        String.format("%.1f", m.getGradePoint()),
                        m.isCAEligible() ? "✅ YES" : "❌ NO"
                });
            }

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(new EmptyBorder(16, 16, 16, 16));

            UIComponents.RoundedPanel summBar = new UIComponents.RoundedPanel(8, new Color(0xEAF7F0));
            summBar.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 8));
            summBar.add(makeStat("Total Students", String.valueOf(list.size())));
            summBar.add(makeStat("CA Eligible",    String.valueOf(eligible)));
            summBar.add(makeStat("Not Eligible",   String.valueOf(list.size() - eligible)));
            panel.add(summBar, BorderLayout.NORTH);
            panel.add(UIComponents.scrolled(tbl), BorderLayout.CENTER);
            dlg.setContentPane(panel);
        } catch (SQLException ex) {
            dlg.setContentPane(new JLabel("Error: " + ex.getMessage()));
        }
        dlg.setVisible(true);
    }

    // ── helpers ──────────────────────────────────────────────────────
    private JLabel makeStat(String label, String val) {
        JLabel lbl = new JLabel("<html><b>" + val + "</b><br><small>" + label + "</small></html>");
        lbl.setFont(AppTheme.FONT_BODY);
        lbl.setForeground(AppTheme.PRIMARY_DARK);
        return lbl;
    }

    private void addRow(JPanel panel, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }

    private void addLabeledField(JPanel panel, String label, JTextField field) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        col.add(lbl);
        col.add(Box.createVerticalStrut(3));
        col.add(field);
        panel.add(col);
    }

    private JTextField makeMarkField(String def) {
        JTextField f = UIComponents.styledField(def);
        f.setText(def);
        return f;
    }

    private double parse(JTextField f) {
        String t = f.getText().trim();
        return t.isEmpty() ? 0.0 : Double.parseDouble(t);
    }

    private String fmt(double v) { return String.format("%.2f", v); }

    private Color gradeColor(String g) {
        if (g == null) return AppTheme.TEXT_MUTED;
        return switch (g) {
            case "A+", "A"  -> new Color(0x1A7A3C);
            case "A-", "B+" -> new Color(0x27AE60);
            case "B", "B-"  -> new Color(0x2980B9);
            case "C+", "C"  -> new Color(0xF39C12);
            case "C-", "D+",
                 "D"        -> new Color(0xE67E22);
            default         -> AppTheme.DANGER;
        };
    }
}
