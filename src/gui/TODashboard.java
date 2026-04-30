package gui;

import dao.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class TODashboard extends JFrame {

    private final User   currentUser;
    private JPanel       contentPanel;
    private CardLayout   cardLayout;
    private JButton      activeNavBtn;


    private static final String[][] NAV_ITEMS = {
        {"", "Dashboard",   "home"},
        {"", "Attendance",  "attendance"},
        {"", "Medical",     "medical"},
        {"", "Timetable",  "timetable"},
        {"", "Notices",     "notices"},
    };

    public TODashboard(User user) {
        this.currentUser = user;
        setTitle("TechNova – Technical Officer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        build();
    }

    private void build() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_MAIN);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.BG_SIDEBAR,
                        0, getHeight(), new Color(0x0A2A1A));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 0));
        sidebar.setOpaque(false);

        // ── Logo block ──
        JPanel logoWrap = new JPanel(new GridBagLayout());
        logoWrap.setOpaque(false);
        logoWrap.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 78));
        logoWrap.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel logoInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoInner.setOpaque(false);

        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(38, 38));
        JLabel logoIcon = new JLabel("🔧");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        logoCircle.add(logoIcon);

        JLabel appName = new JLabel("TechNova");
        appName.setFont(AppTheme.FONT_LOGO);
        appName.setForeground(Color.WHITE);
        logoInner.add(logoCircle);
        logoInner.add(appName);
        logoWrap.add(logoInner);

        // Role tag
        JPanel roleTag = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 2));
        roleTag.setOpaque(false);
        JLabel roleLbl = new JLabel("Technical Officer");
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLbl.setForeground(AppTheme.ACCENT_LIGHT);
        roleTag.add(roleLbl);

        JPanel topArea = new JPanel(new BorderLayout());
        topArea.setOpaque(false);
        topArea.add(logoWrap, BorderLayout.CENTER);
        topArea.add(roleTag,  BorderLayout.SOUTH);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));

        JPanel topBlock = new JPanel(new BorderLayout());
        topBlock.setOpaque(false);
        topBlock.add(topArea, BorderLayout.NORTH);
        topBlock.add(sep,     BorderLayout.SOUTH);

        // ── Nav items ──
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel sectionLbl = new JLabel("MAIN MENU");
        sectionLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLbl.setForeground(new Color(255, 255, 255, 80));
        sectionLbl.setBorder(new EmptyBorder(8, 8, 8, 8));
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(sectionLbl);

        for (String[] item : NAV_ITEMS) {
            // section break before Timetable
            if (item[2].equals("timetable")) {
                JLabel viewLbl = new JLabel("VIEW ONLY");
                viewLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
                viewLbl.setForeground(new Color(255, 255, 255, 80));
                viewLbl.setBorder(new EmptyBorder(14, 8, 6, 8));
                viewLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
                navPanel.add(viewLbl);
            }
            JButton btn = makeNavBtn(item[0], item[1], item[2]);
            navPanel.add(btn);
            navPanel.add(Box.createVerticalStrut(4));
            if (item[2].equals("home")) { activeNavBtn = btn; setActive(btn); }
        }

        // Account section
        JLabel acctLbl = new JLabel("ACCOUNT");
        acctLbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        acctLbl.setForeground(new Color(255, 255, 255, 80));
        acctLbl.setBorder(new EmptyBorder(14, 8, 6, 8));
        acctLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(acctLbl);
        JButton profileBtn = makeNavBtn("", "My Profile", "profile");
        navPanel.add(profileBtn);

        sidebar.add(topBlock,         BorderLayout.NORTH);
        sidebar.add(navPanel,         BorderLayout.CENTER);
        sidebar.add(buildUserCard(),  BorderLayout.SOUTH);
        return sidebar;
    }

    private JButton makeNavBtn(String icon, String label, String card) {
        JButton btn = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (this == activeNavBtn) {
                    g2.setColor(AppTheme.PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 18));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setText(icon + "  " + label);
        btn.setFont(AppTheme.FONT_NAV);
        btn.setForeground(new Color(200, 235, 215));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH - 24, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setBorder(new EmptyBorder(0, 12, 0, 12));
        btn.addActionListener(e -> { setActive(btn); cardLayout.show(contentPanel, card); });
        return btn;
    }

    private void setActive(JButton btn) {
        if (activeNavBtn != null) activeNavBtn.setForeground(new Color(200, 235, 215));
        activeNavBtn = btn;
        btn.setForeground(Color.WHITE);
        if (contentPanel != null) contentPanel.repaint();
    }

    private JPanel buildUserCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 16, 12));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));

        JPanel inner = new JPanel(new BorderLayout(10, 0));
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(12, 10, 0, 10));

        // Avatar
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));
        String fn = currentUser.getFullName() != null ? currentUser.getFullName() : "T";
        JLabel initLbl = new JLabel(String.valueOf(fn.charAt(0)).toUpperCase());
        initLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        initLbl.setForeground(Color.WHITE);
        avatar.add(initLbl);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel nameLbl = new JLabel(fn.length() > 20 ? fn.substring(0, 18) + "…" : fn);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLbl.setForeground(Color.WHITE);
        JLabel roleLbl = new JLabel("Technical Officer");
        roleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLbl.setForeground(new Color(160, 220, 190));
        info.add(nameLbl); info.add(roleLbl);

        // Logout
        JButton logoutBtn = new JButton("⏻") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setForeground(new Color(200, 220, 210));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(32, 32));
        logoutBtn.setToolTipText("Logout");
        logoutBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Logout from TechNova?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });

        inner.add(avatar,    BorderLayout.WEST);
        inner.add(info,      BorderLayout.CENTER);
        inner.add(logoutBtn, BorderLayout.EAST);
        card.add(sep,   BorderLayout.NORTH);
        card.add(inner, BorderLayout.CENTER);
        return card;
    }

    // ── CONTENT ──────────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BG_MAIN);

        contentPanel.add(new TOHomePanel(currentUser),  "home");
        contentPanel.add(new TOAttendancePanel(),        "attendance");
        contentPanel.add(new TOMedicalPanel(),           "medical");
        contentPanel.add(new TOTimetablePanel(),         "timetable");
        contentPanel.add(new TONoticePanel(),            "notices");
        contentPanel.add(new TOProfilePanel(currentUser),"profile");

        cardLayout.show(contentPanel, "home");
        return contentPanel;
    }
}
