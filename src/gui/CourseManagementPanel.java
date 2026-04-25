package gui;

import dao.CourseUnit;
import dao.CourseUnitDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CourseManagementPanel extends JPanel {

    private CourseUnitDAO courseDAO = new CourseUnitDAO();
    private JTable table;
    private DefaultTableModel model;

    public CourseManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadCourses();
    }

    private void build() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        header.add(UIComponents.sectionTitle("📚 Course Management"), BorderLayout.WEST);

        JButton addBtn = UIComponents.primaryButton("＋ Add Course");
        addBtn.addActionListener(e -> showCourseDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        // Table
        String[] cols = {"Code", "Course Name", "Credits", "Theory", "Practical"};
        table = UIComponents.styledTable(cols);
        model = (DefaultTableModel) table.getModel();

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton editBtn = UIComponents.outlineButton("✏ Edit");
        JButton delBtn = UIComponents.dangerButton("🗑 Delete");
        JButton refBtn = UIComponents.outlineButton("↻ Refresh");

        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        refBtn.addActionListener(e -> loadCourses());

        btnRow.add(refBtn);
        btnRow.add(editBtn);
        btnRow.add(delBtn);

        UIComponents.RoundedPanel card =
                new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    public void loadCourses() {
        model.setRowCount(0);
        try {
            List<CourseUnit> courses = courseDAO.getAllCourses();

            for (CourseUnit c : courses) {
                model.addRow(new Object[]{
                        c.getCCode(),
                        c.getCName(),
                        c.getCredit(),
                        c.getIsTheory(),
                        c.getIsPractical()
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        String code = (String) model.getValueAt(row, 0);

        try {
            CourseUnit c = courseDAO.getCourseByCode(code);

            if (c != null) {
                showCourseDialog(c);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course first.");
            return;
        }

        String code = (String) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete course: " + name + "?",
                "Confirm",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseDAO.deleteCourse(code);
                loadCourses();

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showCourseDialog(CourseUnit existing) {
        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add Course" : "Edit Course",
                true
        );

        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JTextField codeF = UIComponents.styledField("e.g. ICT2132");
        JTextField nameF = UIComponents.styledField("Course Name");
        JTextField creditF = UIComponents.styledField("Credits");

        JComboBox<String> theoryBox =
                new JComboBox<>(new String[]{"YES", "NO"});

        JComboBox<String> practBox =
                new JComboBox<>(new String[]{"YES", "NO"});

        theoryBox.setFont(AppTheme.FONT_BODY);
        practBox.setFont(AppTheme.FONT_BODY);

        // Load existing data
        if (existing != null) {
            codeF.setText(existing.getCCode());
            nameF.setText(existing.getCName());
            creditF.setText(String.valueOf(existing.getCredit()));
            theoryBox.setSelectedItem(existing.getIsTheory());
            practBox.setSelectedItem(existing.getIsPractical());
        }

        Object[][] rows = {
                {"Course Code", codeF},
                {"Course Name", nameF},
                {"Credits", creditF},
                {"Has Theory", theoryBox},
                {"Has Practical", practBox}
        };

        for (Object[] row : rows) {
            JLabel lbl = new JLabel((String) row[0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            ((JComponent) row[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setMaximumSize(
                    new Dimension(Integer.MAX_VALUE, 38)
            );

            content.add(lbl);
            content.add(Box.createVerticalStrut(4));
            content.add((Component) row[1]);
            content.add(Box.createVerticalStrut(12));
        }

        JButton saveBtn = UIComponents.primaryButton("💾 Save");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        saveBtn.addActionListener(e -> {
            String code = codeF.getText().trim();
            String name = nameF.getText().trim();
            String creditStr = creditF.getText().trim();

            if (code.isEmpty() || name.isEmpty() || creditStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "All fields are required.");
                return;
            }

            try {
                int credit = Integer.parseInt(creditStr);

                String th = (String) theoryBox.getSelectedItem();
                String pr = (String) practBox.getSelectedItem();

                if (existing == null) {
                    // Add new course
                    courseDAO.addCourse(
                            new CourseUnit(code, name, credit, th, pr)
                    );

                } else {
                    // Save old course code
                    String oldCode = existing.getCCode();

                    // Update new values
                    existing.setCCode(code);
                    existing.setCName(name);
                    existing.setCredit(credit);
                    existing.setIsTheory(th);
                    existing.setIsPractical(pr);

                    // Update database
                    courseDAO.updateCourse(existing, oldCode);
                }

                loadCourses();
                JOptionPane.showMessageDialog(dialog,
                        "Course saved successfully!");
                dialog.dispose();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog,
                        "Credits must be a number.");

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage());
            }
        });

        content.add(saveBtn);

        dialog.setContentPane(new JScrollPane(content));
        dialog.setVisible(true);
    }
}