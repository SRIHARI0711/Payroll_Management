package gui;

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import models.Department;
import models.Employee;
import models.User;
import utils.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel for managing employees
 */
public class EmployeeManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private User currentUser;
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    
    // Components
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JLabel totalEmployeesLabel;
    
    // Table columns
    private final String[] columnNames = {
        "ID", "Employee Code", "First Name", "Last Name", "Email", 
        "Phone", "Department", "Position", "Base Salary", "Status", "Hire Date"
    };
    
    public EmployeeManagementPanel(User user) {
        this.currentUser = user;
        this.employeeDAO = new EmployeeDAO();
        this.departmentDAO = new DepartmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyUserPermissions();
        loadEmployeeData();
    }
    
    private void initializeComponents() {
        // Create table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.setRowHeight(25);
        employeeTable.getTableHeader().setReorderingAllowed(false);
        
        // Setup table sorter
        tableSorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(tableSorter);
        
        // Hide ID column
        employeeTable.getColumnModel().getColumn(0).setMinWidth(0);
        employeeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        employeeTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Employee Code
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(100); // First Name
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Last Name
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Email
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Phone
        employeeTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Department
        employeeTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Position
        employeeTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Base Salary
        employeeTable.getColumnModel().getColumn(9).setPreferredWidth(80);  // Status
        employeeTable.getColumnModel().getColumn(10).setPreferredWidth(100); // Hire Date
        
        // Create components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search employees by code, name, email, position, or department");
        
        addButton = new JButton("Add Employee");
        editButton = new JButton("Edit Employee");
        deleteButton = new JButton("Delete Employee");
        refreshButton = new JButton("Refresh");
        
        totalEmployeesLabel = new JLabel("Total Employees: 0");
        
        // Set button properties
        addButton.setPreferredSize(new Dimension(120, 30));
        editButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.setPreferredSize(new Dimension(120, 30));
        refreshButton.setPreferredSize(new Dimension(100, 30));
        
        // Initially disable edit and delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Bottom panel with statistics
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel leftBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftBottomPanel.add(totalEmployeesLabel);
        
        JPanel rightBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (currentUser != null) {
            JLabel userInfoLabel = new JLabel("Managed by: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
            userInfoLabel.setFont(userInfoLabel.getFont().deriveFont(Font.ITALIC, 10f));
            userInfoLabel.setForeground(Color.GRAY);
            rightBottomPanel.add(userInfoLabel);
        }
        
        bottomPanel.add(leftBottomPanel, BorderLayout.WEST);
        bottomPanel.add(rightBottomPanel, BorderLayout.EAST);
        
        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
        
        // Table selection listener
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = employeeTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
            }
        });
        
        // Button listeners
        addButton.addActionListener(e -> showEmployeeDialog(null));
        editButton.addActionListener(e -> editSelectedEmployee());
        deleteButton.addActionListener(e -> deleteSelectedEmployee());
        refreshButton.addActionListener(e -> refreshData());
        
        // Double-click to edit
        employeeTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && employeeTable.getSelectedRow() != -1) {
                    editSelectedEmployee();
                }
            }
        });
    }
    
    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
        updateEmployeeCount();
    }
    
    private void loadEmployeeData() {
        SwingWorker<List<Employee>, Void> worker = new SwingWorker<List<Employee>, Void>() {
            @Override
            protected List<Employee> doInBackground() throws Exception {
                return employeeDAO.getAllEmployees();
            }
            
            @Override
            protected void done() {
                try {
                    List<Employee> employees = get();
                    updateTable(employees);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                        "Error loading employee data: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Employee> employees) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Employee employee : employees) {
            Object[] row = {
                employee.getEmployeeId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getDepartmentName(),
                employee.getPosition(),
                String.format("$%.2f", employee.getBaseSalary()),
                employee.getEmploymentStatus(),
                employee.getHireDate() != null ? dateFormat.format(employee.getHireDate()) : ""
            };
            tableModel.addRow(row);
        }
        
        updateEmployeeCount();
    }
    
    private void updateEmployeeCount() {
        int totalRows = tableModel.getRowCount();
        int visibleRows = employeeTable.getRowCount();
        
        if (visibleRows == totalRows) {
            totalEmployeesLabel.setText("Total Employees: " + totalRows);
        } else {
            totalEmployeesLabel.setText("Showing " + visibleRows + " of " + totalRows + " employees");
        }
    }
    
    private void showEmployeeDialog(Employee employee) {
        EmployeeDialog dialog = new EmployeeDialog((Frame) SwingUtilities.getWindowAncestor(this), employee, currentUser);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void editSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        int employeeId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Load employee details
        SwingWorker<Employee, Void> worker = new SwingWorker<Employee, Void>() {
            @Override
            protected Employee doInBackground() throws Exception {
                return employeeDAO.getEmployeeById(employeeId);
            }
            
            @Override
            protected void done() {
                try {
                    Employee employee = get();
                    if (employee != null) {
                        showEmployeeDialog(employee);
                    } else {
                        JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                            "Employee not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                        "Error loading employee: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void deleteSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = employeeTable.convertRowIndexToModel(selectedRow);
        String employeeCode = (String) tableModel.getValueAt(modelRow, 1);
        String employeeName = tableModel.getValueAt(modelRow, 2) + " " + tableModel.getValueAt(modelRow, 3);
        
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete employee " + employeeCode + " (" + employeeName + ")?\n" +
            "This will set the employee status to TERMINATED.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            int employeeId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return employeeDAO.deleteEmployee(employeeId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                                "Employee deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                                "Failed to delete employee.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(EmployeeManagementPanel.this,
                            "Error deleting employee: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    public void refreshData() {
        loadEmployeeData();
    }
    
    private void applyUserPermissions() {
        // Apply permissions based on current user role
        if (currentUser != null) {
            // Both ADMIN and HR can view and manage employees
            // HR users have full access to employee management
            // ADMIN users have full access to everything
            
            // Currently both roles have the same permissions for employee management
            // This can be extended in the future if different restrictions are needed
            
            // Example: If we wanted to restrict HR from deleting employees:
            // if (currentUser.getRole() == User.UserRole.HR) {
            //     deleteButton.setEnabled(false);
            //     deleteButton.setToolTipText("Only administrators can delete employees");
            // }
        }
    }
    
    // Inner class for Employee Dialog
    private class EmployeeDialog extends JDialog {
        private Employee employee;
        private User dialogCurrentUser;
        private boolean confirmed = false;
        
        // Form components
        private JTextField employeeCodeField;
        private JTextField firstNameField;
        private JTextField lastNameField;
        private JTextField emailField;
        private JTextField phoneField;
        private JTextArea addressArea;
        private JSpinner dateOfBirthSpinner;
        private JSpinner hireDateSpinner;
        private JComboBox<Department> departmentCombo;
        private JTextField positionField;
        private JTextField baseSalaryField;
        private JComboBox<Employee.EmploymentStatus> statusCombo;
        
        public EmployeeDialog(Frame parent, Employee employee, User currentUser) {
            super(parent, employee == null ? "Add Employee" : "Edit Employee", true);
            this.employee = employee;
            this.dialogCurrentUser = currentUser;
            
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            loadDepartments();
            
            if (employee != null) {
                populateFields();
            }
            
            // Apply role-based restrictions
            applyRoleBasedRestrictions();
            
            pack();
            setLocationRelativeTo(parent);
        }
        
        private void initializeComponents() {
            employeeCodeField = new JTextField(20);
            firstNameField = new JTextField(20);
            lastNameField = new JTextField(20);
            emailField = new JTextField(20);
            phoneField = new JTextField(20);
            addressArea = new JTextArea(3, 20);
            addressArea.setLineWrap(true);
            addressArea.setWrapStyleWord(true);
            
            // Date spinners
            dateOfBirthSpinner = new JSpinner(new SpinnerDateModel());
            hireDateSpinner = new JSpinner(new SpinnerDateModel());
            
            JSpinner.DateEditor dateOfBirthEditor = new JSpinner.DateEditor(dateOfBirthSpinner, "yyyy-MM-dd");
            JSpinner.DateEditor hireDateEditor = new JSpinner.DateEditor(hireDateSpinner, "yyyy-MM-dd");
            
            dateOfBirthSpinner.setEditor(dateOfBirthEditor);
            hireDateSpinner.setEditor(hireDateEditor);
            
            departmentCombo = new JComboBox<>();
            positionField = new JTextField(20);
            baseSalaryField = new JTextField(20);
            statusCombo = new JComboBox<>(Employee.EmploymentStatus.values());
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // Employee Code
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Employee Code:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(employeeCodeField, gbc);
            
            // First Name
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("First Name:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(firstNameField, gbc);
            
            // Last Name
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Last Name:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(lastNameField, gbc);
            
            // Email
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Email:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(emailField, gbc);
            
            // Phone
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Phone:"), gbc);
            gbc.gridx = 1;
            formPanel.add(phoneField, gbc);
            
            // Address
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            formPanel.add(new JScrollPane(addressArea), gbc);
            
            // Date of Birth
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Date of Birth:"), gbc);
            gbc.gridx = 1;
            formPanel.add(dateOfBirthSpinner, gbc);
            
            // Hire Date
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Hire Date:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(hireDateSpinner, gbc);
            
            // Department
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Department:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(departmentCombo, gbc);
            
            // Position
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Position:"), gbc);
            gbc.gridx = 1;
            formPanel.add(positionField, gbc);
            
            // Base Salary
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Base Salary:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(baseSalaryField, gbc);
            
            // Status
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Status:"), gbc);
            gbc.gridx = 1;
            formPanel.add(statusCombo, gbc);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            saveButton.addActionListener(e -> saveEmployee());
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void setupEventHandlers() {
            // Set default button
            getRootPane().setDefaultButton((JButton) ((JPanel) getContentPane().getComponent(1)).getComponent(0));
        }
        
        private void loadDepartments() {
            SwingWorker<List<Department>, Void> worker = new SwingWorker<List<Department>, Void>() {
                @Override
                protected List<Department> doInBackground() throws Exception {
                    return departmentDAO.getAllDepartments();
                }
                
                @Override
                protected void done() {
                    try {
                        List<Department> departments = get();
                        departmentCombo.removeAllItems();
                        for (Department dept : departments) {
                            departmentCombo.addItem(dept);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(EmployeeDialog.this,
                            "Error loading departments: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void populateFields() {
            employeeCodeField.setText(employee.getEmployeeCode());
            firstNameField.setText(employee.getFirstName());
            lastNameField.setText(employee.getLastName());
            emailField.setText(employee.getEmail());
            phoneField.setText(employee.getPhone());
            addressArea.setText(employee.getAddress());
            
            if (employee.getDateOfBirth() != null) {
                dateOfBirthSpinner.setValue(employee.getDateOfBirth());
            }
            
            if (employee.getHireDate() != null) {
                hireDateSpinner.setValue(employee.getHireDate());
            }
            
            positionField.setText(employee.getPosition());
            baseSalaryField.setText(employee.getBaseSalary().toString());
            statusCombo.setSelectedItem(employee.getEmploymentStatus());
            
            // Select department
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                Department dept = departmentCombo.getItemAt(i);
                if (dept.getDepartmentId() == employee.getDepartmentId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        private void saveEmployee() {
            if (!validateInput()) {
                return;
            }
            
            try {
                Employee emp = employee != null ? employee : new Employee();
                
                emp.setEmployeeCode(ValidationUtils.sanitizeInput(employeeCodeField.getText()));
                emp.setFirstName(ValidationUtils.sanitizeInput(firstNameField.getText()));
                emp.setLastName(ValidationUtils.sanitizeInput(lastNameField.getText()));
                emp.setEmail(ValidationUtils.sanitizeInput(emailField.getText()));
                emp.setPhone(ValidationUtils.sanitizeInput(phoneField.getText()));
                emp.setAddress(ValidationUtils.sanitizeInput(addressArea.getText()));
                emp.setDateOfBirth(new Date(((java.util.Date) dateOfBirthSpinner.getValue()).getTime()));
                emp.setHireDate(new Date(((java.util.Date) hireDateSpinner.getValue()).getTime()));
                emp.setDepartmentId(((Department) departmentCombo.getSelectedItem()).getDepartmentId());
                emp.setPosition(ValidationUtils.sanitizeInput(positionField.getText()));
                emp.setBaseSalary(new BigDecimal(baseSalaryField.getText()));
                emp.setEmploymentStatus((Employee.EmploymentStatus) statusCombo.getSelectedItem());
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (employee == null) {
                            return employeeDAO.createEmployee(emp);
                        } else {
                            return employeeDAO.updateEmployee(emp);
                        }
                    }
                    
                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                confirmed = true;
                                dispose();
                            } else {
                                JOptionPane.showMessageDialog(EmployeeDialog.this,
                                    "Failed to save employee.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(EmployeeDialog.this,
                                "Error saving employee: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error preparing employee data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private boolean validateInput() {
            // Employee Code
            String employeeCode = ValidationUtils.sanitizeInput(employeeCodeField.getText());
            if (!ValidationUtils.isValidEmployeeCode(employeeCode)) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid employee code (3-20 characters, uppercase letters and numbers only).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                employeeCodeField.requestFocus();
                return false;
            }
            
            // Check if employee code exists
            if (employeeDAO.employeeCodeExists(employeeCode, employee != null ? employee.getEmployeeId() : 0)) {
                JOptionPane.showMessageDialog(this,
                    "Employee code already exists. Please choose a different code.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                employeeCodeField.requestFocus();
                return false;
            }
            
            // First Name
            if (!ValidationUtils.isValidName(firstNameField.getText())) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid first name.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                firstNameField.requestFocus();
                return false;
            }
            
            // Last Name
            if (!ValidationUtils.isValidName(lastNameField.getText())) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid last name.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                lastNameField.requestFocus();
                return false;
            }
            
            // Email
            String email = ValidationUtils.sanitizeInput(emailField.getText());
            if (!ValidationUtils.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return false;
            }
            
            // Check if email exists
            if (employeeDAO.emailExists(email, employee != null ? employee.getEmployeeId() : 0)) {
                JOptionPane.showMessageDialog(this,
                    "Email address already exists. Please choose a different email.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
                return false;
            }
            
            // Phone (optional)
            if (!ValidationUtils.isValidPhone(phoneField.getText())) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid phone number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                phoneField.requestFocus();
                return false;
            }
            
            // Department
            if (departmentCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a department.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                departmentCombo.requestFocus();
                return false;
            }
            
            // Base Salary
            try {
                BigDecimal salary = new BigDecimal(baseSalaryField.getText());
                if (!ValidationUtils.isValidSalary(salary.doubleValue())) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid salary amount.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    baseSalaryField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid salary amount.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                baseSalaryField.requestFocus();
                return false;
            }
            
            return true;
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
        
        private void applyRoleBasedRestrictions() {
            // Apply restrictions based on user role
            if (dialogCurrentUser != null) {
                // HR users can manage employees but may have some restrictions
                // ADMIN users have full access
                if (dialogCurrentUser.getRole() == User.UserRole.HR) {
                    // HR users cannot modify salary for existing employees (only ADMIN can)
                    if (employee != null) {
                        baseSalaryField.setEditable(false);
                        baseSalaryField.setToolTipText("Only administrators can modify employee salaries");
                    }
                }
                // Both ADMIN and HR can create/edit employees, but with different permissions
            }
        }
    }
}