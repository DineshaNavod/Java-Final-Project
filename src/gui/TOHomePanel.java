package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TOHomePanel extends JPanel {

    private final User             currentUser;
    private final LecUndergraduateDAO ugDAO      = new LecUndergraduateDAO();
    private final TOAttendanceDAO attDAO     = new TOAttendanceDAO();
    private final TOMedicalDAO medDAO     = new TOMedicalDAO();
    private final NoticeDAO        noticeDAO  = new NoticeDAO();
    private final TOTimetableDAO ttDAO      = new TOTimetableDAO();

    public TOHomePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        String name = currentUser.getFullName() != null ? currentUser.getFullName() : "Officer";
        JLabel title = UIComponents.sectionTitle("👋  Welcome, " + name);
        JLabel dateLbl = new JLabel(
            LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLbl.setFont(AppTheme.FONT_SMALL);
        dateLbl.setForeground(AppTheme.TEXT_MUTED);
        header.add(title,   BorderLayout.WEST);
        header.add(dateLbl, BorderLayout.EAST);

        // ── STAT CARDS ──
        UIComponents.StatCard studCard   = new UIComponents.StatCard("🎓", "Students",         "–", AppTheme.PRIMARY);
        UIComponents.StatCard attCard    = new UIComponents.StatCard("📅", "Attendance Today", "–", AppTheme.SUCCESS);
        UIComponents.StatCard medCard    = new UIComponents.StatCard("🏥", "Pending Medical",  "–", AppTheme.WARNING);
        UIComponents.StatCard noticeCard = new UIComponents.StatCard("📋", "Notices",          "–", AppTheme.INFO);

        JPanel statsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        statsRow.setOpaque(false);
        statsRow.add(studCard); statsRow.add(attCard);
        statsRow.add(medCard);  statsRow.add(noticeCard);

        // ── LOWER ROW ──
        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 0));
        lower.setOpaque(false);
        lower.setBorder(new EmptyBorder(20, 0, 0, 0));
        lower.add(buildRecentNotices());
        lower.add(buildTodaySessions());

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(statsRow);
        center.add(lower);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // ── POPULATE stats async ──
        SwingUtilities.invokeLater(() -> {
            try {
                studCard.setValue(String.valueOf(ugDAO.count()));
                noticeCard.setValue(String.valueOf(noticeDAO.countNotices()));

                // Pending medicals = not approved count
                long pending = medDAO.getAll().stream()
                        .filter(m -> "not approved".equals(m.getStatus())).count();
                medCard.setValue(String.valueOf(pending));

                // Attendance today = count of records added today
                String today = LocalDate.now().toString();
                long todayAtt = attDAO.getAllToday(today);
                attCard.setValue(String.valueOf(todayAtt));
            } catch (SQLException ignored) {}
        });
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

                JLabel numLbl = new JLabel("#" + n.getNoticeId());
                numLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                numLbl.setForeground(AppTheme.PRIMARY);
                numLbl.setPreferredSize(new Dimension(28, 20));

                String text = n.getNotice().length() > 58
                        ? n.getNotice().substring(0, 58) + "…" : n.getNotice();
                JLabel txt = new JLabel(text);
                txt.setFont(AppTheme.FONT_SMALL);
                txt.setForeground(AppTheme.TEXT_PRIMARY);

                row.add(numLbl, BorderLayout.WEST);
                row.add(txt,    BorderLayout.CENTER);
                list.add(row);
                list.add(Box.createVerticalStrut(6));
            }
            if (notices.isEmpty()) {
                JLabel empty = new JLabel("No notices yet.");
                empty.setFont(AppTheme.FONT_BODY);
                empty.setForeground(AppTheme.TEXT_MUTED);
                list.add(empty);
            }
        } catch (SQLException e) {
            list.add(new JLabel("Error: " + e.getMessage()));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(list,  BorderLayout.CENTER);
        return card;
    }

    private JPanel buildTodaySessions() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("🗓 Today's Sessions");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setOpaque(false);

        try {
            String today = LocalDate.now().toString();
            List<TOTimetable> sessions = ttDAO.getSessionsByDate(today);
            if (sessions.isEmpty()) {
                UIComponents.RoundedPanel emptyRow = new UIComponents.RoundedPanel(8, AppTheme.BG_MAIN);
                emptyRow.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 10));
                emptyRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
                JLabel lbl = new JLabel("🌙  No sessions scheduled today.");
                lbl.setFont(AppTheme.FONT_BODY);
                lbl.setForeground(AppTheme.TEXT_MUTED);
                emptyRow.add(lbl);
                list.add(emptyRow);
            }
            for (TOTimetable t : sessions) {
                UIComponents.RoundedPanel row = new UIComponents.RoundedPanel(8, AppTheme.BG_MAIN);
                row.setLayout(new BorderLayout(10, 0));
                row.setBorder(new EmptyBorder(10, 14, 10, 14));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

                // Type badge
                JPanel badge = new JPanel(new GridBagLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(t.getType().equals("THEORY") ? AppTheme.INFO : AppTheme.PRIMARY_LIGHT);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                        g2.dispose();
                    }
                };
                badge.setOpaque(false);
                badge.setPreferredSize(new Dimension(72, 22));
                JLabel badgeTxt = new JLabel(t.getType());
                badgeTxt.setFont(new Font("Segoe UI", Font.BOLD, 10));
                badgeTxt.setForeground(Color.WHITE);
                badge.add(badgeTxt);

                JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
                info.setOpaque(false);
                JLabel code = new JLabel(t.getCCode() + "  |  Hall: " + t.getLecHall());
                code.setFont(new Font("Segoe UI", Font.BOLD, 12));
                code.setForeground(AppTheme.TEXT_PRIMARY);
                JLabel dur = new JLabel("Duration: " + t.getDuration());
                dur.setFont(AppTheme.FONT_SMALL);
                dur.setForeground(AppTheme.TEXT_SECONDARY);
                info.add(code); info.add(dur);

                row.add(badge, BorderLayout.WEST);
                row.add(info,  BorderLayout.CENTER);
                list.add(row);
                list.add(Box.createVerticalStrut(6));
            }
        } catch (SQLException e) {
            list.add(new JLabel("Error: " + e.getMessage()));
        }

        card.add(title, BorderLayout.NORTH);
        card.add(new JScrollPane(list) {{
            setBorder(BorderFactory.createEmptyBorder());
            getViewport().setBackground(Color.WHITE);
        }}, BorderLayout.CENTER);
        return card;
    }
}
