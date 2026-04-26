package gui;

import dao.Notice;
import dao.NoticeDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UGNoticePanel extends JPanel {

    private final NoticeDAO noticeDAO = new NoticeDAO();
    private JPanel          noticeContainer;

    public UGNoticePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        header.add(UIComponents.sectionTitle("📋 Notices"), BorderLayout.WEST);

        JButton refBtn = UIComponents.outlineButton("↻ Refresh");
        refBtn.addActionListener(e -> refresh());
        header.add(refBtn, BorderLayout.EAST);

        noticeContainer = new JPanel();
        noticeContainer.setLayout(new BoxLayout(noticeContainer, BoxLayout.Y_AXIS));
        noticeContainer.setBackground(AppTheme.BG_MAIN);

        JScrollPane scroll = new JScrollPane(noticeContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.BG_MAIN);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        noticeContainer.removeAll();
        try {
            List<Notice> notices = noticeDAO.getAllNotices();
            if (notices.isEmpty()) {
                UIComponents.RoundedPanel empty = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
                empty.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 40));
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
            JLabel err = new JLabel("Error: " + e.getMessage());
            err.setForeground(AppTheme.DANGER);
            noticeContainer.add(err);
        }
        noticeContainer.revalidate();
        noticeContainer.repaint();
    }

    private JPanel buildNoticeCard(Notice n) {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Left gradient accent bar
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY,
                        0, getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(6, 0));

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(16, 18, 16, 18));

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

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topRow.setOpaque(false);
        topRow.add(idBadge);

        JTextArea txt = new JTextArea(n.getNotice());
        txt.setFont(AppTheme.FONT_BODY);
        txt.setForeground(AppTheme.TEXT_PRIMARY);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setEditable(false);
        txt.setOpaque(false);
        txt.setBorder(BorderFactory.createEmptyBorder());

        body.add(topRow, BorderLayout.NORTH);
        body.add(txt,    BorderLayout.CENTER);

        card.add(bar,  BorderLayout.WEST);
        card.add(body, BorderLayout.CENTER);
        return card;
    }
}
