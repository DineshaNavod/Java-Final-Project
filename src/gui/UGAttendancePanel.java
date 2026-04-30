package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class UGAttendancePanel extends JPanel {

    private final User          currentUser;
    private final LecAttendanceDAO attDAO    = new LecAttendanceDAO();
    private final CourseUnitDAO courseDAO = new CourseUnitDAO();
    private final LecMedicalDAO    medDAO    = new LecMedicalDAO();

    private JTable            detailTable;
    private DefaultTableModel detailModel;
    private JComboBox<String> courseBox, typeBox;


    private JLabel totalLbl, presentLbl, absentLbl, pctLbl, statusLbl;

    public UGAttendancePanel(User user) {
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
        header.add(UIComponents.sectionTitle("📅 My Attendance"), BorderLayout.WEST);


        UIComponents.RoundedPanel summaryBanner = buildSummaryBanner();


        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(16, 0, 12, 0));

        courseBox = new JComboBox<>();
        courseBox.setFont(AppTheme.FONT_BODY);
        courseBox.setPreferredSize(new Dimension(270, 36));
        courseBox.addItem("All Courses");
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                courseBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        typeBox = new JComboBox<>(new String[]{"Combined", "theory", "practical"});
        typeBox.setFont(AppTheme.FONT_BODY);
        typeBox.setPreferredSize(new Dimension(130, 36));

        JButton loadBtn = UIComponents.primaryButton("🔍 View");
        loadBtn.addActionListener(e -> loadDetails());
        courseBox.addActionListener(e -> loadDetails());
        typeBox.addActionListener(e -> loadDetails());

        filterRow.add(new JLabel("Course:")); filterRow.add(courseBox);
        filterRow.add(new JLabel("Type:"));   filterRow.add(typeBox);
        filterRow.add(loadBtn);


        String[] cols = {"Date", "Type", "Status", "Session ID"};
        detailModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        detailTable = UIComponents.styledTable(cols);
        detailTable.setModel(detailModel);
        detailTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        detailTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        detailTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        detailTable.getColumnModel().getColumn(3).setPreferredWidth(100);


        detailTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    setForeground("present".equals(val) ? AppTheme.SUCCESS : AppTheme.DANGER);
                    setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(0, 20, 20, 20));
        card.add(filterRow,                        BorderLayout.NORTH);
        card.add(UIComponents.scrolled(detailTable), BorderLayout.CENTER);
        card.add(buildLegend(),                    BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(summaryBanner);
        center.add(Box.createVerticalStrut(16));
        center.add(card);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);


        loadDetails();
    }

    private UIComponents.RoundedPanel buildSummaryBanner() {
        UIComponents.RoundedPanel banner = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        banner.setLayout(new GridLayout(1, 5, 0, 0));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 88));

        totalLbl   = bannerVal("–");
        presentLbl = bannerVal("–");
        absentLbl  = bannerVal("–");
        pctLbl     = bannerVal("–");
        statusLbl  = bannerVal("–");

        banner.add(wrapBannerItem("Total Sessions",  totalLbl,   AppTheme.PRIMARY));
        banner.add(wrapBannerItem("Present",         presentLbl, AppTheme.SUCCESS));
        banner.add(wrapBannerItem("Absent",          absentLbl,  AppTheme.DANGER));
        banner.add(wrapBannerItem("Attendance %",    pctLbl,     AppTheme.INFO));
        banner.add(wrapBannerItem("Status",          statusLbl,  AppTheme.WARNING));
        return banner;
    }

    private JLabel bannerVal(String v) {
        JLabel l = new JLabel(v);
        l.setFont(new Font("Segoe UI", Font.BOLD, 20));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel wrapBannerItem(String label, JLabel valLbl, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16, 20, 16, 20));
        valLbl.setForeground(accent);
        JLabel keyLbl = new JLabel(label);
        keyLbl.setFont(AppTheme.FONT_SMALL);
        keyLbl.setForeground(AppTheme.TEXT_MUTED);
        keyLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(valLbl);
        p.add(Box.createVerticalStrut(4));
        p.add(keyLbl);
        return p;
    }

    private void loadDetails() {
        detailModel.setRowCount(0);
        String regNo = currentUser.getUsername();
        int courseIdx = courseBox.getSelectedIndex();
        String typeStr = typeBox.getSelectedItem().toString();
        String type = "Combined".equals(typeStr) ? null : typeStr;

        try {
            List<LecAttendance> list;
            if (courseIdx <= 0) {

                list = attDAO.getByStudent(regNo);
                if (type != null) {
                    list = list.stream()
                            .filter(a -> type.equals(a.getType()))
                            .collect(java.util.stream.Collectors.toList());
                }
            } else {
                String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();
                if (type != null) {
                    list = attDAO.getByStudentCourseType(regNo, cCode, type);
                } else {
                    list = attDAO.getByStudentAndCourse(regNo, cCode);
                }
            }

            long present = 0, total = list.size();
            for (LecAttendance a : list) {
                detailModel.addRow(new Object[]{
                        a.getAttenDate(), a.getType(), a.getStatus(), a.getSessionId()
                });
                if ("present".equals(a.getStatus())) present++;
            }

            long absent = total - present;
            double pct = total > 0 ? (present * 100.0 / total) : 0;
            totalLbl.setText(String.valueOf(total));
            presentLbl.setText(String.valueOf(present));
            absentLbl.setText(String.valueOf(absent));
            pctLbl.setText(String.format("%.1f%%", pct));

            if (pct > 80)       { statusLbl.setText("Good");    statusLbl.setForeground(AppTheme.SUCCESS); }
            else if (pct == 80) { statusLbl.setText("Borderline"); statusLbl.setForeground(AppTheme.WARNING); }
            else                { statusLbl.setText("Low");     statusLbl.setForeground(AppTheme.DANGER); }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private JPanel buildLegend() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 8));
        p.setOpaque(false);
        addLegendItem(p, "✅ present",     AppTheme.SUCCESS);
        addLegendItem(p, "❌ absent",      AppTheme.DANGER);
        addLegendItem(p, "⚠ < 80% alert", AppTheme.WARNING);
        return p;
    }

    private void addLegendItem(JPanel p, String text, Color c) {
        JLabel l = new JLabel(text);
        l.setFont(AppTheme.FONT_SMALL);
        l.setForeground(c);
        p.add(l);
    }
}

