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

public class TOAttendancePanel extends JPanel {

    private final TOAttendanceDAO attDAO   = new TOAttendanceDAO();
    private final LecUndergraduateDAO ugDAO    = new LecUndergraduateDAO();
    private final TOTimetableDAO ttDAO    = new TOTimetableDAO();

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> statusFilter;
    private JTextField        searchField;

    private static final String[] COLS = {
        "Att ID", "Reg No", "Student Name", "Date", "Type", "Status", "Session ID"
    };

    public TOAttendancePanel() {
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
        header.add(UIComponents.sectionTitle("📅 Attendance Management"), BorderLayout.WEST);
        JButton addBtn = UIComponents.primaryButton("＋ Add Record");
        addBtn.addActionListener(e -> showFormDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        // ── FILTER ROW ──
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        searchField = UIComponents.styledField("Search by reg no or name…");
        searchField.setPreferredSize(new Dimension(220, 36));

        statusFilter = new JComboBox<>(new String[]{"All", "present", "absent"});
        statusFilter.setFont(AppTheme.FONT_BODY);
        statusFilter.setPreferredSize(new Dimension(130, 36));

        JButton searchBtn = UIComponents.outlineButton("🔍 Filter");
        JButton refBtn    = UIComponents.outlineButton("↻ Refresh");
        JButton summaryBtn = UIComponents.outlineButton("📊 Summary");

        searchBtn.addActionListener(e -> loadAll());
        refBtn.addActionListener(e -> loadAll());
        statusFilter.addActionListener(e -> loadAll());
        summaryBtn.addActionListener(e -> showBatchSummaryDialog());

        filterRow.add(searchField);
        filterRow.add(new JLabel("Status:")); filterRow.add(statusFilter);
        filterRow.add(searchBtn); filterRow.add(refBtn); filterRow.add(summaryBtn);

        // ── TABLE ──
        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(COLS);
        table.setModel(model);
        int[] widths = {75, 90, 150, 100, 90, 80, 90};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Colour Status column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    setForeground("present".equals(val) ? AppTheme.SUCCESS : AppTheme.DANGER);
                    setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // ── ACTION ROW ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton editBtn = UIComponents.outlineButton("✏ Edit");
        JButton delBtn  = UIComponents.dangerButton("🗑 Delete");
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e  -> deleteSelected());
        btnRow.add(editBtn); btnRow.add(delBtn);

        // ── CARD ──
        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                        BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table),     BorderLayout.CENTER);
        card.add(btnRow,                           BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    // ── LOAD ─────────────────────────────────────────────────────────
    private void loadAll() {
        model.setRowCount(0);
        try {
            List<TOAttendance> list = attDAO.getAll();
            Map<String, String> nameMap = new HashMap<>();
            ugDAO.getAll().forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            String search = searchField.getText().trim().toLowerCase();
            String status = statusFilter.getSelectedItem().toString();

            for (TOAttendance a : list) {
                if (!"All".equals(status) && !status.equals(a.getStatus())) continue;
                String name = nameMap.getOrDefault(a.getRegNo(), "–");
                if (!search.isEmpty()
                    && !a.getRegNo().toLowerCase().contains(search)
                    && !name.toLowerCase().contains(search)) continue;

                model.addRow(new Object[]{
                    a.getAttId(), a.getRegNo(), name,
                    a.getAttenDate(), a.getType(), a.getStatus(), a.getSessionId()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ── EDIT ─────────────────────────────────────────────────────────
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record to edit."); return; }
        String attId = model.getValueAt(row, 0).toString();
        try {
            List<TOAttendance> all = attDAO.getAll();
            for (TOAttendance a : all) {
                if (a.getAttId().equals(attId)) { showFormDialog(a); return; }
            }
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    // ── DELETE ───────────────────────────────────────────────────────
    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a record."); return; }
        String attId = model.getValueAt(row, 0).toString();
        String name  = model.getValueAt(row, 2).toString();
        int c = JOptionPane.showConfirmDialog(this,
            "Delete attendance record for " + name + " (ID: " + attId + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try { attDAO.deleteAttendance(attId); loadAll(); }
            catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    // ── FORM DIALOG (Add / Edit) ──────────────────────────────────────
    private void showFormDialog(TOAttendance existing) {
        boolean isEdit = existing != null;
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Edit Attendance" : "Add Attendance Record", true);
        dlg.setSize(460, 470);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(AppTheme.BG_MAIN);
        content.setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titleLbl = new JLabel(isEdit ? "✏ Edit Attendance" : "📅 Add Attendance Record");
        titleLbl.setFont(AppTheme.FONT_TITLE);
        titleLbl.setForeground(AppTheme.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields
        JTextField attIdF = UIComponents.styledField("e.g. A099");
        JComboBox<String> ugBox = new JComboBox<>();
        ugBox.setFont(AppTheme.FONT_BODY);
        JTextField dateF = UIComponents.styledField("YYYY-MM-DD");
        JComboBox<String> typeBox   = new JComboBox<>(new String[]{"theory", "practical"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"present", "absent"});
        JComboBox<String> sessionBox = new JComboBox<>();
        typeBox.setFont(AppTheme.FONT_BODY);
        statusBox.setFont(AppTheme.FONT_BODY);
        sessionBox.setFont(AppTheme.FONT_BODY);

        try {
            ugDAO.getAll().forEach(u -> ugBox.addItem(u.getRegNo() + " | " + u.getFullName()));
            ttDAO.getAllSessions().forEach(t -> sessionBox.addItem(t.getSessionId() + " | " + t.getCCode() + " | " + t.getType()));
        } catch (SQLException ignored) {}

        if (isEdit) {
            attIdF.setText(existing.getAttId());
            attIdF.setEditable(false);
            attIdF.setBackground(new Color(0xEAEAEA));
            dateF.setText(existing.getAttenDate() != null ? existing.getAttenDate().toString() : "");
            typeBox.setSelectedItem(existing.getType());
            statusBox.setSelectedItem(existing.getStatus());
            // Pre-select student
            for (int i = 0; i < ugBox.getItemCount(); i++) {
                if (ugBox.getItemAt(i).startsWith(existing.getRegNo())) { ugBox.setSelectedIndex(i); break; }
            }
            // Pre-select session
            if (existing.getSessionId() != null) {
                for (int i = 0; i < sessionBox.getItemCount(); i++) {
                    if (sessionBox.getItemAt(i).startsWith(existing.getSessionId())) { sessionBox.setSelectedIndex(i); break; }
                }
            }
        }

        Object[][] rows = {
            {"Attendance ID",   attIdF},
            {"Student",         ugBox},
            {"Date (YYYY-MM-DD)", dateF},
            {"Type",            typeBox},
            {"Status",          statusBox},
            {"Session",         sessionBox},
        };

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Object[] row : rows) {
            JLabel lbl = new JLabel((String) row[0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            formPanel.add(lbl);
            formPanel.add(Box.createVerticalStrut(4));
            formPanel.add((Component) row[1]);
            formPanel.add(Box.createVerticalStrut(12));
        }

        JButton saveBtn = UIComponents.primaryButton(isEdit ? "💾 Update" : "💾 Save");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            try {
                String attId  = attIdF.getText().trim();
                String regNo  = ugBox.getSelectedItem().toString().split("\\|")[0].trim();
                String dateStr = dateF.getText().trim();
                String type   = typeBox.getSelectedItem().toString();
                String status = statusBox.getSelectedItem().toString();
                String sessId = sessionBox.getSelectedItem().toString().split("\\|")[0].trim();

                if (attId.isEmpty() || dateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg, "Attendance ID and Date are required."); return;
                }
                Date sqlDate = Date.valueOf(dateStr);
                TOAttendance a = new TOAttendance(attId, type, sqlDate, status, regNo, sessId);

                if (isEdit) {
                    attDAO.updateAttendance(a);
                    JOptionPane.showMessageDialog(dlg, "✅ Attendance updated!");
                } else {
                    if (attDAO.existsById(attId)) {
                        JOptionPane.showMessageDialog(dlg, "⚠ Attendance ID already exists."); return;
                    }
                    attDAO.addAttendance(a);
                    JOptionPane.showMessageDialog(dlg, "✅ Attendance added!");
                }
                loadAll();
                dlg.dispose();
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(dlg, "Invalid date format. Use YYYY-MM-DD.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(16));
        content.add(formPanel);
        content.add(saveBtn);
        dlg.setContentPane(new JScrollPane(content) {{
            setBorder(BorderFactory.createEmptyBorder());
        }});
        dlg.setVisible(true);
    }

    // ── BATCH SUMMARY DIALOG ─────────────────────────────────────────
    private void showBatchSummaryDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Attendance Summary", true);
        dlg.setSize(680, 500);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(AppTheme.BG_MAIN);

        // Filter controls
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setOpaque(false);
        JLabel info = new JLabel("Combined attendance % per student across all sessions:");
        info.setFont(AppTheme.FONT_BODY); info.setForeground(AppTheme.TEXT_SECONDARY);
        top.add(info);

        // Table
        String[] cols = {"Reg No", "Student Name", "Total", "Present", "Absent", "Att %", "Status"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = UIComponents.styledTable(cols);
        tbl.setModel(dm);

        // Colour last 2 columns
        for (int col : new int[]{5, 6}) {
            tbl.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int c) {
                    super.getTableCellRendererComponent(t, v, sel, foc, row, c);
                    if (!sel) {
                        String val = v == null ? "" : v.toString();
                        if (val.contains("✅"))       setForeground(AppTheme.SUCCESS);
                        else if (val.contains("⚠"))  setForeground(AppTheme.WARNING);
                        else if (val.contains("❌"))  setForeground(AppTheme.DANGER);
                        else setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                    }
                    setBorder(new EmptyBorder(0, 10, 0, 10));
                    return this;
                }
            });
        }

        try {
            Map<String, String> nameMap = new HashMap<>();
            ugDAO.getAll().forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            List<TOAttendance> all = attDAO.getAll();
            Map<String, int[]> summary = new LinkedHashMap<>(); // regNo -> [total, present]
            for (TOAttendance a : all) {
                summary.computeIfAbsent(a.getRegNo(), k -> new int[]{0, 0});
                summary.get(a.getRegNo())[0]++;
                if ("present".equals(a.getStatus())) summary.get(a.getRegNo())[1]++;
            }
            for (Map.Entry<String, int[]> e : summary.entrySet()) {
                int total   = e.getValue()[0];
                int present = e.getValue()[1];
                int absent  = total - present;
                double pct  = total > 0 ? (present * 100.0 / total) : 0;
                String statusStr = pct > 80 ? "Above 80%" : pct == 80 ? "Exactly 80%" : "Below 80%";
                dm.addRow(new Object[]{
                    e.getKey(), nameMap.getOrDefault(e.getKey(), "–"),
                    total, present, absent,
                    String.format("%.1f%%", pct), statusStr
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
        }

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.setOpaque(false);
        for (String[] l : new String[][]{
            {"Above 80%", String.valueOf(AppTheme.SUCCESS.getRGB())},
            {"Exactly 80%", String.valueOf(AppTheme.WARNING.getRGB())},
            {"Below 80%", String.valueOf(AppTheme.DANGER.getRGB())}
        }) {
            JLabel lb = new JLabel(l[0]);
            lb.setFont(AppTheme.FONT_SMALL);
            lb.setForeground(new Color(Integer.parseInt(l[1])));
            legend.add(lb);
        }

        content.add(top,                           BorderLayout.NORTH);
        content.add(UIComponents.scrolled(tbl),    BorderLayout.CENTER);
        content.add(legend,                        BorderLayout.SOUTH);
        dlg.setContentPane(content);
        dlg.setVisible(true);
    }
}
