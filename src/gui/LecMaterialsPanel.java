package gui;

import dao.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.awt.datatransfer.DataFlavor;
import javax.swing.TransferHandler;

public class LecMaterialsPanel extends JPanel {

    private final LecCourseMaterialDAO matDAO  = new LecCourseMaterialDAO();
    private final CourseUnitDAO     courseDAO = new CourseUnitDAO();

    private JComboBox<String>  courseBox;
    private JTable             table;
    private DefaultTableModel  model;

    public LecMaterialsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("Course Materials"), BorderLayout.WEST);
        JButton addBtn = UIComponents.primaryButton("Add Material");
        addBtn.addActionListener(e -> showAddDialog());
        header.add(addBtn, BorderLayout.EAST);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 12, 0));

        courseBox = new JComboBox<>();
        courseBox.setFont(AppTheme.FONT_BODY);
        courseBox.setPreferredSize(new Dimension(280, 36));
        courseBox.addItem("-- Select Course --");
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                courseBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        JButton loadBtn = UIComponents.outlineButton("🔍 Load");
        loadBtn.addActionListener(e -> loadMaterials());
        filterRow.add(new JLabel("Course:")); filterRow.add(courseBox); filterRow.add(loadBtn);

        String[] cols = {"Material ID", "Course", "Title", "Link / Path"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = UIComponents.styledTable(cols);
        table.setModel(model);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(260);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(10, 0, 0, 0));
        JButton delBtn = UIComponents.dangerButton("🗑 Delete");
        JButton refBtn = UIComponents.outlineButton("↻ Refresh");
        delBtn.addActionListener(e -> deleteSelected());
        refBtn.addActionListener(e -> loadMaterials());
        btnRow.add(refBtn); btnRow.add(delBtn);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow,                        BorderLayout.NORTH);
        card.add(UIComponents.scrolled(table),     BorderLayout.CENTER);
        card.add(btnRow,                           BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card,   BorderLayout.CENTER);
    }

    private void loadMaterials() {
        int idx = courseBox.getSelectedIndex();
        if (idx <= 0) { JOptionPane.showMessageDialog(this, "Select a course."); return; }
        String cCode = courseBox.getSelectedItem().toString().split("\\|")[0].trim();
        model.setRowCount(0);
        try {
            for (LecCourseMaterial m : matDAO.getByCourse(cCode))
                model.addRow(new Object[]{m.getMatId(), m.getCCode(), m.getTitle(), m.getLink()});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a row."); return; }
        String matId = model.getValueAt(row, 0).toString();
        int c = JOptionPane.showConfirmDialog(this, "Delete material: " + model.getValueAt(row, 2) + "?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try {
                matDAO.deleteMaterial(matId);
                loadMaterials();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showAddDialog() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Material", true);
        dlg.setSize(420, 320);
        dlg.setLocationRelativeTo(this);

        JLabel title = new JLabel("Add Course Material");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField matIdF = UIComponents.styledField("e.g. M001");
        JComboBox<String> cBox = new JComboBox<>();
        cBox.setFont(AppTheme.FONT_BODY);
        cBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        cBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        try {
            for (CourseUnit cu : courseDAO.getAllCourses())
                cBox.addItem(cu.getCCode() + " | " + cu.getCName());
        } catch (SQLException ignored) {}

        JTextField titleF = UIComponents.styledField("Material title");

        //drag and drop pdf
        JTextField linkF = UIComponents.styledField("Select or drag PDF...");
        linkF.setEditable(false);

        linkF.setTransferHandler(new TransferHandler() {

            @Override
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport support) {
                try {
                    java.util.List<java.io.File> files =
                            (java.util.List<java.io.File>) support.getTransferable()
                                    .getTransferData(DataFlavor.javaFileListFlavor);

                    if (!files.isEmpty()) {
                        java.io.File file = files.get(0);

                        if (!file.getName().toLowerCase().endsWith(".pdf")) {
                            JOptionPane.showMessageDialog(null, "Only PDF allowed!");
                            return false;
                        }

                        String destPath = "materials/" + file.getName();

                        java.nio.file.Files.copy(
                                file.toPath(),
                                new java.io.File(destPath).toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                        );

                        linkF.setText(destPath);
                        return true;
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
                return false;
            }
        });

        JButton browseBtn = UIComponents.outlineButton("Browse");

        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

            int result = chooser.showOpenDialog(dlg);

            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File file = chooser.getSelectedFile();

                if (!file.getName().toLowerCase().endsWith(".pdf")) {
                    JOptionPane.showMessageDialog(dlg, "Only PDF allowed!");
                    return;
                }

                String destPath = "materials/" + file.getName();

                try {
                    java.nio.file.Files.copy(
                            file.toPath(),
                            new java.io.File(destPath).toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );

                    linkF.setText(destPath);

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
                }
            }
        });

        Object[][] rows = {
                {"Material ID", matIdF},
                {"Course",      cBox},
                {"Title",       titleF},
                {"Link / Path", linkF},
                {"Browse File", browseBtn}
        };


        JButton saveBtn = UIComponents.primaryButton("💾 Save");
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            String mid = matIdF.getText().trim();
            String cCode = cBox.getSelectedItem().toString().split("\\|")[0].trim();
            String t  = titleF.getText().trim();
            String lnk = linkF.getText().trim();
            if (mid.isEmpty() || t.isEmpty() || lnk.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "All fields required."); return;
            }
            try {
                matDAO.addMaterial(new LecCourseMaterial(mid, cCode, t, lnk));
                JOptionPane.showMessageDialog(dlg, "Material added!");
                dlg.dispose();
                loadMaterials();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex.getMessage());
            }
        });



        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
        p2.setBackground(AppTheme.BG_MAIN);
        p2.setBorder(new EmptyBorder(24, 28, 24, 28));
        p2.add(title);
        p2.add(Box.createVerticalStrut(18));
        for (Object[] row : rows) {
            JLabel lbl = new JLabel((String)row[0]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(AppTheme.TEXT_SECONDARY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent)row[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent)row[1]).setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            p2.add(lbl);
            p2.add(Box.createVerticalStrut(4));
            p2.add((Component)row[1]);
            p2.add(Box.createVerticalStrut(10));
        }
        p2.add(Box.createVerticalStrut(4));
        p2.add(saveBtn);

        dlg.setContentPane(new JScrollPane(p2));
        dlg.setVisible(true);
    }
}
