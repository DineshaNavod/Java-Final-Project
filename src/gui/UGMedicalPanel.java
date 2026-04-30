package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UGMedicalPanel extends JPanel {

    private final User       currentUser;
    private final LecMedicalDAO medDAO = new LecMedicalDAO();

    public UGMedicalPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🏥 My Medical Records"), BorderLayout.WEST);

        JButton refBtn = UIComponents.outlineButton("↻ Refresh");
        refBtn.addActionListener(e -> refresh());
        header.add(refBtn, BorderLayout.EAST);


        UIComponents.RoundedPanel summary = buildSummaryRow();
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));


        String[] cols = {"Medical ID", "Submitted", "Start Date", "End Date", "Status", "Description"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = UIComponents.styledTable(cols);
        table.setModel(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(85);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(280);


        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    setForeground("approved".equals(val) ? AppTheme.SUCCESS : AppTheme.DANGER);
                    setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        this.tableModel = model;
        this.table = table;


        loadData();


        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) showDetailDialog(row);
                }
            }
        });

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);

        JLabel hint = new JLabel("Double-click any row to view the full description.");
        hint.setFont(AppTheme.FONT_SMALL);
        hint.setForeground(AppTheme.TEXT_MUTED);
        hint.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(hint, BorderLayout.SOUTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);
        center.add(summary);
        center.add(Box.createVerticalStrut(16));
        center.add(card);

        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JTable            table;
    private DefaultTableModel tableModel;
    private List<LecMedical> records;

    private UIComponents.RoundedPanel buildSummaryRow() {
        UIComponents.RoundedPanel banner = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        banner.setLayout(new GridLayout(1, 3, 0, 0));
        approvedLbl   = summaryVal("–");
        pendingLbl    = summaryVal("–");
        totalMedLbl   = summaryVal("–");
        banner.add(wrapSummItem("Total Submissions", totalMedLbl,  AppTheme.PRIMARY));
        banner.add(wrapSummItem("Approved",          approvedLbl,  AppTheme.SUCCESS));
        banner.add(wrapSummItem("Not Approved",      pendingLbl,   AppTheme.DANGER));
        return banner;
    }

    private JLabel approvedLbl, pendingLbl, totalMedLbl;

    private JLabel summaryVal(String v) {
        JLabel l = new JLabel(v);
        l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel wrapSummItem(String label, JLabel val, Color accent) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 24, 18, 24));
        val.setForeground(accent);
        JLabel key = new JLabel(label);
        key.setFont(AppTheme.FONT_SMALL);
        key.setForeground(AppTheme.TEXT_MUTED);
        key.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(val);
        p.add(Box.createVerticalStrut(4));
        p.add(key);
        return p;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        String regNo = currentUser.getUsername();
        try {
            records = medDAO.getByStudent(regNo);
            long approved  = records.stream().filter(m -> "approved".equals(m.getStatus())).count();
            long notApproved = records.size() - approved;

            totalMedLbl.setText(String.valueOf(records.size()));
            approvedLbl.setText(String.valueOf(approved));
            pendingLbl.setText(String.valueOf(notApproved));

            for (LecMedical m : records) {
                String desc = m.getDescription() != null
                    ? (m.getDescription().length() > 55
                       ? m.getDescription().substring(0, 55) + "…" : m.getDescription()) : "–";
                tableModel.addRow(new Object[]{
                    m.getMedicalId(), m.getSubmissionDate(),
                    m.getAffectedStartDate(), m.getAffectedEndDate(),
                    m.getStatus(), desc
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void refresh() { loadData(); }

    private void showDetailDialog(int row) {
        if (records == null || row >= records.size()) return;
        LecMedical m = records.get(row);

        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this),
                "Medical Detail – " + m.getMedicalId(), true);
        dlg.setSize(480, 360);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel("🏥 Medical Record: " + m.getMedicalId());
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(18));

        String[][] fields = {
            {"Submitted",   m.getSubmissionDate()    != null ? m.getSubmissionDate().toString()    : "–"},
            {"Affected From",m.getAffectedStartDate()!= null ? m.getAffectedStartDate().toString() : "–"},
            {"Affected To", m.getAffectedEndDate()   != null ? m.getAffectedEndDate().toString()   : "–"},
            {"Session ID",  m.getSessionId()         != null ? m.getSessionId()                    : "–"},
            {"Status",      m.getStatus()},
        };

        for (String[] f : fields) {
            JPanel row2 = new JPanel(new BorderLayout(12, 0));
            row2.setOpaque(false);
            row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            row2.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel k = new JLabel(f[0] + ":");
            k.setFont(new Font("Segoe UI", Font.BOLD, 12));
            k.setForeground(AppTheme.TEXT_SECONDARY);
            k.setPreferredSize(new Dimension(110, 20));

            JLabel v = new JLabel(f[1]);
            v.setFont(AppTheme.FONT_BODY);
            if ("Status".equals(f[0])) {
                v.setForeground("approved".equals(f[1]) ? AppTheme.SUCCESS : AppTheme.DANGER);
                v.setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
            } else {
                v.setForeground(AppTheme.TEXT_PRIMARY);
            }
            row2.add(k, BorderLayout.WEST);
            row2.add(v, BorderLayout.CENTER);
            content.add(row2);
            content.add(Box.createVerticalStrut(8));
        }


        JLabel descKey = new JLabel("Description:");
        descKey.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descKey.setForeground(AppTheme.TEXT_SECONDARY);
        descKey.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descArea = new JTextArea(m.getDescription() != null ? m.getDescription() : "–");
        descArea.setFont(AppTheme.FONT_BODY);
        descArea.setForeground(AppTheme.TEXT_PRIMARY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(AppTheme.BG_MAIN);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                new EmptyBorder(8, 10, 8, 10)));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        content.add(descKey);
        content.add(Box.createVerticalStrut(6));
        content.add(descArea);

        dlg.setContentPane(new JScrollPane(content) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        dlg.setVisible(true);
    }
}
