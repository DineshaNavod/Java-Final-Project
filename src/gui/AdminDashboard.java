package gui;

import dao.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class AdminDashboard extends JFrame {

    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeNavBtn;

    // Nav items: {icon, label, card name}
    private static final String[][] NAV_ITEMS = {
        {"", "Dashboard",    "dashboard"},
        {"", "Users",        "users"},
        {"", "Courses",      "courses"},
        {"", "Notices",      "notices"},
        {"", "Timetable",   "timetable"},
        {"", "My Profile",   "profile"},
        {"",  "Settings",    "settings"},
    };

    public AdminDashboard(User user) {
        this.currentUser = user;
        setTitle("TechNova – Admin Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setMinimumSize(new Dimension(960, 600));
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BG_MAIN);

        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);

        setContentPane(root);
    }

    // ── SIDEBAR ──────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
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

        // ── Logo section ──
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(AppTheme.SIDEBAR_WIDTH, 80));
        logoPanel.setBorder(new EmptyBorder(0, 16, 0, 16));

        JPanel logoInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        logoInner.setOpaque(false);

        // Green logo circle
        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.PRIMARY);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(38, 38));
        JLabel logoIcon = new JLabel("🎓");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        logoCircle.add(logoIcon);

        JLabel appName = new JLabel("TechNova");
        appName.setFont(AppTheme.FONT_LOGO);
        appName.setForeground(Color.WHITE);

        logoInner.add(logoCircle);
        logoInner.add(appName);
        logoPanel.add(logoInner);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 30));
        sep.setBackground(new Color(255, 255, 255, 30));

        // ── Nav section ──
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel navLabel = new JLabel("MAIN MENU");
        navLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
        navLabel.setForeground(new Color(255, 255, 255, 80));
        navLabel.setBorder(new EmptyBorder(8, 8, 8, 8));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navPanel.add(navLabel);

        for (String[] item : NAV_ITEMS) {
            // Insert section label before "My Profile"
            if (item[2].equals("profile")) {
                JLabel acctLabel = new JLabel("ACCOUNT");
                acctLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 10));
                acctLabel.setForeground(new Color(255, 255, 255, 80));
                acctLabel.setBorder(new EmptyBorder(14, 8, 6, 8));
                acctLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                navPanel.add(acctLabel);
            }
            JButton navBtn = createNavButton(item[0], item[1], item[2]);
            navPanel.add(navBtn);
            navPanel.add(Box.createVerticalStrut(4));
            if (item[2].equals("dashboard")) {
                activeNavBtn = navBtn;
                setActive(navBtn);
            }
        }

        // ── Bottom user card ──
        JPanel userCard = buildUserCard();

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(logoPanel, BorderLayout.NORTH);
        top.add(sep,       BorderLayout.CENTER);

        sidebar.add(top,       BorderLayout.NORTH);
        sidebar.add(navPanel,  BorderLayout.CENTER);
        sidebar.add(userCard,  BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String icon, String label, String card) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
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

        btn.addActionListener(e -> {
            setActive(btn);
            cardLayout.show(contentPanel, card);
        });

        return btn;
    }

    private void setActive(JButton btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setForeground(new Color(200, 235, 215));
        }
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

        // Avatar circle
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(38, 38));
        String initial = currentUser.getFullName() != null && !currentUser.getFullName().isEmpty()
                ? String.valueOf(currentUser.getFullName().charAt(0)).toUpperCase() : "A";
        JLabel initLbl = new JLabel(initial);
        initLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        initLbl.setForeground(Color.WHITE);
        avatar.add(initLbl);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel name = new JLabel(currentUser.getFullName() != null ? currentUser.getFullName() : "Admin");
        name.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        name.setForeground(Color.WHITE);
        JLabel role = new JLabel("System Administrator");
        role.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 10));
        role.setForeground(new Color(160, 220, 190));
        info.add(name);
        info.add(role);

        JButton logoutBtn = new JButton("⏻") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoutBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        logoutBtn.setForeground(Color.WHITE);
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

    // ── CONTENT AREA ─────────────────────────────────────────────────
    private JPanel buildContent() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppTheme.BG_MAIN);

        contentPanel.add(new DashboardPanel(),          "dashboard");
        contentPanel.add(new UserManagementPanel(),     "users");
        contentPanel.add(new CourseManagementPanel(),   "courses");
        contentPanel.add(new NoticeManagementPanel(),   "notices");
        contentPanel.add(new TimetableManagementPanel(),"timetable");
        contentPanel.add(new ProfilePanel(currentUser), "profile");
        contentPanel.add(new SettingsPanel(),           "settings");

        cardLayout.show(contentPanel, "dashboard");
        return contentPanel;
    }
}
