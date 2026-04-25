package gui;

import dao.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class DashboardPanel extends JPanel {

    private UserDAO userDAO = new UserDAO();
    private NoticeDAO noticeDAO = new NoticeDAO();
    private CourseUnitDAO courseDAO = new CourseUnitDAO();
    private TimetableDAO timetableDAO = new TimetableDAO();

    private UIComponents.StatCard lecturerCard, studentCard, staffCard, courseCard;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        refresh();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = UIComponents.sectionTitle("📊 Dashboard Overview");
        JLabel dateLabel = new JLabel(new java.util.Date().toString().substring(0, 24));
        dateLabel.setFont(AppTheme.FONT_SMALL);
        dateLabel.setForeground(AppTheme.TEXT_SECONDARY);

        header.add(title, BorderLayout.WEST);
        header.add(dateLabel, BorderLayout.EAST);

        // Stat cards row
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        lecturerCard = new UIComponents.StatCard("", "Lecturers",    "–", AppTheme.INFO);
        studentCard  = new UIComponents.StatCard("", "Students",      "–", AppTheme.SUCCESS);
        staffCard    = new UIComponents.StatCard("", "Tech Officers",  "–", AppTheme.WARNING);
        courseCard   = new UIComponents.StatCard("", "Courses",        "–", AppTheme.PRIMARY);

        statsPanel.add(lecturerCard);
        statsPanel.add(studentCard);
        statsPanel.add(staffCard);
        statsPanel.add(courseCard);

        // Lower panels
        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 0));
        lower.setOpaque(false);
        lower.setBorder(new EmptyBorder(20, 0, 0, 0));

        lower.add(buildRecentUsers());
        lower.add(buildQuickActions());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsPanel);
        center.add(lower);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel buildRecentUsers() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("👥 User Summary");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        String[] cols = {"Role", "Count"};
        JTable table = UIComponents.styledTable(cols);
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        try {
            model.addRow(new Object[]{"Lecturers", userDAO.countByRole(2)});
            model.addRow(new Object[]{"Technical Officers", userDAO.countByRole(3)});
            model.addRow(new Object[]{"Undergraduates", userDAO.countByRole(4)});
            model.addRow(new Object[]{"Total Users", userDAO.countAll()});
        } catch (SQLException e) {
            model.addRow(new Object[]{"Error", e.getMessage()});
        }

        card.add(title, BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildQuickActions() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("⚡ Quick Actions");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel actions = new JPanel(new GridLayout(4, 1, 0, 10));
        actions.setOpaque(false);

        String[][] quickActions = {
            {"➕ Add New User",      "Manage Users section"},
            {"📚 Add Course Unit",   "Manage Courses section"},
            {"📋 Post a Notice",     "Manage Notices section"},
            {"🗓 Schedule Session",  "Manage Timetable section"}
        };

        for (String[] qa : quickActions) {
            UIComponents.RoundedPanel row = new UIComponents.RoundedPanel(8, AppTheme.BG_MAIN);
            row.setLayout(new BorderLayout());
            row.setBorder(new EmptyBorder(10, 14, 10, 14));

            JLabel lbl = new JLabel(qa[0]);
            lbl.setFont(AppTheme.FONT_BODY);
            lbl.setForeground(AppTheme.TEXT_PRIMARY);

            JLabel hint = new JLabel(qa[1]);
            hint.setFont(AppTheme.FONT_SMALL);
            hint.setForeground(AppTheme.TEXT_MUTED);

            row.add(lbl, BorderLayout.NORTH);
            row.add(hint, BorderLayout.SOUTH);
            actions.add(row);
        }

        card.add(title, BorderLayout.NORTH);
        card.add(actions, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        try {
            lecturerCard.setValue(String.valueOf(userDAO.countByRole(2)));
            studentCard.setValue(String.valueOf(userDAO.countByRole(4)));
            staffCard.setValue(String.valueOf(userDAO.countByRole(3)));
            courseCard.setValue(String.valueOf(courseDAO.countCourses()));
        } catch (SQLException e) {
            // silent fail on stats
        }
    }
}
