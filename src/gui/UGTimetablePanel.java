package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UGTimetablePanel extends JPanel {

    private final TimetableDAO  ttDAO     = new TimetableDAO();
    private final CourseUnitDAO courseDAO = new CourseUnitDAO();

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> typeFilter;
    private JTextField        searchField;
    private JLabel            totalLbl, theoryLbl, practLbl;

    public UGTimetablePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        load();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🗓 My Timetable"), BorderLayout.WEST);

        // Filter row
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        searchField = UIComponents.styledField("Search by code, date or hall…");
        searchField.setPreferredSize(new Dimension(220, 36));

        typeFilter = new JComboBox<>(new String[]{"All Types", "THEORY", "PRACTICAL"});
        typeFilter.setFont(AppTheme.FONT_BODY);
        typeFilter.setPreferredSize(new Dimension(140, 36));

        JButton filterBtn = UIComponents.outlineButton("🔍 Filter");
        JButton refBtn    = UIComponents.outlineButton("↻ Refresh");
        filterBtn.addActionListener(e -> load());
        refBtn.addActionListener(e -> load());
        typeFilter.addActionListener(e -> load());

        filterRow.add(searchField);
        filterRow.add(new JLabel("Type:")); filterRow.add(typeFilter);
        filterRow.add(filterBtn); filterRow.add(refBtn);

        // Table
        String[] cols = {"Session ID", "Course Code", "Course Name", "Date", "Type", "Duration", "Hall"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(cols);
        table.setModel(model);
        int[] widths = {90, 100, 220, 105, 90, 90, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Type column colour
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    String val = v == null ? "" : v.toString();
                    setForeground("THEORY".equals(val) ? AppTheme.INFO : AppTheme.PRIMARY_LIGHT);
                    setFont(AppTheme.FONT_BODY.deriveFont(Font.BOLD));
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF7FBF9));
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });

        // Stats bar
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        statsBar.setOpaque(false);
        statsBar.setBorder(new EmptyBorder(8, 0, 0, 0));
        totalLbl  = statLbl("Total Sessions: –");
        theoryLbl = statLbl("Theory: –");
        practLbl  = statLbl("Practical: –");
        statsBar.add(totalLbl); statsBar.add(theoryLbl); statsBar.add(practLbl);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                    BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(statsBar,                     BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private JLabel statLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(AppTheme.TEXT_SECONDARY);
        return l;
    }

    private void load() {
        model.setRowCount(0);
        try {
            List<Timetable> sessions = ttDAO.getAllSessions();
            java.util.Map<String, String> nameMap = new java.util.HashMap<>();
            courseDAO.getAllCourses().forEach(c -> nameMap.put(c.getCCode(), c.getCName()));

            String typeF  = typeFilter.getSelectedItem().toString();
            String search = searchField.getText().trim().toLowerCase();

            int theory = 0, practical = 0;
            for (Timetable t : sessions) {
                if (!"All Types".equals(typeF) && !typeF.equals(t.getType())) continue;
                String cName = nameMap.getOrDefault(t.getCCode(), "–");
                boolean match = search.isEmpty()
                    || t.getSessionId().toLowerCase().contains(search)
                    || (t.getCCode() != null && t.getCCode().toLowerCase().contains(search))
                    || cName.toLowerCase().contains(search)
                    || (t.getSessionDate() != null && t.getSessionDate().toString().contains(search))
                    || t.getLecHall().toLowerCase().contains(search);
                if (!match) continue;

                model.addRow(new Object[]{
                    t.getSessionId(), t.getCCode(), cName,
                    t.getSessionDate(), t.getType(), t.getDuration(), t.getLecHall()
                });
                if ("THEORY".equals(t.getType())) theory++; else practical++;
            }
            totalLbl.setText("Total Sessions: " + (theory + practical));
            theoryLbl.setText("Theory: " + theory);
            practLbl.setText("Practical: " + practical);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
