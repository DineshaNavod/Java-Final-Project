package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LecMedicalPanel extends JPanel {

    private final LecMedicalDAO medDAO = new LecMedicalDAO();
    private final LecUndergraduateDAO ugDAO  = new LecUndergraduateDAO();

    private JTable  table;
    private DefaultTableModel model;

    public LecMedicalPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadAll();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🏥 Medical Records"), BorderLayout.WEST);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "approved", "not approved"});
        statusFilter.setFont(AppTheme.FONT_BODY);
        statusFilter.setPreferredSize(new Dimension(160, 36));
        JButton loadBtn = UIComponents.outlineButton("🔍 Filter");
        JButton refBtn  = UIComponents.outlineButton("↻ Refresh");
        loadBtn.addActionListener(e -> loadFiltered((String) statusFilter.getSelectedItem()));
        refBtn.addActionListener(e -> loadAll());

        filterRow.add(new JLabel("Status:")); filterRow.add(statusFilter);
        filterRow.add(loadBtn); filterRow.add(refBtn);

        String[] cols = {"Medical ID", "Reg No", "Student Name", "Submitted",
                         "Start Date", "End Date", "Description", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(cols);
        table.setModel(model);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);


        table.getColumnModel().getColumn(7).setCellRenderer(
            new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(
                        JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                    Component c = super.getTableCellRendererComponent(t,v,sel,foc,row,col);
                    if (!sel) {
                        String val = v == null ? "" : v.toString();
                        c.setForeground(val.equals("approved") ? AppTheme.SUCCESS : AppTheme.DANGER);
                        ((JLabel)c).setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                        c.setBackground(row%2==0?Color.WHITE:new Color(0xF7FBF9));
                    }
                    return c;
                }
            });

        // ── STUDENT VIEW BUTTON ──
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton viewStudBtn = UIComponents.outlineButton("👤 Student Medicals");
        viewStudBtn.addActionListener(e -> showStudentMedicals());
        btnRow.add(viewStudBtn);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow, BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void loadAll() { loadFiltered("All"); }

    private void loadFiltered(String status) {
        model.setRowCount(0);
        try {
            List<LecMedical> list = medDAO.getAll();
            Map<String, String> nameMap = new HashMap<>();
            ugDAO.getAll().forEach(u -> nameMap.put(u.getRegNo(), u.getFullName()));

            for (LecMedical m : list) {
                if (!status.equals("All") && !m.getStatus().equals(status)) continue;
                model.addRow(new Object[]{
                    m.getMedicalId(), m.getRegNo(),
                    nameMap.getOrDefault(m.getRegNo(), "–"),
                    m.getSubmissionDate(), m.getAffectedStartDate(), m.getAffectedEndDate(),
                    m.getDescription() != null
                        ? (m.getDescription().length() > 50
                           ? m.getDescription().substring(0, 50) + "…"
                           : m.getDescription()) : "–",
                    m.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void showStudentMedicals() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Student Medical Records", true);
        dlg.setSize(540, 460);
        dlg.setLocationRelativeTo(this);

        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.setBackground(AppTheme.BG_MAIN);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        top.setOpaque(false);
        JComboBox<String> ugBox = new JComboBox<>();
        ugBox.setFont(AppTheme.FONT_BODY);
        ugBox.setPreferredSize(new Dimension(260, 34));
        try {
            for (LecUndergraduate u : ugDAO.getAll())
                ugBox.addItem(u.getRegNo() + " | " + u.getFullName());
        } catch (SQLException ignored) {}

        JButton goBtn = UIComponents.primaryButton("Load");

        String[] cols = {"Medical ID", "Submitted", "Start", "End", "Description", "Status"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = UIComponents.styledTable(cols);
        tbl.setModel(dm);
        tbl.getColumnModel().getColumn(4).setPreferredWidth(200);

        goBtn.addActionListener(e -> {
            String regNo = ugBox.getSelectedItem().toString().split("\\|")[0].trim();
            dm.setRowCount(0);
            try {
                for (LecMedical m : medDAO.getByStudent(regNo)) {
                    String desc = m.getDescription() != null
                            ? (m.getDescription().length() > 50
                               ? m.getDescription().substring(0, 50) + "…"
                               : m.getDescription()) : "–";
                    dm.addRow(new Object[]{
                        m.getMedicalId(), m.getSubmissionDate(),
                        m.getAffectedStartDate(), m.getAffectedEndDate(),
                        desc, m.getStatus()
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });

        top.add(new JLabel("Student:")); top.add(ugBox); top.add(goBtn);
        content.add(top, BorderLayout.NORTH);
        content.add(UIComponents.scrolled(tbl), BorderLayout.CENTER);
        dlg.setContentPane(content);
        dlg.setVisible(true);
    }
}
