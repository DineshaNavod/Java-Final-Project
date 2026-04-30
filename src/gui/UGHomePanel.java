package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UGHomePanel extends JPanel {

    private final User             currentUser;
    private final LecMarksDAO         marksDAO   = new LecMarksDAO();
    private final LecAttendanceDAO    attDAO     = new LecAttendanceDAO();
    private final LecMedicalDAO       medDAO     = new LecMedicalDAO();
    private final CourseUnitDAO    courseDAO  = new CourseUnitDAO();
    private final NoticeDAO        noticeDAO  = new NoticeDAO();

    public UGHomePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        String name = currentUser.getFullName() != null ? currentUser.getFullName() : "Student";
        JLabel title = UIComponents.sectionTitle("👋  Hello, " + name.split(" ")[0] + "!");
        JLabel dateLbl = new JLabel(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLbl.setFont(AppTheme.FONT_SMALL);
        dateLbl.setForeground(AppTheme.TEXT_MUTED);
        header.add(title,   BorderLayout.WEST);
        header.add(dateLbl, BorderLayout.EAST);


        UIComponents.StatCard sgpaCard   = new UIComponents.StatCard("📊", "My SGPA",       "–", AppTheme.PRIMARY);
        UIComponents.StatCard attCard    = new UIComponents.StatCard("📅", "Attendance",     "–", AppTheme.SUCCESS);
        UIComponents.StatCard courseCard = new UIComponents.StatCard("📚", "Courses",        "–", AppTheme.INFO);
        UIComponents.StatCard medCard    = new UIComponents.StatCard("🏥", "Medical Records","–", AppTheme.WARNING);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(sgpaCard); statsRow.add(attCard);
        statsRow.add(courseCard); statsRow.add(medCard);


        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 0));
        lower.setOpaque(false);
        lower.setBorder(new EmptyBorder(20, 0, 0, 0));
        lower.add(buildRecentGrades());
        lower.add(buildRecentNotices());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsRow);
        center.add(lower);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);


        SwingUtilities.invokeLater(() -> {
            String regNo = currentUser.getUsername();
            try {

                double sgpa = marksDAO.calculateSGPA(regNo);
                sgpaCard.setValue(String.format("%.2f", sgpa));


                List<LecAttendance> atts = attDAO.getByStudent(regNo);
                if (!atts.isEmpty()) {
                    long present = atts.stream().filter(a -> "present".equals(a.getStatus())).count();
                    double pct = (present * 100.0) / atts.size();
                    attCard.setValue(String.format("%.0f%%", pct));
                } else { attCard.setValue("N/A"); }


                courseCard.setValue(String.valueOf(courseDAO.countCourses()));


                medCard.setValue(String.valueOf(medDAO.getByStudent(regNo).size()));
            } catch (SQLException ignored) {}
        });
    }

    private JPanel buildRecentGrades() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📊 Recent Grades");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        String regNo = currentUser.getUsername();
        try {
            List<LecMarks> marksList = marksDAO.getByStudent(regNo);
            if (marksList.isEmpty()) {
                JLabel empty = new JLabel("No grade records found yet.");
                empty.setFont(AppTheme.FONT_BODY);
                empty.setForeground(AppTheme.TEXT_MUTED);
                list.add(empty);
            }
            int max = Math.min(marksList.size(), 4);
            for (int i = 0; i < max; i++) {
                LecMarks m = marksList.get(i);
                UIComponents.RoundedPanel row = new UIComponents.RoundedPanel(8, AppTheme.BG_MAIN);
                row.setLayout(new BorderLayout(10, 0));
                row.setBorder(new EmptyBorder(8, 12, 8, 12));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
                info.setOpaque(false);
                JLabel code = new JLabel(m.getCCode());
                code.setFont(new Font("Segoe UI", Font.BOLD, 12));
                code.setForeground(AppTheme.TEXT_PRIMARY);
                JLabel tot = new JLabel(String.format("Total: %.2f  CA: %.2f/40",
                        m.getTotalMarks(), m.getCAMarks()));
                tot.setFont(AppTheme.FONT_SMALL);
                tot.setForeground(AppTheme.TEXT_SECONDARY);
                info.add(code); info.add(tot);

                JLabel gradeLbl = new JLabel(m.getGrade());
                gradeLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
                gradeLbl.setForeground(gradeColor(m.getGrade()));

                row.add(info,     BorderLayout.CENTER);
                row.add(gradeLbl, BorderLayout.EAST);
                list.add(row);
                list.add(Box.createVerticalStrut(6));
            }
        } catch (SQLException e) {
            list.add(new JLabel("Error: " + e.getMessage()));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(list,  BorderLayout.CENTER);
        return card;
    }

    private JPanel buildRecentNotices() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📋 Latest Notices");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        try {
            List<Notice> notices = noticeDAO.getAllNotices();
            int max = Math.min(notices.size(), 4);
            for (int i = 0; i < max; i++) {
                Notice n = notices.get(i);
                UIComponents.RoundedPanel row = new UIComponents.RoundedPanel(6, AppTheme.BG_MAIN);
                row.setLayout(new BorderLayout(8, 0));
                row.setBorder(new EmptyBorder(8, 12, 8, 12));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                row.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel numLbl = new JLabel("#" + n.getNoticeId());
                numLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                numLbl.setForeground(AppTheme.PRIMARY);
                numLbl.setPreferredSize(new Dimension(28, 20));

                String text = n.getNotice().length() > 56
                        ? n.getNotice().substring(0, 56) + "…" : n.getNotice();
                JLabel txt = new JLabel(text);
                txt.setFont(AppTheme.FONT_SMALL);
                txt.setForeground(AppTheme.TEXT_PRIMARY);

                row.add(numLbl, BorderLayout.WEST);
                row.add(txt,    BorderLayout.CENTER);
                list.add(row);
                list.add(Box.createVerticalStrut(6));
            }
            if (notices.isEmpty()) {
                list.add(new JLabel("No notices posted yet.") {{
                    setFont(AppTheme.FONT_BODY);
                    setForeground(AppTheme.TEXT_MUTED);
                }});
            }
        } catch (SQLException e) {
            list.add(new JLabel("Error: " + e.getMessage()));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(list,  BorderLayout.CENTER);
        return card;
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
