package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class LecturerDashboardPanel extends JPanel {

    private final User lecturer;
    private final CourseUnitDAO   courseDAO = new CourseUnitDAO();
    private final LecUndergraduateDAO ugDAO   = new LecUndergraduateDAO();
    private final NoticeDAO        noticeDAO = new NoticeDAO();
    private final LecMarksDAO marksDAO  = new LecMarksDAO();

    public LecturerDashboardPanel(User lecturer) {
        this.lecturer = lecturer;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        String name = lecturer.getFullName() != null ? lecturer.getFullName() : "Lecturer";
        JLabel title = UIComponents.sectionTitle("👋  Welcome, " + name);

        JLabel dateLbl = new JLabel(new java.util.Date().toString().substring(0, 24));
        dateLbl.setFont(AppTheme.FONT_SMALL);
        dateLbl.setForeground(AppTheme.TEXT_MUTED);

        header.add(title,   BorderLayout.WEST);
        header.add(dateLbl, BorderLayout.EAST);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);

        UIComponents.StatCard courseCard = new UIComponents.StatCard("📚", "Courses",     "–", AppTheme.PRIMARY);
        UIComponents.StatCard ugCard     = new UIComponents.StatCard("🎓", "Students",    "–", AppTheme.SUCCESS);
        UIComponents.StatCard noticeCard = new UIComponents.StatCard("📋", "Notices",     "–", AppTheme.INFO);
        UIComponents.StatCard eligCard   = new UIComponents.StatCard("✅", "CA Eligible", "–", AppTheme.WARNING);

        statsRow.add(courseCard);
        statsRow.add(ugCard);
        statsRow.add(noticeCard);
        statsRow.add(eligCard);

        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 0));
        lower.setOpaque(false);
        lower.setBorder(new EmptyBorder(20, 0, 0, 0));
        lower.add(buildRecentNotices());
        lower.add(buildQuickNav());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsRow);
        center.add(lower);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        SwingUtilities.invokeLater(() -> {
            try {
                courseCard.setValue(String.valueOf(courseDAO.countCourses()));
                ugCard.setValue(String.valueOf(ugDAO.count()));
                noticeCard.setValue(String.valueOf(noticeDAO.countNotices()));

                int eligible = 0;
                List<CourseUnit> courses = courseDAO.getAllCourses();
                for (CourseUnit cu : courses) {
                    List<LecMarks> mList = marksDAO.getByCourse(cu.getCCode());
                    for (LecMarks m : mList) if (m.isCAEligible()) eligible++;
                }
                eligCard.setValue(String.valueOf(eligible));
            } catch (SQLException ignored) {}
        });
    }

    private JPanel buildRecentNotices() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Recent Notices");
        title.setFont(AppTheme.FONT_SUBTITLE);
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
                row.setLayout(new BorderLayout());
                row.setBorder(new EmptyBorder(8, 12, 8, 12));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

                JLabel num = new JLabel("#" + n.getNoticeId());
                num.setFont(new Font("dialog", Font.BOLD, 11));
                num.setForeground(AppTheme.PRIMARY);
                num.setPreferredSize(new Dimension(30, 20));

                String text = n.getNotice().length() > 55
                        ? n.getNotice().substring(0, 55) + "…" : n.getNotice();
                JLabel txt = new JLabel(text);
                txt.setFont(AppTheme.FONT_SMALL);
                txt.setForeground(AppTheme.TEXT_PRIMARY);

                row.add(num, BorderLayout.WEST);
                row.add(txt, BorderLayout.CENTER);
                list.add(row);
                list.add(Box.createVerticalStrut(6));
            }
            if (notices.isEmpty()) {
                JLabel empty = new JLabel("No notices posted yet.");
                empty.setFont(AppTheme.FONT_BODY);
                empty.setForeground(AppTheme.TEXT_MUTED);
                list.add(empty);
            }
        } catch (SQLException e) {
            list.add(new JLabel("Error loading notices."));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(list,  BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickNav() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quick Access");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel rows = new JPanel(new GridLayout(5, 1, 0, 8));
        rows.setOpaque(false);

        String[][] items = {
            {"📊", "Upload / View Marks",       "Marks & Grades section"},
            {"📚", "Manage Course Materials",    "Materials section"},
            {"👁", "View Attendance Records",    "Attendance section"},
            {"🏥", "View Medical Records",       "Medical section"},
            {"🎓", "Check Student Eligibility",  "Marks & Grades section"},
        };

        for (String[] item : items) {
            UIComponents.RoundedPanel row = new UIComponents.RoundedPanel(8, AppTheme.BG_MAIN);
            row.setLayout(new BorderLayout(10, 0));
            row.setBorder(new EmptyBorder(8, 12, 8, 12));

            JLabel icon = new JLabel(item[0]);
            icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            JPanel txt = new JPanel(new GridLayout(2, 1, 0, 2));
            txt.setOpaque(false);
            JLabel lbl  = new JLabel(item[1]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_PRIMARY);
            JLabel hint = new JLabel(item[2]);
            hint.setFont(AppTheme.FONT_SMALL);
            hint.setForeground(AppTheme.TEXT_MUTED);
            txt.add(lbl); txt.add(hint);

            row.add(icon, BorderLayout.WEST);
            row.add(txt,  BorderLayout.CENTER);
            rows.add(row);
        }

        card.add(title, BorderLayout.NORTH);
        card.add(rows,  BorderLayout.CENTER);
        return card;
    }
}
