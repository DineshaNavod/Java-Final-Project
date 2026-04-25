package gui;

import db.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));
        header.add(UIComponents.sectionTitle("⚙ Settings"), BorderLayout.WEST);

        JPanel grid = new JPanel(new GridLayout(1, 2, 20, 0));
        grid.setOpaque(false);

        grid.add(buildDbCard());
        grid.add(buildAboutCard());

        add(header,  BorderLayout.NORTH);
        add(grid,    BorderLayout.CENTER);
    }

    private JPanel buildDbCard() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        JLabel title = new JLabel("🗄 Database Connection");
        title.setFont(AppTheme.FONT_SUBTITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Test and view current database configuration");
        sub.setFont(AppTheme.FONT_SMALL);
        sub.setForeground(AppTheme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // DB info rows
        String[][] info = {
            {"Database",  "technova"},
            {"Host",      "localhost"},
            {"Port",      "3306"},
            {"Driver",    "MySQL Connector/J"},
        };

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setBorder(new EmptyBorder(18, 0, 18, 0));

        for (String[] row : info) {
            JPanel r = new JPanel(new BorderLayout());
            r.setOpaque(false);
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            r.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER),
                    new EmptyBorder(6, 0, 6, 0)));

            JLabel key = new JLabel(row[0]);
            key.setFont(new Font("Segoe UI", Font.BOLD, 12));
            key.setForeground(AppTheme.TEXT_SECONDARY);

            JLabel val = new JLabel(row[1]);
            val.setFont(AppTheme.FONT_BODY);
            val.setForeground(AppTheme.TEXT_PRIMARY);

            r.add(key, BorderLayout.WEST);
            r.add(val, BorderLayout.EAST);
            infoPanel.add(r);
        }

        // Status indicator
        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        statusRow.setOpaque(false);
        statusRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel statusDot = new JLabel("●");
        statusDot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel statusText = new JLabel("Not tested");
        statusText.setFont(AppTheme.FONT_BODY);
        statusText.setForeground(AppTheme.TEXT_SECONDARY);
        statusDot.setForeground(AppTheme.TEXT_MUTED);
        statusRow.add(statusDot);
        statusRow.add(statusText);

        JButton testBtn = UIComponents.primaryButton("🔌 Test Connection");
        testBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        testBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        testBtn.addActionListener(e -> {
            try {
                Connection con = DatabaseConnection.getConnection();
                if (con != null && !con.isClosed()) {
                    statusDot.setForeground(AppTheme.SUCCESS);
                    statusText.setText("Connected successfully");
                    statusText.setForeground(AppTheme.SUCCESS);
                }
            } catch (SQLException ex) {
                statusDot.setForeground(AppTheme.DANGER);
                statusText.setText("Connection failed");
                statusText.setForeground(AppTheme.DANGER);
                JOptionPane.showMessageDialog(this, "Connection error:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(infoPanel);
        card.add(statusRow);
        card.add(Box.createVerticalStrut(16));
        card.add(testBtn);

        return card;
    }

    private JPanel buildAboutCard() {
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        // Logo
        JPanel logoCircle = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppTheme.PRIMARY_DARK, getWidth(), getHeight(), AppTheme.ACCENT);
                g2.setPaint(gp);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(64, 64));
        logoCircle.setMaximumSize(new Dimension(64, 64));
        JLabel icon = new JLabel("🎓");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoCircle.add(icon);
        logoCircle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("TechNova");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appName.setForeground(AppTheme.PRIMARY_DARK);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("Faculty Management System  v1.0");
        version.setFont(AppTheme.FONT_SMALL);
        version.setForeground(AppTheme.TEXT_MUTED);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        String[][] details = {
            {"📚", "Course:    ICT2132 – OOP Practicum"},
            {"🏛", "Faculty:   Faculty of Technology"},
            {"🎓", "Module:    Mini Project – B08"},
            {"💻", "Stack:      Java Swing + MySQL"},
            {"📅", "Year:       2026"},
        };

        JPanel detPanel = new JPanel();
        detPanel.setLayout(new BoxLayout(detPanel, BoxLayout.Y_AXIS));
        detPanel.setOpaque(false);
        detPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detPanel.setBorder(new EmptyBorder(14, 0, 14, 0));

        for (String[] d : details) {
            JLabel lbl = new JLabel(d[0] + "  " + d[1]);
            lbl.setFont(AppTheme.FONT_BODY);
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setBorder(new EmptyBorder(4, 0, 4, 0));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            detPanel.add(lbl);
        }

        JLabel footerLbl = new JLabel("Built with ❤ for University of Ruhuna");
        footerLbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        footerLbl.setForeground(AppTheme.TEXT_MUTED);
        footerLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(logoCircle);
        card.add(Box.createVerticalStrut(14));
        card.add(appName);
        card.add(Box.createVerticalStrut(4));
        card.add(version);
        card.add(Box.createVerticalStrut(16));
        card.add(sep);
        card.add(detPanel);
        card.add(Box.createVerticalGlue());
        card.add(footerLbl);

        return card;
    }
}
