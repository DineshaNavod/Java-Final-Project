package gui;

import dao.User;
import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LecProfilePanel extends JPanel {

    private final User    lecturer;
    private final UserDAO userDAO = new UserDAO();

    private JTextField fullNameF, emailF, phoneF;
    private JLabel     avatarLabel;

    public LecProfilePanel(User lecturer) {
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
        header.add(UIComponents.sectionTitle("👤 My Profile"), BorderLayout.WEST);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());


        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(220, 0));
        left.setBorder(new EmptyBorder(40, 28, 40, 20));

        JPanel avatarCircle = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY, getWidth(), getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatarCircle.setOpaque(false);
        avatarCircle.setPreferredSize(new Dimension(90, 90));
        avatarCircle.setMaximumSize(new Dimension(90, 90));

        String initials = getInitials(lecturer.getFullName());
        avatarLabel = new JLabel(initials);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        avatarLabel.setForeground(Color.WHITE);
        avatarCircle.add(avatarLabel);
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel(lecturer.getFullName() != null ? lecturer.getFullName() : "Lecturer");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        nameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        roleBadge.setMaximumSize(new Dimension(160, 28));
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel roleLabel = new JLabel("Lecturer");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLabel.setForeground(AppTheme.PRIMARY_DARK);
        roleBadge.add(roleLabel);

        // Info
        JPanel infoRows = new JPanel();
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));
        infoRows.setOpaque(false);
        infoRows.setBorder(new EmptyBorder(16, 0, 0, 0));
        infoRows.setAlignmentX(Component.CENTER_ALIGNMENT);
        addInfoRow(infoRows, "", "ID",       String.valueOf(lecturer.getUserId()));
        addInfoRow(infoRows, "", "Username", lecturer.getUsername());

        left.add(Box.createVerticalGlue());
        left.add(avatarCircle);
        left.add(Box.createVerticalStrut(12));
        left.add(nameLabel);
        left.add(Box.createVerticalStrut(8));
        left.add(roleBadge);
        left.add(infoRows);
        left.add(Box.createVerticalGlue());

        // ── RIGHT FORM ──
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(40, 36, 40, 36));

        JLabel formTitle = new JLabel("Edit Profile");
        formTitle.setFont(AppTheme.FONT_SUBTITLE);
        formTitle.setForeground(AppTheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("You can update your name, email and phone.");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        fullNameF = UIComponents.styledField("Full Name");
        emailF    = UIComponents.styledField("Email");
        phoneF    = UIComponents.styledField("Phone");
        fullNameF.setText(lecturer.getFullName() != null ? lecturer.getFullName() : "");
        emailF.setText(lecturer.getEmail()    != null ? lecturer.getEmail()    : "");
        phoneF.setText(lecturer.getPhone()    != null ? lecturer.getPhone()    : "");

        // Warning note
        JPanel noteBox = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0xFFF3CD));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
                g2.dispose();
            }
        };
        noteBox.setOpaque(false);
        noteBox.setBorder(new EmptyBorder(10,14,10,14));
        noteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        noteBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noteLbl = new JLabel("⚠ Username and password are managed by Admin.");
        noteLbl.setFont(AppTheme.FONT_SMALL);
        noteLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        noteLbl.setForeground(new Color(0x856404));
        noteBox.add(noteLbl);

        JButton saveBtn = UIComponents.primaryButton("💾 Save Changes");
        saveBtn.setMaximumSize(new Dimension(180, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> save(nameLabel));

        right.add(formTitle);
        right.add(Box.createVerticalStrut(4));
        right.add(sub);
        right.add(Box.createVerticalStrut(22));
        addFieldRow(right, "Full Name", fullNameF);
        addFieldRow(right, "Email",     emailF);
        addFieldRow(right, "Phone",     phoneF);
        right.add(Box.createVerticalStrut(12));
        right.add(noteBox);
        right.add(Box.createVerticalStrut(20));
        right.add(saveBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(left,  BorderLayout.WEST);
        wrapper.add(right, BorderLayout.CENTER);
        card.add(wrapper);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void save(JLabel nameLabel) {
        String fn = fullNameF.getText().trim();
        if (fn.isEmpty()) { JOptionPane.showMessageDialog(this, "Name cannot be empty."); return; }
        try {
            lecturer.setFullName(fn);
            lecturer.setEmail(emailF.getText().trim());
            lecturer.setPhone(phoneF.getText().trim());
            userDAO.updateUser(lecturer);
            nameLabel.setText(fn);
            avatarLabel.setText(getInitials(fn));
            JOptionPane.showMessageDialog(this, "✅ Profile updated!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void addFieldRow(JPanel panel, String label, JTextField field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(4));
        panel.add(field);
        panel.add(Box.createVerticalStrut(14));
    }

    private void addInfoRow(JPanel panel, String icon, String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel k = new JLabel(icon + " " + key + ":");
        k.setFont(new Font("Segoe UI", Font.BOLD, 11));
        k.setForeground(AppTheme.TEXT_SECONDARY);
        JLabel v = new JLabel(value != null ? value : "–");
        v.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        v.setForeground(AppTheme.TEXT_PRIMARY);
        row.add(k); row.add(v);
        panel.add(row);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "L";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return String.valueOf(p[0].charAt(0)).toUpperCase();
        return (String.valueOf(p[0].charAt(0)) + String.valueOf(p[p.length-1].charAt(0))).toUpperCase();
    }
}
