package gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class UIComponents {

    // ── Rounded Panel ──────────────────────────────────────────────
    public static class RoundedPanel extends JPanel {
        private int radius;
        private Color bg;

        public RoundedPanel(int radius, Color bg) {
            this.radius = radius;
            this.bg = bg;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── Stat Card ───────────────────────────────────────────────────
    public static class StatCard extends RoundedPanel {
        private JLabel numLabel;
        private JLabel titleLabel;
        private JLabel iconLabel;
        private Color accentColor;

        public StatCard(String icon, String title, String value, Color accent) {
            super(AppTheme.CORNER_RADIUS, AppTheme.BG_CARD);
            this.accentColor = accent;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20, 22, 20, 22));

            JPanel left = new JPanel(new GridLayout(2, 1, 0, 4));
            left.setOpaque(false);

            numLabel = new JLabel(value);
            numLabel.setFont(AppTheme.FONT_STAT_NUM);
            numLabel.setForeground(AppTheme.TEXT_PRIMARY);

            titleLabel = new JLabel(title);
            titleLabel.setFont(AppTheme.FONT_STAT_LABEL);
            titleLabel.setForeground(AppTheme.TEXT_SECONDARY);

            left.add(numLabel);
            left.add(titleLabel);

            iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel iconWrapper = new JPanel(new GridBagLayout());
            iconWrapper.setOpaque(false);
            iconWrapper.setPreferredSize(new Dimension(56, 56));

            RoundedPanel iconBg = new RoundedPanel(28, accent.brighter().brighter()) {
                @Override
                public Dimension getPreferredSize() { return new Dimension(50, 50); }
            };
            iconBg.setLayout(new GridBagLayout());
            iconBg.add(iconLabel);
            iconWrapper.add(iconBg);

            add(left, BorderLayout.CENTER);
            add(iconWrapper, BorderLayout.EAST);

            // Bottom color bar
            JPanel bar = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(accent);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
                    g2.dispose();
                }
            };
            bar.setPreferredSize(new Dimension(0, 4));
            bar.setOpaque(false);
            add(bar, BorderLayout.SOUTH);
        }

        public void setValue(String v) { numLabel.setText(v); }
    }

    // ── Green Button ────────────────────────────────────────────────
    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? AppTheme.PRIMARY_DARK
                        : getModel().isRollover() ? AppTheme.PRIMARY_HOVER : AppTheme.PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("dialog", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 36));
        return btn;
    }

    // ── Danger Button ───────────────────────────────────────────────
    public static JButton dangerButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? new Color(0xC0392B) : AppTheme.DANGER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AppTheme.FONT_BUTTON);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 36));
        return btn;
    }

    // ── Outlined Button ─────────────────────────────────────────────
    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(AppTheme.ACCENT_LIGHT);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.setColor(AppTheme.PRIMARY);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(AppTheme.FONT_BUTTON);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        btn.setForeground(AppTheme.PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 36));
        return btn;
    }

    // ── Styled Table ─────────────────────────────────────────────────
    public static JTable styledTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(AppTheme.FONT_BODY);
        table.setRowHeight(38);
        table.setShowVerticalLines(false);
        table.setGridColor(AppTheme.BORDER);
        table.setSelectionBackground(AppTheme.ACCENT_LIGHT);
        table.setSelectionForeground(AppTheme.TEXT_PRIMARY);
        table.setBackground(Color.WHITE);
        table.setForeground(AppTheme.TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(AppTheme.FONT_SUBTITLE);
        header.setBackground(AppTheme.PRIMARY);
        header.setForeground(Color.black);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createEmptyBorder());
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Alternating row renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                if (!sel) {
                    comp.setBackground(r % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                    comp.setForeground(AppTheme.TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return comp;
            }
        });
        return table;
    }

    // ── Styled TextField ─────────────────────────────────────────────
    public static JTextField styledField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(AppTheme.FONT_BODY);
        field.setForeground(AppTheme.TEXT_PRIMARY);
        field.setBackground(AppTheme.BG_MAIN);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        field.setOpaque(false);
        return field;
    }

    // ── Styled Label ─────────────────────────────────────────────────
    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(AppTheme.FONT_TITLE);
        lbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        lbl.setForeground(AppTheme.TEXT_PRIMARY);
        return lbl;
    }

    // ── Scrollpane wrapper ───────────────────────────────────────────
    public static JScrollPane scrolled(Component comp) {
        JScrollPane sp = new JScrollPane(comp);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(Color.WHITE);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }
}
