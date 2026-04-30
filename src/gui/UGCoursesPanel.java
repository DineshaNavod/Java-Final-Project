package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UGCoursesPanel extends JPanel {

    private final User          currentUser;
    private final CourseUnitDAO courseDAO = new CourseUnitDAO();
    private final LecMarksDAO      marksDAO  = new LecMarksDAO();

    public UGCoursesPanel(User user) {
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
        header.add(UIComponents.sectionTitle("📚 My Courses"), BorderLayout.WEST);


        JPanel grid = new JPanel(new GridLayout(0, 2, 16, 16));
        grid.setOpaque(false);

        try {
            List<CourseUnit> courses = courseDAO.getAllCourses();
            String regNo = currentUser.getUsername();

            for (CourseUnit cu : courses) {
                LecMarks m = marksDAO.getMarks(regNo, cu.getCCode());
                grid.add(buildCourseCard(cu, m));
            }

            if (courses.isEmpty()) {
                UIComponents.RoundedPanel empty = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
                JLabel lbl = new JLabel("No courses available.");
                lbl.setFont(AppTheme.FONT_SUBTITLE);
                lbl.setForeground(AppTheme.TEXT_MUTED);
                empty.add(lbl);
                grid.add(empty);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildCourseCard(CourseUnit cu, LecMarks m) {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout(0, 0));


        JPanel topBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY_DARK,
                        getWidth(), 0, AppTheme.PRIMARY_LIGHT);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        topBar.setLayout(new BorderLayout());
        topBar.setPreferredSize(new Dimension(0, 54));
        topBar.setBorder(new EmptyBorder(12, 18, 12, 18));
        topBar.setOpaque(false);

        JLabel codeLbl = new JLabel(cu.getCCode());
        codeLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        codeLbl.setForeground(Color.WHITE);


        JPanel badges = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        badges.setOpaque(false);
        if ("YES".equals(cu.getIsTheory()))   badges.add(typeBadge("T", new Color(0x2980B9)));
        if ("YES".equals(cu.getIsPractical()))badges.add(typeBadge("P", new Color(0x8E44AD)));

        topBar.add(codeLbl, BorderLayout.WEST);
        topBar.add(badges,  BorderLayout.EAST);


        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(14, 18, 16, 18));

        JLabel nameLbl = new JLabel("<html><div style='width:200px'>" + cu.getCName() + "</div></html>");
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLbl.setForeground(AppTheme.TEXT_PRIMARY);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel semLbl = new JLabel("Semester: " + (cu.getSemester() != null ? cu.getSemester() : "–")
                + "   |   Credits: " + cu.getCredit());
        semLbl.setFont(AppTheme.FONT_SMALL);
        semLbl.setForeground(AppTheme.TEXT_MUTED);
        semLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(nameLbl);
        body.add(Box.createVerticalStrut(6));
        body.add(semLbl);
        body.add(Box.createVerticalStrut(12));


        if (m != null) {
            JSeparator divider = new JSeparator();
            divider.setForeground(AppTheme.BORDER);
            divider.setAlignmentX(Component.LEFT_ALIGNMENT);
            divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            body.add(divider);
            body.add(Box.createVerticalStrut(10));

            JPanel marksRow = new JPanel(new GridLayout(2, 2, 8, 4));
            marksRow.setOpaque(false);
            marksRow.setAlignmentX(Component.LEFT_ALIGNMENT);
            marksRow.add(miniStat("CA", String.format("%.2f/40", m.getCAMarks()), AppTheme.PRIMARY));
            marksRow.add(miniStat("Final", String.format("%.2f/60", m.getFinalExamMarks()), AppTheme.INFO));
            marksRow.add(miniStat("Total", String.format("%.2f", m.getTotalMarks()), AppTheme.SUCCESS));
            marksRow.add(miniStat("Grade", m.getGrade(), gradeColor(m.getGrade())));
            body.add(marksRow);
        } else {
            UIComponents.RoundedPanel noMarks = new UIComponents.RoundedPanel(6, AppTheme.BG_MAIN);
            noMarks.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));
            noMarks.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            noMarks.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel nml = new JLabel("📭  No marks recorded yet");
            nml.setFont(AppTheme.FONT_SMALL);
            nml.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            nml.setForeground(AppTheme.TEXT_MUTED);
            noMarks.add(nml);
            body.add(noMarks);
        }

        card.add(topBar, BorderLayout.NORTH);
        card.add(body,   BorderLayout.CENTER);
        return card;
    }

    private JPanel typeBadge(String text, Color color) {
        JPanel badge = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        badge.setOpaque(false);
        badge.setPreferredSize(new Dimension(22, 22));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(Color.WHITE);
        badge.add(l);
        return badge;
    }

    private JPanel miniStat(String label, String value, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel v = new JLabel(value);
        v.setFont(new Font("Segoe UI", Font.BOLD, 13));
        v.setForeground(color);
        JLabel k = new JLabel(label);
        k.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        k.setForeground(AppTheme.TEXT_MUTED);
        p.add(v); p.add(k);
        return p;
    }

    private Color gradeColor(String g) {
        if (g == null) return AppTheme.TEXT_MUTED;
        return switch (g) {
            case "A+", "A"  -> new Color(0x1A7A3C);
            case "A-", "B+" -> AppTheme.SUCCESS;
            case "B", "B-"  -> AppTheme.INFO;
            case "C+", "C"  -> new Color(0xF39C12);
            default         -> AppTheme.DANGER;
        };
    }
}
