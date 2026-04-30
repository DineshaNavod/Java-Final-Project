package gui;

import dao.User;
import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;

public class UGProfilePanel extends JPanel {

    private final User    currentUser;
    private final UserDAO userDAO = new UserDAO();

    private JTextField emailF, phoneF;
    private JLabel     avatarLabel, displayNameLabel, picLabel;
    private String     selectedPicPath = null;
    private BufferedImage profileImage = null;

    public UGProfilePanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        header.add(UIComponents.sectionTitle("🪪 My Profile"), BorderLayout.WEST);


        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());


        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(250, 0));
        left.setBorder(new EmptyBorder(36, 28, 36, 20));


        JPanel picPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (profileImage != null) {
                    // Draw image clipped to circle
                    g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth(), getHeight()));
                    g2.drawImage(profileImage, 0, 0, getWidth(), getHeight(), null);
                    g2.setClip(null);
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY,
                            getWidth(), getHeight(), AppTheme.ACCENT);
                    g2.setPaint(gp);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.setColor(new Color(255, 255, 255, 60));
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
                }
                g2.dispose();
            }
        };
        picPanel.setOpaque(false);
        picPanel.setPreferredSize(new Dimension(100, 100));
        picPanel.setMaximumSize(new Dimension(100, 100));
        picPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        avatarLabel = new JLabel(getInitials(currentUser.getFullName()));
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        avatarLabel.setForeground(Color.WHITE);
        picPanel.add(avatarLabel);


        if (currentUser.getProfilePic() != null && !currentUser.getProfilePic().isBlank()) {
            try {
                profileImage = ImageIO.read(new File(currentUser.getProfilePic()));
                avatarLabel.setText(""); // hide initials when image loaded
                picPanel.repaint();
            } catch (IOException ignored) {}
        }


        JButton changePicBtn = new JButton("Change Photo") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? AppTheme.ACCENT_LIGHT : AppTheme.BG_MAIN);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(AppTheme.PRIMARY);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        changePicBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        changePicBtn.setForeground(AppTheme.PRIMARY);
        changePicBtn.setContentAreaFilled(false);
        changePicBtn.setBorderPainted(false);
        changePicBtn.setFocusPainted(false);
        changePicBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        changePicBtn.setPreferredSize(new Dimension(130, 30));
        changePicBtn.setMaximumSize(new Dimension(160, 30));
        changePicBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        changePicBtn.addActionListener(e -> choosePicture(picPanel));

        picLabel = new JLabel("JPG or PNG, max 2MB");
        picLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        picLabel.setForeground(AppTheme.TEXT_MUTED);
        picLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        displayNameLabel = new JLabel(safeStr(currentUser.getFullName()));
        displayNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        displayNameLabel.setForeground(AppTheme.TEXT_PRIMARY);
        displayNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


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
        roleBadge.setMaximumSize(new Dimension(180, 26));
        roleBadge.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel roleLbl = new JLabel("Undergraduate");
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleLbl.setForeground(AppTheme.PRIMARY_DARK);
        roleBadge.add(roleLbl);


        JPanel infoRows = new JPanel();
        infoRows.setLayout(new BoxLayout(infoRows, BoxLayout.Y_AXIS));
        infoRows.setOpaque(false);
        infoRows.setBorder(new EmptyBorder(18, 0, 0, 0));
        infoRows.setAlignmentX(Component.CENTER_ALIGNMENT);
        addInfoRow(infoRows, "", "Email", safeStr(currentUser.getEmail()));
        addInfoRow(infoRows, "", "Phone", safeStr(currentUser.getPhone()));

        left.add(Box.createVerticalGlue());
        left.add(picPanel);
        left.add(Box.createVerticalStrut(10));
        left.add(changePicBtn);
        left.add(Box.createVerticalStrut(4));
        left.add(picLabel);
        left.add(Box.createVerticalStrut(14));
        left.add(displayNameLabel);
        left.add(Box.createVerticalStrut(8));
        left.add(roleBadge);
        left.add(infoRows);
        left.add(Box.createVerticalGlue());


        JSeparator divider = new JSeparator(SwingConstants.VERTICAL);
        divider.setForeground(AppTheme.BORDER);
        divider.setPreferredSize(new Dimension(1, 0));


        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);
        right.setBorder(new EmptyBorder(36, 36, 36, 36));

        JLabel formTitle = new JLabel("Edit Contact Details");
        formTitle.setFont(AppTheme.FONT_SUBTITLE);
        formTitle.setForeground(AppTheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLbl = new JLabel("You can update your email and phone number.");
        subLbl.setFont(AppTheme.FONT_SMALL);
        subLbl.setForeground(AppTheme.TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);


        JTextField fullNameDisplay = UIComponents.styledField(safeStr(currentUser.getFullName()));
        fullNameDisplay.setEditable(false);
        fullNameDisplay.setBackground(new Color(0xF0F0F0));
        fullNameDisplay.setForeground(AppTheme.TEXT_MUTED);

        emailF = UIComponents.styledField("Email Address");
        emailF.setText(safeStr(currentUser.getEmail()));

        phoneF = UIComponents.styledField("Phone Number");
        phoneF.setText(safeStr(currentUser.getPhone()));


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
        noteBox.setBorder(new EmptyBorder(9, 14, 9, 14));
        noteBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        noteBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noteLbl = new JLabel("⚠  Full name, username and password are managed by Admin.");
        noteLbl.setFont(AppTheme.FONT_SMALL);
        noteLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        noteLbl.setForeground(new Color(0x856404));
        noteBox.add(noteLbl);

        JButton saveBtn = UIComponents.primaryButton("💾 Save Changes");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(190, 42));
        saveBtn.addActionListener(e -> saveProfile());

        right.add(formTitle);
        right.add(Box.createVerticalStrut(4));
        right.add(subLbl);
        right.add(Box.createVerticalStrut(24));
        addFieldRow(right, "Full Name (read-only)", fullNameDisplay);
        addFieldRow(right, "Email Address",         emailF);
        addFieldRow(right, "Phone Number",           phoneF);
        right.add(Box.createVerticalStrut(14));
        right.add(noteBox);
        right.add(Box.createVerticalStrut(22));
        right.add(saveBtn);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(left, BorderLayout.WEST);
        wrapper.add(right, BorderLayout.CENTER);
        card.add(wrapper, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void choosePicture(JPanel picPanel) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Profile Picture");
        chooser.setFileFilter(new FileNameExtensionFilter("Image Files (JPG, PNG)", "jpg", "jpeg", "png"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile.length() > 2 * 1024 * 1024) {
                JOptionPane.showMessageDialog(this, "File too large. Please choose an image under 2MB.");
                return;
            }
            try {
                profileImage     = ImageIO.read(selectedFile);
                selectedPicPath  = selectedFile.getAbsolutePath();
                avatarLabel.setText(""); // hide initials
                picLabel.setText("✅ " + selectedFile.getName());
                picLabel.setForeground(AppTheme.SUCCESS);
                picPanel.repaint();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Could not read image: " + ex.getMessage());
            }
        }
    }

    private void saveProfile() {
        String email = emailF.getText().trim();
        String phone = phoneF.getText().trim();

        try {
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            if (selectedPicPath != null) {
                currentUser.setProfilePic(selectedPicPath);
            }
            userDAO.updateUser(currentUser);
            JOptionPane.showMessageDialog(this, "✅ Profile updated successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error saving profile:\n" + ex.getMessage());
        }
    }

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
        if (name == null || name.isBlank()) return "S";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) +
                String.valueOf(parts[parts.length - 1].charAt(0))).toUpperCase();
    }

    private String safeStr(String s) { return s != null ? s : ""; }
}
