package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TOTimetablePanel extends JPanel {

    private final TOTimetableDAO ttDAO     = new TOTimetableDAO();
    private final CourseUnitDAO courseDAO = new CourseUnitDAO();

    private JTable            table;
    private DefaultTableModel model;
    private JComboBox<String> typeFilter;
    private JTextField        searchField;

    private static final String[] COLS = {
        "Session ID", "Course Code", "Course Name", "Date", "Type", "Duration", "Hall"
    };

    public TOTimetablePanel() {
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
        header.add(UIComponents.sectionTitle("🗓 Department Timetable"), BorderLayout.WEST);


        UIComponents.RoundedPanel badge = new UIComponents.RoundedPanel(20, new Color(0xFFF3CD));
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 6));
        badge.setPreferredSize(new Dimension(160, 36));
        JLabel badgeLbl = new JLabel("View Only");
        badgeLbl.setFont(new Font("Segoe UI Emoji", Font.BOLD, 12));
        badgeLbl.setForeground(new Color(0x856404));
        badge.add(badgeLbl);
        header.add(badge, BorderLayout.EAST);


        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        searchField = UIComponents.styledField("Search by code, date or hall…");
        searchField.setPreferredSize(new Dimension(220, 36));

        typeFilter = new JComboBox<>(new String[]{"All Types", "THEORY", "PRACTICAL"});
        typeFilter.setFont(AppTheme.FONT_BODY);
        typeFilter.setPreferredSize(new Dimension(140, 36));

        JButton searchBtn = UIComponents.outlineButton("🔍 Filter");
        JButton refBtn    = UIComponents.outlineButton("↻ Refresh");
        searchBtn.addActionListener(e -> load());
        refBtn.addActionListener(e -> load());
        typeFilter.addActionListener(e -> load());

        filterRow.add(searchField);
        filterRow.add(new JLabel("Type:")); filterRow.add(typeFilter);
        filterRow.add(searchBtn); filterRow.add(refBtn);


        model = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(COLS);
        table.setModel(model);
        int[] widths = {90, 100, 220, 100, 90, 90, 70};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);


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


        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 10));
        statsBar.setOpaque(false);
        statsBar.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel totalLbl   = new JLabel("Total Sessions: –");
        JLabel theoryLbl  = new JLabel("Theory: –");
        JLabel practLbl   = new JLabel("Practical: –");
        for (JLabel l : new JLabel[]{totalLbl, theoryLbl, practLbl}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(AppTheme.TEXT_SECONDARY);
            statsBar.add(l);
        }




        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                    BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(statsBar,                     BorderLayout.SOUTH);

        // Store refs for stats update
        this.totalLbl  = totalLbl;
        this.theoryLbl = theoryLbl;
        this.practLbl  = practLbl;

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }


    private JLabel totalLbl, theoryLbl, practLbl;

    private void load() {
        model.setRowCount(0);
        try {
            List<TOTimetable> sessions = ttDAO.getAllSessions();
            java.util.Map<String, String> nameMap = new java.util.HashMap<>();
            courseDAO.getAllCourses().forEach(c -> nameMap.put(c.getCCode(), c.getCName()));

            String typeFilter2 = typeFilter.getSelectedItem().toString();
            String search      = searchField.getText().trim().toLowerCase();

            int theory = 0, practical = 0;
            for (TOTimetable t : sessions) {
                if (!"All Types".equals(typeFilter2) && !typeFilter2.equals(t.getType())) continue;
                String cName = nameMap.getOrDefault(t.getCCode(), "–");
                boolean matchSearch = search.isEmpty()
                    || t.getSessionId().toLowerCase().contains(search)
                    || t.getCCode().toLowerCase().contains(search)
                    || cName.toLowerCase().contains(search)
                    || (t.getSessionDate() != null && t.getSessionDate().toString().contains(search))
                    || t.getLecHall().toLowerCase().contains(search);
                if (!matchSearch) continue;

                model.addRow(new Object[]{
                    t.getSessionId(), t.getCCode(), cName,
                    t.getSessionDate(), t.getType(), t.getDuration(), t.getLecHall()
                });
                if ("THEORY".equals(t.getType()))    theory++;
                else                                  practical++;
            }
            totalLbl.setText("Total Sessions: " + (theory + practical));
            theoryLbl.setText("Theory: " + theory);
            practLbl.setText("Practical: " + practical);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
