package gui;

import dao.User;
import dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setUndecorated(true);
        setTitle("TechNova – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 580);
        setLocationRelativeTo(null);
        setResizable(false);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY_DARK,
                        getWidth() / 2, getHeight(), AppTheme.PRIMARY);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth() / 2 + 30, getHeight());
                g2.setColor(AppTheme.BG_MAIN);
                g2.fillRect(getWidth() / 2 + 30, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);

        // ── LEFT BRANDING PANEL ──
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new GridBagLayout());
        left.setPreferredSize(new Dimension(400, 580));

        JPanel brandBox = new JPanel();
        brandBox.setOpaque(false);
        brandBox.setLayout(new BoxLayout(brandBox, BoxLayout.Y_AXIS));
        brandBox.setBorder(new EmptyBorder(0, 40, 0, 30));

        // Logo circle
        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(80, 80));
        logoCircle.setMaximumSize(new Dimension(80, 80));
        JLabel logoIcon = new JLabel("🎓");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        logoCircle.add(logoIcon);

        JLabel appName = new JLabel("TechNova");
        appName.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='width:220px'>Faculty of Technology<br>Management System</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tagline.setForeground(new Color(255, 255, 255, 180));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Feature bullets
        String[] features = {
                "✔ View and update student profile details",
                "✔ Register for available courses",
                "✔ Check latest notices and announcements",
                "✔ View weekly lecture timetable"
        };
        JPanel feats = new JPanel();
        feats.setOpaque(false);
        feats.setLayout(new BoxLayout(feats, BoxLayout.Y_AXIS));
        feats.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (String f : features) {
            JLabel fl = new JLabel(f);
            fl.setFont(new Font("Dialog", Font.PLAIN, 13));
            fl.setForeground(new Color(200, 240, 215));
            fl.setBorder(new EmptyBorder(4, 0, 4, 0));
            feats.add(fl);
        }

        logoCircle.setAlignmentX(Component.LEFT_ALIGNMENT);
        brandBox.add(logoCircle);
        brandBox.add(Box.createVerticalStrut(18));
        brandBox.add(appName);
        brandBox.add(Box.createVerticalStrut(8));
        brandBox.add(tagline);
        brandBox.add(Box.createVerticalStrut(30));
        brandBox.add(feats);

        left.add(brandBox);

        // ── RIGHT LOGIN PANEL ──
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(500, 580));

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(16, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(380, 380));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(AppTheme.TEXT_PRIMARY);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(AppTheme.FONT_BODY);
        sub.setForeground(AppTheme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = UIComponents.styledField("Username");
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        passwordField = new JPasswordField();
        passwordField.setFont(AppTheme.FONT_BODY);
        passwordField.setForeground(AppTheme.TEXT_PRIMARY);
        passwordField.setBackground(AppTheme.BG_MAIN);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        errorLabel.setForeground(AppTheme.DANGER);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton loginBtn = UIComponents.primaryButton("Sign In");
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(e -> doLogin());

        passwordField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        card.add(welcome);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(makeLabel("Username"));
        card.add(Box.createVerticalStrut(5));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(makeLabel("Password"));
        card.add(Box.createVerticalStrut(5));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);

        right.add(card);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠ Please enter username and password.");
            return;
        }

        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                errorLabel.setText("⚠ Invalid username or password.");
                return;
            }
            if (user.getRoleId() != 1) {
                errorLabel.setText("⚠ Access denied. Admin only.");
                return;
            }
            dispose();
            SwingUtilities.invokeLater(() -> new AdminDashboard(user).setVisible(true));
        } catch (SQLException ex) {
            errorLabel.setText("⚠ Database error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}
