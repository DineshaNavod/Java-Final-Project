package gui;

import dao.Notice;
import dao.NoticeDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TONoticePanel extends JPanel {

    private final NoticeDAO noticeDAO = new NoticeDAO();

    public TONoticePanel() {
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
        header.add(UIComponents.sectionTitle("📋 Notices"), BorderLayout.WEST);

        UIComponents.RoundedPanel badge = new UIComponents.RoundedPanel(20, new Color(0xFFF3CD));
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 6));
        badge.setPreferredSize(new Dimension(140, 36));
        JLabel badgeLbl = new JLabel("View Only");
        badgeLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        badgeLbl.setForeground(new Color(0x856404));
        badge.add(badgeLbl);

        JButton refBtn = UIComponents.outlineButton("↻ Refresh");
        refBtn.addActionListener(e -> refreshNotices());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(refBtn);
        rightPanel.add(badge);
        header.add(rightPanel, BorderLayout.EAST);

        // ── NOTICE SCROLL AREA ──
        noticeContainer = new JPanel();
        noticeContainer.setLayout(new BoxLayout(noticeContainer, BoxLayout.Y_AXIS));
        noticeContainer.setBackground(AppTheme.BG_MAIN);
        noticeContainer.setBorder(new EmptyBorder(0, 0, 0, 0));

        JScrollPane scroll = new JScrollPane(noticeContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        refreshNotices();
    }

    private JPanel noticeContainer;

    private void refreshNotices() {
        noticeContainer.removeAll();
        try {
            List<Notice> notices = noticeDAO.getAllNotices();
            if (notices.isEmpty()) {
                UIComponents.RoundedPanel empty = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 30));
                empty.setAlignmentX(Component.LEFT_ALIGNMENT);
                empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                JLabel lbl = new JLabel("📭  No notices posted yet.");
                lbl.setFont(AppTheme.FONT_SUBTITLE);
                lbl.setForeground(AppTheme.TEXT_MUTED);
                empty.add(lbl);
                noticeContainer.add(empty);
            } else {
                for (Notice n : notices) {
                    noticeContainer.add(buildNoticeCard(n));
                    noticeContainer.add(Box.createVerticalStrut(12));
                }
            }
        } catch (SQLException e) {
            JLabel err = new JLabel("Error loading notices: " + e.getMessage());
            err.setForeground(AppTheme.DANGER);
            noticeContainer.add(err);
        }
        noticeContainer.revalidate();
        noticeContainer.repaint();
    }

    private JPanel buildNoticeCard(Notice n) {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Left colour accent bar
        JPanel accentBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY,
                        0, getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
            }
        };
        accentBar.setOpaque(false);
        accentBar.setPreferredSize(new Dimension(6, 0));

        // Content
        JPanel content = new JPanel(new BorderLayout(0, 6));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(16, 18, 16, 18));

        // ID badge
        JPanel idBadge = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        idBadge.setOpaque(false);
        idBadge.setPreferredSize(new Dimension(44, 22));
        JLabel idLbl = new JLabel("# " + n.getNoticeId());
        idLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        idLbl.setForeground(AppTheme.PRIMARY_DARK);
        idBadge.add(idLbl);

        // Notice text
        JTextArea textArea = new JTextArea(n.getNotice());
        textArea.setFont(AppTheme.FONT_BODY);
        textArea.setForeground(AppTheme.TEXT_PRIMARY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setBorder(BorderFactory.createEmptyBorder());
        textArea.setBackground(new Color(0, 0, 0, 0));

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topRow.setOpaque(false);
        topRow.add(idBadge);

        content.add(topRow,   BorderLayout.NORTH);
        content.add(textArea, BorderLayout.CENTER);

        card.add(accentBar, BorderLayout.WEST);
        card.add(content,   BorderLayout.CENTER);
        return card;
    }
}
