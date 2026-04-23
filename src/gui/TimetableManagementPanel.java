package gui;

import dao.Timetable;
import dao.TimetableDAO;
import dao.CourseUnit;
import dao.CourseUnitDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class TimetableManagementPanel extends JPanel {

    private TimetableDAO timetableDAO = new TimetableDAO();
    private CourseUnitDAO courseDAO   = new CourseUnitDAO();
    private JTable table;
    private DefaultTableModel model;

    public TimetableManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadSessions();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("🗓 Timetable Management"), BorderLayout.WEST);
        JButton addBtn = UIComponents.primaryButton("＋ Add Session");
        addBtn.addActionListener(e -> showSessionDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        String[] cols = {"Session ID", "Course Code", "Date", "Type", "Duration", "Hall"};
        table = UIComponents.styledTable(cols);
        model = (DefaultTableModel) table.getModel();
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton editBtn = UIComponents.outlineButton("✏ Edit");
        JButton delBtn  = UIComponents.dangerButton("🗑 Delete");
        JButton refBtn  = UIComponents.outlineButton("↻ Refresh");
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        refBtn.addActionListener(e -> loadSessions());
        btnRow.add(refBtn); btnRow.add(editBtn); btnRow.add(delBtn);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    public void loadSessions() {
        model.setRowCount(0);
        try {
            List<Timetable> sessions = timetableDAO.getAllSessions();
            for (Timetable t : sessions) {
                model.addRow(new Object[]{
                    t.getSessionId(), t.getCCode(), t.getSessionDate(),
                    t.getType(), t.getDuration(), t.getLecHall()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a session first."); return; }
        // Build Timetable from row
        Timetable t = new Timetable(
            (String) model.getValueAt(row, 0),
            (String) model.getValueAt(row, 1),
            (Date)   model.getValueAt(row, 2),
            (String) model.getValueAt(row, 3),
            (Time)   model.getValueAt(row, 4),
            (String) model.getValueAt(row, 5)
        );
        showSessionDialog(t);
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a session first."); return; }
        String id = (String) model.getValueAt(row, 0);
        int c = JOptionPane.showConfirmDialog(this, "Delete session: " + id + "?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try {
                timetableDAO.deleteSession(id);
                loadSessions();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showSessionDialog(Timetable existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Session" : "Edit Session", true);
        dialog.setSize(420, 430);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JTextField sessionIdF = UIComponents.styledField("e.g. S001");
        JTextField cCodeF     = UIComponents.styledField("Course Code");
        JTextField dateF      = UIComponents.styledField("YYYY-MM-DD");
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"THEORY", "PRACTICAL"});
        typeBox.setFont(AppTheme.FONT_BODY);
        JTextField durationF  = UIComponents.styledField("HH:MM:SS e.g. 02:00:00");
        JTextField hallF      = UIComponents.styledField("Hall e.g. LH1");

        if (existing != null) {
            sessionIdF.setText(existing.getSessionId());
            sessionIdF.setEditable(false);
            cCodeF.setText(existing.getCCode());
            dateF.setText(existing.getSessionDate().toString());
            typeBox.setSelectedItem(existing.getType());
            durationF.setText(existing.getDuration().toString());
            hallF.setText(existing.getLecHall());
        }

        Object[][] fields = {
            {"Session ID", sessionIdF},
            {"Course Code", cCodeF},
            {"Date (YYYY-MM-DD)", dateF},
            {"Type", typeBox},
            {"Duration (HH:MM:SS)", durationF},
            {"Lecture Hall", hallF}
        };

        for (Object[] f : fields) {
            JLabel lbl = new JLabel((String) f[0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) f[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) f[1]).setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            content.add(lbl);
            content.add(Box.createVerticalStrut(4));
            content.add((Component) f[1]);
            content.add(Box.createVerticalStrut(12));
        }

        JButton saveBtn = UIComponents.primaryButton("💾 Save");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.addActionListener(e -> {
            try {
                String sid      = sessionIdF.getText().trim();
                String code     = cCodeF.getText().trim();
                String dateStr  = dateF.getText().trim();
                String type     = (String) typeBox.getSelectedItem();
                String durStr   = durationF.getText().trim();
                String hall     = hallF.getText().trim();

                if (sid.isEmpty() || code.isEmpty() || dateStr.isEmpty() || durStr.isEmpty() || hall.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields required."); return;
                }
                Date   date = Date.valueOf(dateStr);
                Time   dur  = Time.valueOf(durStr);
                Timetable t = new Timetable(sid, code, date, type, dur, hall);

                if (existing == null) {
                    timetableDAO.addSession(t);
                } else {
                    timetableDAO.updateSession(t);
                }
                loadSessions();
                JOptionPane.showMessageDialog(dialog, "Session saved!");
                dialog.dispose();
            } catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(dialog, "Invalid date or time format.\nDate: YYYY-MM-DD, Time: HH:MM:SS");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        content.add(saveBtn);
        dialog.setContentPane(new JScrollPane(content));
        dialog.setVisible(true);
    }
}
