package gui;

import dao.User;
import dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;

public class ProfilePanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO = new UserDAO();

    private JTextField fullNameF, emailF, phoneF;
    private JLabel avatarLabel;

    public ProfilePanel(User user) {
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
        header.add(UIComponents.sectionTitle("👤 My Profile"), BorderLayout.WEST);

        // ── MAIN CARD ──
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout(0, 0));

        // Left: Avatar side
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(240, 0));
        leftPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        // Avatar circle
        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY,
                        getWidth(), getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                // Ring
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(4, 4, getWidth() - 8, getHeight() - 8);
                g2.dispose();
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setPreferredSize(new Dimension(100, 100));
        avatarCircle.setMaximumSize(new Dimension(100, 100));

        String initials = getInitials(currentUser.getFullName());
        avatarLabel = new JLabel(initials);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        avatarLabel.setForeground(Color.WHITE);
        avatarCircle.add(avatarLabel);

        JLabel nameLabel = new JLabel(currentUser.getFullName() != null ? currentUser.getFullName() : "Admin");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Role badge
        JPanel roleBadge = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppTheme.ACCENT_LIGHT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        roleBadge.setOpaque(false);
        roleBadge.setMaximumSize(new Dimension(160, 28));
        JLabel roleLabel = new JLabel("" + currentUser.getRoleName());
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setForeground(AppTheme.PRIMARY_DARK);
        roleBadge.add(roleLabel);

        // Info rows
        JPanel infoRows = new JPanel();
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));
        infoRows.setOpaque(false);
        infoRows.setBorder(new EmptyBorder(20, 0, 0, 0));

        addInfoRow(infoRows, "", "ID", String.valueOf(currentUser.getUserId()));
        addInfoRow(infoRows, "", "Username", currentUser.getUsername());

        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoRows.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(avatarCircle);
        leftPanel.add(Box.createVerticalStrut(14));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(roleBadge);
        leftPanel.add(infoRows);
        leftPanel.add(Box.createVerticalGlue());

        // Vertical divider
        JSeparator divider = new JSeparator(SwingConstants.VERTICAL);
        divider.setForeground(AppTheme.BORDER);
        divider.setPreferredSize(new Dimension(1, 0));

        // Right: Edit form
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(40, 36, 40, 36));

        JLabel formTitle = new JLabel("Edit Profile Details");
        formTitle.setFont(AppTheme.FONT_SUBTITLE);
        formTitle.setForeground(AppTheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitle = new JLabel("Update your contact information below");
        subTitle.setFont(AppTheme.FONT_SMALL);
        subTitle.setForeground(AppTheme.TEXT_MUTED);
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameF = UIComponents.styledField("Full Name");
        emailF    = UIComponents.styledField("Email Address");
        phoneF    = UIComponents.styledField("Phone Number");

        fullNameF.setText(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        emailF.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        phoneF.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "");

        // Non-editable fields (username/password note)
        JPanel noteBox = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFF3CD));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(0xFFC107));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
            }
        };
        noteBox.setOpaque(false);
        noteBox.setBorder(new EmptyBorder(10, 14, 10, 14));
        noteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        JLabel noteLabel = new JLabel("⚠  Username and password cannot be changed here.");
        noteLabel.setFont(AppTheme.FONT_SMALL);
        noteLabel.setForeground(new Color(0x856404));
        noteBox.add(noteLabel);

        JButton saveBtn = UIComponents.primaryButton("💾 Save Changes");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(180, 42));
        saveBtn.addActionListener(e -> saveProfile());

        // Layout form rows
        addFormRow(rightPanel, "Full Name", fullNameF);
        addFormRow(rightPanel, "Email Address", emailF);
        addFormRow(rightPanel, "Phone Number", phoneF);

        noteBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(subTitle);
        rightPanel.add(Box.createVerticalStrut(28));

        // Re-add form rows in correct order
        rightPanel.removeAll();
        rightPanel.add(formTitle);
        rightPanel.add(Box.createVerticalStrut(4));
        rightPanel.add(subTitle);
        rightPanel.add(Box.createVerticalStrut(24));
        addFormRowDirect(rightPanel, "Full Name", fullNameF);
        addFormRowDirect(rightPanel, "Email Address", emailF);
        addFormRowDirect(rightPanel, "Phone Number", phoneF);
        rightPanel.add(Box.createVerticalStrut(16));
        rightPanel.add(noteBox);
        rightPanel.add(Box.createVerticalStrut(24));
        rightPanel.add(saveBtn);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(divider,   BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        // Ensure right panel expands
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(leftPanel, BorderLayout.WEST);
        wrapper.add(rightPanel, BorderLayout.CENTER);
        card.setLayout(new BorderLayout());
        card.add(wrapper, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    private void addFormRow(JPanel panel, String label, JTextField field) {
        // placeholder only, real add is via addFormRowDirect
    }

    private void addFormRowDirect(JPanel panel, String labelText, JTextField field) {
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
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel k = new JLabel(icon + " " + key + ":");
        k.setFont(new Font("Segoe UI", Font.BOLD, 11));
        k.setForeground(AppTheme.TEXT_SECONDARY);

        JLabel v = new JLabel(value != null ? value : "–");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        v.setForeground(AppTheme.TEXT_PRIMARY);

        row.add(k); row.add(v);
        panel.add(row);
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

            // Update avatar initials
            avatarLabel.setText(getInitials(fn));
            JOptionPane.showMessageDialog(this, "✅ Profile updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving profile:\n" + ex.getMessage());
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "A";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[parts.length - 1].charAt(0))).toUpperCase();
    }
}
