package gui;

import dao.User;
import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class TOProfilePanel extends JPanel {

    private final User    currentUser;
    private final UserDAO userDAO = new UserDAO();

    private JTextField fullNameF, emailF, phoneF;
    private JLabel     avatarLabel, displayNameLabel;

    public TOProfilePanel(User user) {
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
        header.add(UIComponents.sectionTitle("My Profile"), BorderLayout.WEST);

        // ── MAIN CARD ──
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());

        // ── LEFT: avatar + read-only info ──
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(240, 0));
        left.setBorder(new EmptyBorder(40, 30, 40, 20));

        // Avatar circle
        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY,
                        getWidth(), getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Ring
                g2.setColor(new Color(255, 255, 255, 60));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.dispose();
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setPreferredSize(new Dimension(96, 96));
        avatarCircle.setMaximumSize(new Dimension(96, 96));
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarLabel = new JLabel(getInitials(currentUser.getFullName()));
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        avatarLabel.setForeground(Color.WHITE);
        avatarCircle.add(avatarLabel);

        displayNameLabel = new JLabel(
            currentUser.getFullName() != null ? currentUser.getFullName() : "Technical Officer");
        displayNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        displayNameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        displayNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role badge
        JPanel roleBadge = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        roleBadge.setOpaque(false);
        roleBadge.setMaximumSize(new Dimension(190, 28));
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel roleLabel = new JLabel("Technical Officer");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setForeground(AppTheme.PRIMARY_DARK);
        roleBadge.add(roleLabel);

        // Static info rows
        JPanel infoRows = new JPanel();
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));
        infoRows.setOpaque(false);
        infoRows.setBorder(new EmptyBorder(20, 0, 0, 0));
        infoRows.setAlignmentX(Component.CENTER_ALIGNMENT);

        addInfoRow(infoRows, "", "User ID",  String.valueOf(currentUser.getUserId()));
        addInfoRow(infoRows, "", "Username", currentUser.getUsername());
        addInfoRow(infoRows, "", "Password", "••••••••");

        left.add(Box.createVerticalGlue());
        left.add(avatarCircle);
        left.add(Box.createVerticalStrut(14));
        left.add(displayNameLabel);
        left.add(Box.createVerticalStrut(8));
        left.add(roleBadge);
        left.add(infoRows);
        left.add(Box.createVerticalGlue());

        // Vertical divider
        JSeparator divider = new JSeparator(SwingConstants.VERTICAL);
        divider.setForeground(AppTheme.BORDER);
        divider.setPreferredSize(new Dimension(1, 0));

        // ── RIGHT: edit form ──
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(40, 10, 40, 36)); // was 36 left
        JLabel formTitle = new JLabel("Edit Contact Details");
        formTitle.setFont(AppTheme.FONT_SUBTITLE);
        formTitle.setForeground(AppTheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("You can update your name, email and phone number.");
        subLbl.setFont(AppTheme.FONT_SMALL);
        subLbl.setForeground(AppTheme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameF = UIComponents.styledField("Full Name");
        emailF    = UIComponents.styledField("Email Address");
        phoneF    = UIComponents.styledField("Phone Number");

        fullNameF.setText(safeStr(currentUser.getFullName()));
        emailF.setText(safeStr(currentUser.getEmail()));
        phoneF.setText(safeStr(currentUser.getPhone()));

        // Warning note
        JPanel noteBox = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFF3CD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0xFFE083));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        noteBox.setOpaque(false);
        noteBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        noteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        noteBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noteLbl = new JLabel("⚠  Username and password are managed by the Admin.");
        noteLbl.setFont(AppTheme.FONT_SMALL);
        noteLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        noteLbl.setForeground(new Color(0x856404));
        noteBox.add(noteLbl);

        JButton saveBtn = UIComponents.primaryButton("💾 Save Changes");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(190, 42));
        saveBtn.addActionListener(e -> saveProfile());

        right.add(formTitle);
        right.add(Box.createVerticalStrut(4));
        right.add(subLbl);
        right.add(Box.createVerticalStrut(26));
        addFieldRow(right, "Full Name",     fullNameF);
        addFieldRow(right, "Email Address", emailF);
        addFieldRow(right, "Phone Number",  phoneF);
        right.add(Box.createVerticalStrut(14));
        right.add(noteBox);
        right.add(Box.createVerticalStrut(22));
        right.add(saveBtn);

        // Assemble card
        JPanel innerWrapper = new JPanel(new BorderLayout());
        innerWrapper.setOpaque(false);
        innerWrapper.add(left, BorderLayout.WEST);

// wrapper to hold divider + right panel together
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setOpaque(false);

        rightWrapper.add(divider, BorderLayout.WEST);
        rightWrapper.add(right, BorderLayout.CENTER);

        innerWrapper.add(rightWrapper, BorderLayout.CENTER);
        card.add(innerWrapper, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void saveProfile() {
        String fn    = fullNameF.getText().trim();
        String email = emailF.getText().trim();
        String phone = phoneF.getText().trim();

        if (fn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name cannot be empty.");
            return;
        }
        try {
            currentUser.setFullName(fn);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            userDAO.updateUser(currentUser);

            // Update avatar and name label live
            avatarLabel.setText(getInitials(fn));
            displayNameLabel.setText(fn);
            JOptionPane.showMessageDialog(this, "✅ Profile updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving profile:\n" + ex.getMessage());
        }
    }

    // ── helpers ──────────────────────────────────────────────────────
    private void addFieldRow(JPanel panel, String labelText, JTextField field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(16));
    }

    private void addInfoRow(JPanel panel, String icon, String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel k = new JLabel(icon + "  " + key + ":");
        k.setFont(new Font("Segoe UI", Font.BOLD, 11));
        k.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel v = new JLabel(value != null ? value : "–");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        v.setForeground(AppTheme.TEXT_PRIMARY);

        row.add(k); row.add(v);
        panel.add(row);
    }

    private String getInitials(String name) {
        if (name == null || name.isBlank()) return "T";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) +
                String.valueOf(parts[parts.length - 1].charAt(0))).toUpperCase();
    }

    private String safeStr(String s) { return s != null ? s : ""; }
}
