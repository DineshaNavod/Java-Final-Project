package gui;

import dao.Notice;
import dao.NoticeDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NoticeManagementPanel extends JPanel {

    private NoticeDAO noticeDAO = new NoticeDAO();
    private JTable table;
    private DefaultTableModel model;

    public NoticeManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadNotices();
    }

    private void build() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("📋 Notice Management"), BorderLayout.WEST);
        JButton addBtn = UIComponents.primaryButton("＋ Post Notice");
        addBtn.addActionListener(e -> showNoticeDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        String[] cols = {"Notice ID", "Notice Content"};
        table = UIComponents.styledTable(cols);
        model = (DefaultTableModel) table.getModel();
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(500);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));
        JButton editBtn = UIComponents.outlineButton("✏ Edit");
        JButton delBtn  = UIComponents.dangerButton("🗑 Delete");
        JButton refBtn  = UIComponents.outlineButton("↻ Refresh");
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        refBtn.addActionListener(e -> loadNotices());
        btnRow.add(refBtn); btnRow.add(editBtn); btnRow.add(delBtn);

        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(UIComponents.scrolled(table), BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    public void loadNotices() {
        model.setRowCount(0);
        try {
            List<Notice> notices = noticeDAO.getAllNotices();
            for (Notice n : notices) {
                model.addRow(new Object[]{n.getNoticeId(), n.getNotice()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a notice first."); return; }
        int id = (int) model.getValueAt(row, 0);
        String content = (String) model.getValueAt(row, 1);
        showNoticeDialog(new Notice(id, content));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a notice first."); return; }
        int id = (int) model.getValueAt(row, 0);
        int c = JOptionPane.showConfirmDialog(this, "Delete this notice?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            try {
                noticeDAO.deleteNotice(id);
                loadNotices();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showNoticeDialog(Notice existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Post Notice" : "Edit Notice", true);
        dialog.setSize(460, 280);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel(existing == null ? "Post New Notice" : "Edit Notice");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField idField = UIComponents.styledField("Notice ID (number)");
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JTextArea noticeArea = new JTextArea(4, 30);
        noticeArea.setFont(AppTheme.FONT_BODY);
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);
        noticeArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1),
                new EmptyBorder(8, 10, 8, 10)));
        JScrollPane areaSP = new JScrollPane(noticeArea);
        areaSP.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaSP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        if (existing != null) {
            idField.setText(String.valueOf(existing.getNoticeId()));
            idField.setEditable(false);
            noticeArea.setText(existing.getNotice());
        }

        JButton saveBtn = UIComponents.primaryButton("💾 Save");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        saveBtn.addActionListener(e -> {
            String idStr = idField.getText().trim();
            String noticeText = noticeArea.getText().trim();
            if (idStr.isEmpty() || noticeText.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "ID and notice content are required."); return;
            }
            try {
                int id = Integer.parseInt(idStr);
                if (existing == null) {
                    noticeDAO.addNotice(new Notice(id, noticeText));
                } else {
                    noticeDAO.updateNotice(new Notice(existing.getNoticeId(), noticeText));
                }
                loadNotices();
                JOptionPane.showMessageDialog(dialog, "Notice saved!");
                dialog.dispose();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Notice ID must be a number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        JLabel idLbl = new JLabel("Notice ID"); idLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idLbl.setForeground(AppTheme.TEXT_SECONDARY); idLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel noticeLbl = new JLabel("Notice Content"); noticeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        noticeLbl.setForeground(AppTheme.TEXT_SECONDARY); noticeLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(title);
        content.add(Box.createVerticalStrut(18));
        content.add(idLbl);
        content.add(Box.createVerticalStrut(4));
        content.add(idField);
        content.add(Box.createVerticalStrut(12));
        content.add(noticeLbl);
        content.add(Box.createVerticalStrut(4));
        content.add(areaSP);
        content.add(Box.createVerticalStrut(16));
        content.add(saveBtn);

        dialog.setContentPane(content);
        dialog.setVisible(true);
    }
}
