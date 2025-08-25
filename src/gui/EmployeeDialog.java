package gui;

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import models.Department;
import models.Employee;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Dialog for adding/editing employees
 */
public class EmployeeDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private Employee employee;
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    private boolean confirmed = false;
    
    // Form components
    private JTextField employeeCodeField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField dateOfBirthField;
    private JTextField hireDateField;
    private JComboBox<Department> departmentCombo;
    private JTextField positionField;
    private JTextField baseSalaryField;
    private JComboBox<Employee.EmploymentStatus> statusCombo;
    
    private JButton saveButton;
    private JButton cancelButton;
    
    public EmployeeDialog(Frame parent, Employee employee) {
        super(parent, employee == null ? "Add Employee" : "Edit Employee", true);
        this.employee = employee;
        this.employeeDAO = new EmployeeDAO();
        this.departmentDAO = new DepartmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Create form fields
        employeeCodeField = new JTextField(20);
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        dateOfBirthField = new JTextField(20);
        hireDateField = new JTextField(20);
        departmentCombo = new JComboBox<>();
        positionField = new JTextField(20);
        baseSalaryField = new JTextField(20);
        statusCombo = new JComboBox<>(Employee.EmploymentStatus.values());
        
        // Set tooltips
        employeeCodeField.setToolTipText("Unique employee code (e.g., EMP001)");
        dateOfBirthField.setToolTipText("Date format: YYYY-MM-DD");
        hireDateField.setToolTipText("Date format: YYYY-MM-DD");
        baseSalaryField.setToolTipText("Annual base salary in dollars");
        
        // Create buttons
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        
        // Set button properties
        saveButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel with form
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 0: Employee Code
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Employee Code:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(employeeCodeField, gbc);
        
        // Row 1: First Name
        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("First Name:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(firstNameField, gbc);
        
        // Row 2: Last Name
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Last Name:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(lastNameField, gbc);
        
        // Row 3: Email
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        // Row 4: Phone
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(phoneField, gbc);
        
        // Row 5: Address
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        mainPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JScrollPane(addressArea), gbc);
        
        // Row 6: Date of Birth
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(dateOfBirthField, gbc);
        
        // Row 7: Hire Date
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Hire Date:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(hireDateField, gbc);
        
        // Row 8: Department
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(departmentCombo, gbc);
        
        // Row 9: Position
        gbc.gridx = 0; gbc.gridy = 9;
        mainPanel.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(positionField, gbc);
        
        // Row 10: Base Salary
        gbc.gridx = 0; gbc.gridy = 10;
        mainPanel.add(new JLabel("Base Salary:*"), gbc);
        gbc.gridx = 1;
        mainPanel.add(baseSalaryField, gbc);
        
        // Row 11: Status
        gbc.gridx = 0; gbc.gridy = 11;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(statusCombo, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add required field note
        JLabel noteLabel = new JLabel("* Required fields");
        noteLabel.setFont(noteLabel.getFont().deriveFont(Font.ITALIC));
        noteLabel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        add(noteLabel, BorderLayout.NORTH);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        // Enter key saves, Escape key cancels
        getRootPane().setDefaultButton(saveButton);
        
        // Add key bindings
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    private void loadData() {
        // Load departments
        loadDepartments();
        
        // If editing existing employee, populate fields
        if (employee != null) {
            employeeCodeField.setText(employee.getEmployeeCode());
            firstNameField.setText(employee.getFirstName());
            lastNameField.setText(employee.getLastName());
            emailField.setText(employee.getEmail());
            phoneField.setText(employee.getPhone());
            addressArea.setText(employee.getAddress());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (employee.getDateOfBirth() != null) {
                dateOfBirthField.setText(dateFormat.format(employee.getDateOfBirth()));
            }
            if (employee.getHireDate() != null) {
                hireDateField.setText(dateFormat.format(employee.getHireDate()));
            }
            
            // Select department
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                Department dept = departmentCombo.getItemAt(i);
                if (dept.getDepartmentId() == employee.getDepartmentId()) {
                    departmentCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            positionField.setText(employee.getPosition());
            if (employee.getBaseSalary() != null) {
                baseSalaryField.setText(employee.getBaseSalary().toString());
            }
            statusCombo.setSelectedItem(employee.getEmploymentStatus());
        } else {
            // Set defaults for new employee
            statusCombo.setSelectedItem(Employee.EmploymentStatus.ACTIVE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            hireDateField.setText(dateFormat.format(new java.util.Date()));
        }
    }
    
    private void loadDepartments() {
        try {
            List<Department> departments = departmentDAO.getAllDepartments();
            departmentCombo.removeAllItems();
            
            // Add empty option
            departmentCombo.addItem(new Department(0, "-- Select Department --", "", "", BigDecimal.ZERO));
            
            for (Department dept : departments) {
                departmentCombo.addItem(dept);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading departments: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveEmployee() {
        try {
            // Validate input
            if (!validateInput()) {
                return;
            }
            
            // Create or update employee object
            if (employee == null) {
                employee = new Employee();
            }
            
            // Set employee data
            employee.setEmployeeCode(employeeCodeField.getText().trim());
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setPhone(phoneField.getText().trim());
            employee.setAddress(addressArea.getText().trim());
            
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (!dateOfBirthField.getText().trim().isEmpty()) {
                try {
                    java.util.Date dob = dateFormat.parse(dateOfBirthField.getText().trim());
                    employee.setDateOfBirth(new Date(dob.getTime()));
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid date of birth format. Please use YYYY-MM-DD.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                java.util.Date hireDate = dateFormat.parse(hireDateField.getText().trim());
                employee.setHireDate(new Date(hireDate.getTime()));
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid hire date format. Please use YYYY-MM-DD.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Set department
            Department selectedDept = (Department) departmentCombo.getSelectedItem();
            if (selectedDept != null && selectedDept.getDepartmentId() > 0) {
                employee.setDepartmentId(selectedDept.getDepartmentId());
            } else {
                employee.setDepartmentId(0); // No department
            }
            
            employee.setPosition(positionField.getText().trim());
            employee.setBaseSalary(new BigDecimal(baseSalaryField.getText().trim()));
            employee.setEmploymentStatus((Employee.EmploymentStatus) statusCombo.getSelectedItem());
            
            // Save to database
            boolean success;
            if (employee.getEmployeeId() == 0) {
                success = employeeDAO.createEmployee(employee);
            } else {
                success = employeeDAO.updateEmployee(employee);
            }
            
            if (success) {
                confirmed = true;
                JOptionPane.showMessageDialog(this,
                    "Employee saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save employee. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving employee: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateInput() {
        // Check required fields
        if (employeeCodeField.getText().trim().isEmpty()) {
            showValidationError("Employee code is required.");
            employeeCodeField.requestFocus();
            return false;
        }
        
        if (firstNameField.getText().trim().isEmpty()) {
            showValidationError("First name is required.");
            firstNameField.requestFocus();
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            showValidationError("Last name is required.");
            lastNameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showValidationError("Email is required.");
            emailField.requestFocus();
            return false;
        }
        
        if (hireDateField.getText().trim().isEmpty()) {
            showValidationError("Hire date is required.");
            hireDateField.requestFocus();
            return false;
        }
        
        if (baseSalaryField.getText().trim().isEmpty()) {
            showValidationError("Base salary is required.");
            baseSalaryField.requestFocus();
            return false;
        }
        
        // Validate email format
        if (!ValidationUtils.isValidEmail(emailField.getText().trim())) {
            showValidationError("Please enter a valid email address.");
            emailField.requestFocus();
            return false;
        }
        
        // Validate salary format
        try {
            BigDecimal salary = new BigDecimal(baseSalaryField.getText().trim());
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                showValidationError("Base salary must be a positive number.");
                baseSalaryField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showValidationError("Please enter a valid salary amount.");
            baseSalaryField.requestFocus();
            return false;
        }
        
        // Check for duplicate employee code
        String employeeCode = employeeCodeField.getText().trim();
        int excludeId = employee != null ? employee.getEmployeeId() : 0;
        if (employeeDAO.employeeCodeExists(employeeCode, excludeId)) {
            showValidationError("Employee code already exists. Please choose a different code.");
            employeeCodeField.requestFocus();
            return false;
        }
        
        // Check for duplicate email
        String email = emailField.getText().trim();
        if (employeeDAO.emailExists(email, excludeId)) {
            showValidationError("Email address already exists. Please choose a different email.");
            emailField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public Employee getEmployee() {
        return employee;
    }
}