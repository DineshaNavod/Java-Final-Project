package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class TOMedicalPanel extends JPanel {

    private final TOMedicalDAO medDAO = new TOMedicalDAO();
    private final UndergraduateDAO ugDAO  = new UndergraduateDAO();
    private final TOTimetableDAO ttDAO  = new TOTimetableDAO();

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> statusFilter;
    private JTextField        searchField;

    private static final String[] COLS = {
        "Medical ID", "Reg No", "Student Name", "Submitted",
        "Start Date", "End Date", "Status", "Description"
    };

    public TOMedicalPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadAll();
    }

    private void build() {
        // ── HEADER ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🏥 Medical Records"), BorderLayout.WEST);
        JButton addBtn = UIComponents.primaryButton("＋ Add Medical");
        addBtn.addActionListener(e -> showFormDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        // ── FILTER ──
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        searchField = UIComponents.styledField("Search reg no or name…");
        searchField.setPreferredSize(new Dimension(200, 36));

        statusFilter = new JComboBox<>(new String[]{"All", "approved", "not approved"});
        statusFilter.setFont(AppTheme.FONT_BODY);
        statusFilter.setPreferredSize(new Dimension(150, 36));

        JButton filterBtn = UIComponents.outlineButton("🔍 Filter");
        JButton refBtn    = UIComponents.outlineButton("↻ Refresh");
        filterBtn.addActionListener(e -> loadAll());
        refBtn.addActionListener(e -> loadAll());
        statusFilter.addActionListener(e -> loadAll());

        filterRow.add(searchField);
        filterRow.add(new JLabel("Status:")); filterRow.add(statusFilter);
        filterRow.add(filterBtn); filterRow.add(refBtn);

        // ── TABLE ──
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(COLS);
        table.setModel(model);
        int[] widths = {80, 90, 150, 100, 95, 95, 110, 200};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Status colour
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

        // ── ACTIONS ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton editBtn    = UIComponents.outlineButton("✏ Edit");
        JButton approveBtn = UIComponents.primaryButton("✅ Approve");
        JButton rejectBtn  = UIComponents.outlineButton("❌ Reject");
        JButton delBtn     = UIComponents.dangerButton("🗑 Delete");

        editBtn.addActionListener(e    -> editSelected());
        approveBtn.addActionListener(e -> changeStatus("approved"));
        rejectBtn.addActionListener(e  -> changeStatus("not approved"));
        delBtn.addActionListener(e     -> deleteSelected());
        btnRow.add(editBtn); btnRow.add(approveBtn);
        btnRow.add(rejectBtn); btnRow.add(delBtn);

        // ── CARD ──
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                    BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow,                       BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    // ── LOAD ─────────────────────────────────────────────────────────
    private void loadAll() {
        model.setRowCount(0);
        try {
            Map<String, String> nameMap = new HashMap<>();
            ugDAO.getAll().forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            List<TOMedical> list = medDAO.getAll();
            String sel    = statusFilter.getSelectedItem().toString();
            String search = searchField.getText().trim().toLowerCase();

            for (TOMedical m : list) {
                if (!"All".equals(sel) && !sel.equals(m.getStatus())) continue;
                String name = nameMap.getOrDefault(m.getRegNo(), "–");
                if (!search.isEmpty()
                    && !m.getRegNo().toLowerCase().contains(search)
                    && !name.toLowerCase().contains(search)) continue;

                String desc = m.getDescription() != null
                    ? (m.getDescription().length() > 50
                       ? m.getDescription().substring(0, 50) + "…" : m.getDescription()) : "–";

                model.addRow(new Object[]{
                    m.getMedicalId(), m.getRegNo(), name,
                    m.getSubmissionDate(), m.getAffectedStartDate(), m.getAffectedEndDate(),
                    m.getStatus(), desc
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record."); return; }
        String medId = model.getValueAt(row, 0).toString();
        try {
            for (TOMedical m : medDAO.getAll()) {
                if (m.getMedicalId().equals(medId)) { showFormDialog(m); return; }
            }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void changeStatus(String newStatus) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record."); return; }
        String medId = model.getValueAt(row, 0).toString();
        String name  = model.getValueAt(row, 2).toString();
        String label = "approved".equals(newStatus) ? "approve" : "reject";
        int c = JOptionPane.showConfirmDialog(this,
            "Do you want to " + label + " the medical for " + name + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            try {
                medDAO.updateStatus(medId, newStatus);
                loadAll();
                JOptionPane.showMessageDialog(this,
                    "Medical record " + ("approved".equals(newStatus) ? "approved ✅" : "rejected ❌"));
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record."); return; }
        String medId = model.getValueAt(row, 0).toString();
        String name  = model.getValueAt(row, 2).toString();
        int c = JOptionPane.showConfirmDialog(this,
            "Delete medical record for " + name + " (ID: " + medId + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try { medDAO.deleteMedical(medId); loadAll(); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ── FORM DIALOG ───────────────────────────────────────────────────
    private void showFormDialog(TOMedical existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Medical Record" : "Add Medical Record", true);
        dlg.setSize(480, 560);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_MAIN);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titleLbl = new JLabel(isEdit ? "✏ Edit Medical Record" : "🏥 Add Medical Record");
        titleLbl.setFont(AppTheme.FONT_TITLE);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField medIdF    = UIComponents.styledField("e.g. MED010");
        JComboBox<String> ugBox = new JComboBox<>();
        ugBox.setFont(AppTheme.FONT_BODY);
        JTextField submitF   = UIComponents.styledField("YYYY-MM-DD");
        JTextField startF    = UIComponents.styledField("YYYY-MM-DD");
        JTextField endF      = UIComponents.styledField("YYYY-MM-DD");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"not approved", "approved"});
        statusBox.setFont(AppTheme.FONT_BODY);
        JComboBox<String> sessionBox = new JComboBox<>();
        sessionBox.setFont(AppTheme.FONT_BODY);

        JTextArea descArea = new JTextArea(3, 28);
        descArea.setFont(AppTheme.FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descSP = new JScrollPane(descArea);
        descSP.setAlignmentX(Component.LEFT_ALIGNMENT);
        descSP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        descSP.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        try {
            ugDAO.getAll().forEach(u -> ugBox.addItem(u.getRegNo() + " | " + u.getFullName()));
            ttDAO.getAllSessions().forEach(t ->
                sessionBox.addItem(t.getSessionId() + " | " + t.getCCode() + " | " + t.getType()));
        } catch (SQLException ignored) {}

        if (isEdit) {
            medIdF.setText(existing.getMedicalId());
            medIdF.setEditable(false);
            medIdF.setBackground(new Color(0xEAEAEA));
            submitF.setText(existing.getSubmissionDate() != null ? existing.getSubmissionDate().toString() : "");
            startF.setText(existing.getAffectedStartDate()!= null ? existing.getAffectedStartDate().toString() : "");
            endF.setText(existing.getAffectedEndDate()    != null ? existing.getAffectedEndDate().toString()   : "");
            descArea.setText(existing.getDescription() != null ? existing.getDescription() : "");
            statusBox.setSelectedItem(existing.getStatus());
            for (int i = 0; i < ugBox.getItemCount(); i++) {
                if (ugBox.getItemAt(i).startsWith(existing.getRegNo())) { ugBox.setSelectedIndex(i); break; }
            }
            if (existing.getSessionId() != null) {
                for (int i = 0; i < sessionBox.getItemCount(); i++) {
                    if (sessionBox.getItemAt(i).startsWith(existing.getSessionId())) { sessionBox.setSelectedIndex(i); break; }
                }
            }
        }

        Object[][] rows = {
            {"Medical ID",         medIdF},
            {"Student",            ugBox},
            {"Submission Date",    submitF},
            {"Affected Start Date",startF},
            {"Affected End Date",  endF},
            {"Status",             statusBox},
            {"Session",            sessionBox},
        };

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        for (Object[] row : rows) {
            JLabel lbl = new JLabel((String) row[0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            formPanel.add(lbl); formPanel.add(Box.createVerticalStrut(4));
            formPanel.add((Component) row[1]); formPanel.add(Box.createVerticalStrut(10));
        }
        JLabel descLbl = new JLabel("Description");
        descLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        descLbl.setForeground(AppTheme.TEXT_SECONDARY);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(descLbl);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(descSP);

        JButton saveBtn = UIComponents.primaryButton(isEdit ? "💾 Update" : "💾 Save");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String medId   = medIdF.getText().trim();
                String regNo   = ugBox.getSelectedItem().toString().split("\\|")[0].trim();
                String submit  = submitF.getText().trim();
                String start   = startF.getText().trim();
                String end     = endF.getText().trim();
                String status  = statusBox.getSelectedItem().toString();
                String sessId  = sessionBox.getSelectedItem().toString().split("\\|")[0].trim();
                String desc    = descArea.getText().trim();

                if (medId.isEmpty() || submit.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Medical ID and all dates are required."); return;
                }
                TOMedical m = new TOMedical(medId, Date.valueOf(submit), desc,
                        Date.valueOf(start), Date.valueOf(end), status, regNo, sessId);
                if (isEdit) {
                    medDAO.updateMedical(m);
                    JOptionPane.showMessageDialog(dlg, "✅ Medical record updated!");
                } else {
                    if (medDAO.existsById(medId)) {
                        JOptionPane.showMessageDialog(dlg, "⚠ Medical ID already exists."); return;
                    }
                    medDAO.addMedical(m);
                    JOptionPane.showMessageDialog(dlg, "✅ Medical record added!");
                }
                loadAll(); dlg.dispose();
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(dlg, "Invalid date format. Use YYYY-MM-DD.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(16));
        content.add(formPanel);
        content.add(Box.createVerticalStrut(14));
        content.add(saveBtn);

        dlg.setContentPane(new JScrollPane(content) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        dlg.setVisible(true);
    }
}
