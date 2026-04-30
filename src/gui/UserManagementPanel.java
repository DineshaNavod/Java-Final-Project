package gui;

import dao.User;
import dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> roleFilter;
    private JTextField searchField;

    public UserManagementPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(30, 30, 30, 30));
        build();
        loadUsers();
    }

    private void build() {

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));
        header.add(UIComponents.sectionTitle("👤 User Management"), BorderLayout.WEST);


        JButton addBtn = UIComponents.primaryButton("＋ Add User");
        addBtn.addActionListener(e -> showUserDialog(null));
        header.add(addBtn, BorderLayout.EAST);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.setBorder(new EmptyBorder(0, 0, 14, 0));

        searchField = UIComponents.styledField("Search by name or username...");
        searchField.setForeground(Color.GRAY);
        searchField.setPreferredSize(new Dimension(240, 36));

        roleFilter = new JComboBox<>(new String[]{"All Roles", "Admin", "Lecturer", "Technical Officer", "Undergraduate"});
        roleFilter.setFont(AppTheme.FONT_BODY);
        roleFilter.setBackground(Color.WHITE);
        roleFilter.setPreferredSize(new Dimension(170, 36));

        JButton searchBtn = UIComponents.outlineButton("🔍 Search");
        searchBtn.addActionListener(e -> loadUsers());
        roleFilter.addActionListener(e -> loadUsers());

        filterRow.add(searchField);
        filterRow.add(roleFilter);
        filterRow.add(searchBtn);


        String[] cols = {"ID", "Full Name", "Username", "Role", "Email", "Phone"};
        table = UIComponents.styledTable(cols);
        model = (DefaultTableModel) table.getModel();


        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        JScrollPane scroll = UIComponents.scrolled(table);


        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.setBorder(new EmptyBorder(12, 0, 0, 0));

        JButton editBtn = UIComponents.outlineButton("✏ Edit");
        JButton delBtn  = UIComponents.dangerButton("🗑 Delete");
        JButton refBtn  = UIComponents.outlineButton("↻ Refresh");

        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        refBtn.addActionListener(e -> loadUsers());

        btnRow.add(refBtn);
        btnRow.add(editBtn);
        btnRow.add(delBtn);


        UIComponents.RoundedPanel card = new UIComponents.RoundedPanel(AppTheme.CORNER_RADIUS, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));
        card.add(filterRow, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        add(header, BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
    }

    public void loadUsers() {
        model.setRowCount(0);
        try {
            List<User> users;
            int role = roleFilter.getSelectedIndex(); // 0=all,1=admin,2=lec,3=tech,4=ug
            if (role == 0) {
                users = userDAO.getAllUsers();
            } else {
                users = userDAO.getUsersByRole(role);
            }
            String search = searchField.getText().trim().toLowerCase();
            for (User u : users) {
                if (!search.isEmpty() &&
                    !u.getFullName().toLowerCase().contains(search) &&
                    !u.getUsername().toLowerCase().contains(search)) continue;
                model.addRow(new Object[]{
                    u.getUserId(), u.getFullName(), u.getUsername(),
                    u.getRoleName(), u.getEmail(), u.getPhone()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading users:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a user."); return; }
        int userId = (int) model.getValueAt(row, 0);
        try {
            User u = userDAO.getUserById(userId);
            if (u != null) showUserDialog(u);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a user."); return; }
        int userId = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete user: " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userDAO.deleteUser(userId);
                loadUsers();
                JOptionPane.showMessageDialog(this, "User deleted successfully.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showUserDialog(User existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Add User" : "Edit User", true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 28, 24, 28));
        content.setBackground(AppTheme.BG_MAIN);

        JLabel title = new JLabel(existing == null ? "Add New User" : "Edit User");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField fullNameF = UIComponents.styledField("Full Name");
        JTextField usernameF = UIComponents.styledField("Username");
        JPasswordField passF = new JPasswordField();
        passF.setFont(AppTheme.FONT_BODY);
        passF.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        JTextField emailF   = UIComponents.styledField("Email");
        JTextField phoneF   = UIComponents.styledField("Phone");
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Lecturer", "Technical Officer", "Undergraduate"});
        roleBox.setFont(AppTheme.FONT_BODY);
        roleBox.setBackground(Color.WHITE);

        if (existing != null) {
            fullNameF.setText(existing.getFullName());
            usernameF.setText(existing.getUsername());
            passF.setText(existing.getPassword());
            emailF.setText(existing.getEmail());
            phoneF.setText(existing.getPhone());
            roleBox.setSelectedIndex(existing.getRoleId() - 1);
        }

        Component[][] rows = {
            {makeLabel("Full Name"), fullNameF},
            {makeLabel("Username"),  usernameF},
            {makeLabel("Password"),  passF},
            {makeLabel("Email"),     emailF},
            {makeLabel("Phone"),     phoneF},
            {makeLabel("Role"),      roleBox}
        };

        for (Component[] row : rows) {
            row[0].setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
            ((JComponent) row[0]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setAlignmentX(Component.LEFT_ALIGNMENT);
            ((JComponent) row[1]).setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            content.add(row[0]);
            content.add(Box.createVerticalStrut(4));
            content.add(row[1]);
            content.add(Box.createVerticalStrut(12));
        }

        JButton saveBtn = UIComponents.primaryButton("💾 Save");
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        saveBtn.addActionListener(e -> {
            String fn = fullNameF.getText().trim();
            String un = usernameF.getText().trim();
            String pw = new String(passF.getPassword()).trim();
            String em = emailF.getText().trim();
            String ph = phoneF.getText().trim();
            int roleId = roleBox.getSelectedIndex() + 1;

            if (fn.isEmpty() || un.isEmpty() || pw.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Full name, username and password are required.");
                return;
            }
            try {
                if (existing == null) {
                    User u = new User(0, un, pw, roleId, fn, em, ph, null);
                    userDAO.addUser(u);
                    JOptionPane.showMessageDialog(dialog, "User added successfully!");
                } else {
                    existing.setFullName(fn);
                    existing.setUsername(un);
                    existing.setPassword(pw);
                    existing.setEmail(em);
                    existing.setPhone(ph);
                    existing.setRoleId(roleId);
                    userDAO.updateUser(existing);
                    JOptionPane.showMessageDialog(dialog, "User updated successfully!");
                }
                loadUsers();
                dialog.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        content.add(title);
        content.add(Box.createVerticalStrut(16));
        content.add(saveBtn);

        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        dialog.setContentPane(sp);
        dialog.setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(AppTheme.TEXT_SECONDARY);
        return lbl;
    }
}
