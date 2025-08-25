package gui;

import dao.DepartmentDAO;
import dao.EmployeeDAO;
import dao.PayrollDAO;
import models.Department;
import models.Employee;
import models.Payroll;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel for generating and viewing reports
 */
public class ReportsPanel extends JPanel {
    private User currentUser;
    private EmployeeDAO employeeDAO;
    private PayrollDAO payrollDAO;
    private DepartmentDAO departmentDAO;
    
    // Components
    private JTabbedPane reportTabs;
    private JTable employeeReportTable;
    private JTable payrollReportTable;
    private JTable departmentReportTable;
    private DefaultTableModel employeeTableModel;
    private DefaultTableModel payrollTableModel;
    private DefaultTableModel departmentTableModel;
    
    // Filter components
    private JComboBox<Department> departmentFilter;
    private JComboBox<String> statusFilter;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;
    
    // Separate buttons for each tab
    private JButton generateEmployeeReportButton;
    private JButton exportEmployeeReportButton;
    private JButton generatePayrollReportButton;
    private JButton exportPayrollReportButton;
    private JButton generateDepartmentReportButton;
    private JButton exportDepartmentReportButton;
    
    // Summary components
    private JLabel totalEmployeesLabel;
    private JLabel activeEmployeesLabel;
    private JLabel totalPayrollLabel;
    private JLabel totalSalaryLabel;
    
    public ReportsPanel(User user) {
        this.currentUser = user;
        this.employeeDAO = new EmployeeDAO();
        this.payrollDAO = new PayrollDAO();
        this.departmentDAO = new DepartmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }
    
    private void initializeComponents() {
        // Create tabbed pane for different reports
        reportTabs = new JTabbedPane();
        
        // Employee Report Table
        String[] employeeColumns = {
            "Employee Code", "Name", "Email", "Department", "Position", 
            "Base Salary", "Status", "Hire Date"
        };
        employeeTableModel = new DefaultTableModel(employeeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeReportTable = new JTable(employeeTableModel);
        employeeReportTable.setRowHeight(25);
        
        // Payroll Report Table
        String[] payrollColumns = {
            "Employee Code", "Employee Name", "Pay Period", "Base Salary", 
            "Overtime Pay", "Bonus", "Gross Salary", "Deductions", "Net Salary", "Status"
        };
        payrollTableModel = new DefaultTableModel(payrollColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        payrollReportTable = new JTable(payrollTableModel);
        payrollReportTable.setRowHeight(25);
        
        // Department Report Table
        String[] departmentColumns = {
            "Department Code", "Department Name", "Manager", "Employee Count", 
            "Total Salary", "Budget", "Budget Utilization"
        };
        departmentTableModel = new DefaultTableModel(departmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        departmentReportTable = new JTable(departmentTableModel);
        departmentReportTable.setRowHeight(25);
        
        // Filter components
        departmentFilter = new JComboBox<>();
        departmentFilter.addItem(null); // "All departments" option
        
        statusFilter = new JComboBox<>(new String[]{"All", "ACTIVE", "INACTIVE", "TERMINATED"});
        
        // Date spinners for payroll reports
        startDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner = new JSpinner(new SpinnerDateModel());
        
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        
        startDateSpinner.setEditor(startEditor);
        endDateSpinner.setEditor(endEditor);
        
        // Set default date range (current month)
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        startDateSpinner.setValue(cal.getTime());
        
        cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
        endDateSpinner.setValue(cal.getTime());
        
        // Create separate buttons for each tab
        generateEmployeeReportButton = new JButton("Generate Report");
        exportEmployeeReportButton = new JButton("Export to CSV");
        generatePayrollReportButton = new JButton("Generate Report");
        exportPayrollReportButton = new JButton("Export to CSV");
        generateDepartmentReportButton = new JButton("Generate Report");
        exportDepartmentReportButton = new JButton("Export to CSV");
        
        // Summary labels
        totalEmployeesLabel = new JLabel("Total Employees: 0");
        activeEmployeesLabel = new JLabel("Active Employees: 0");
        totalPayrollLabel = new JLabel("Total Payroll Records: 0");
        totalSalaryLabel = new JLabel("Total Salary Paid: $0.00");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create employee report panel
        JPanel employeePanel = createEmployeeReportPanel();
        reportTabs.addTab("Employee Report", employeePanel);
        
        // Create payroll report panel
        JPanel payrollPanel = createPayrollReportPanel();
        reportTabs.addTab("Payroll Report", payrollPanel);
        
        // Create department report panel
        JPanel departmentPanel = createDepartmentReportPanel();
        reportTabs.addTab("Department Report", departmentPanel);
        
        // Create summary panel
        JPanel summaryPanel = createSummaryPanel();
        reportTabs.addTab("Summary", summaryPanel);
        
        add(reportTabs, BorderLayout.CENTER);
    }
    
    private JPanel createEmployeeReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Department:"));
        filterPanel.add(departmentFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(generateEmployeeReportButton);
        filterPanel.add(exportEmployeeReportButton);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(employeeReportTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPayrollReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Start Date:"));
        filterPanel.add(startDateSpinner);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("End Date:"));
        filterPanel.add(endDateSpinner);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(generatePayrollReportButton);
        filterPanel.add(exportPayrollReportButton);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(payrollReportTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDepartmentReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(generateDepartmentReportButton);
        buttonPanel.add(exportDepartmentReportButton);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(departmentReportTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        JLabel titleLabel = new JLabel("System Summary");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 102, 153));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        // Employee Statistics
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 1;
        
        JLabel employeeStatsLabel = new JLabel("Employee Statistics:");
        employeeStatsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        panel.add(employeeStatsLabel, gbc);
        
        gbc.gridy = 2;
        totalEmployeesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(totalEmployeesLabel, gbc);
        
        gbc.gridy = 3;
        activeEmployeesLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(activeEmployeesLabel, gbc);
        
        // Payroll Statistics
        gbc.gridy = 4;
        gbc.insets = new Insets(40, 20, 20, 20);
        
        JLabel payrollStatsLabel = new JLabel("Payroll Statistics:");
        payrollStatsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(payrollStatsLabel, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 20, 20, 20);
        totalPayrollLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(totalPayrollLabel, gbc);
        
        gbc.gridy = 6;
        totalSalaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(totalSalaryLabel, gbc);
        
        // Refresh button
        gbc.gridy = 7;
        gbc.insets = new Insets(40, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        JButton refreshSummaryButton = new JButton("Refresh Summary");
        refreshSummaryButton.addActionListener(e -> loadSummaryData());
        panel.add(refreshSummaryButton, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Employee report buttons
        generateEmployeeReportButton.addActionListener(e -> generateEmployeeReport());
        exportEmployeeReportButton.addActionListener(e -> exportEmployeeReport());
        
        // Payroll report buttons
        generatePayrollReportButton.addActionListener(e -> generatePayrollReport());
        exportPayrollReportButton.addActionListener(e -> exportPayrollReport());
        
        // Department report buttons
        generateDepartmentReportButton.addActionListener(e -> generateDepartmentReport());
        exportDepartmentReportButton.addActionListener(e -> exportDepartmentReport());
        
        // Tab change listener to update button states
        reportTabs.addChangeListener(e -> updateButtonStates());
    }
    
    private void updateButtonStates() {
        // Update export button states based on data availability
        exportEmployeeReportButton.setEnabled(employeeTableModel.getRowCount() > 0);
        exportPayrollReportButton.setEnabled(payrollTableModel.getRowCount() > 0);
        exportDepartmentReportButton.setEnabled(departmentTableModel.getRowCount() > 0);
    }
    
    private void exportEmployeeReport() {
        exportTableToCSV(employeeTableModel, "Employee_Report.csv");
    }
    
    private void exportPayrollReport() {
        exportTableToCSV(payrollTableModel, "Payroll_Report.csv");
    }
    
    private void exportDepartmentReport() {
        exportTableToCSV(departmentTableModel, "Department_Report.csv");
    }
    
    private void generateEmployeeReport() {
        Department selectedDept = (Department) departmentFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        SwingWorker<List<Employee>, Void> worker = new SwingWorker<List<Employee>, Void>() {
            @Override
            protected List<Employee> doInBackground() throws Exception {
                if (selectedDept != null) {
                    return employeeDAO.getEmployeesByDepartment(selectedDept.getDepartmentId());
                } else {
                    return employeeDAO.getAllEmployees();
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<Employee> employees = get();
                    updateEmployeeReportTable(employees, selectedStatus);
                    updateButtonStates();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                        "Error generating employee report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateEmployeeReportTable(List<Employee> employees, String statusFilter) {
        employeeTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Employee employee : employees) {
            // Apply status filter
            if (!"All".equals(statusFilter) && !employee.getEmploymentStatus().toString().equals(statusFilter)) {
                continue;
            }
            
            Object[] row = {
                employee.getEmployeeCode(),
                employee.getFullName(),
                employee.getEmail(),
                employee.getDepartmentName(),
                employee.getPosition(),
                String.format("$%.2f", employee.getBaseSalary()),
                employee.getEmploymentStatus(),
                employee.getHireDate() != null ? dateFormat.format(employee.getHireDate()) : ""
            };
            employeeTableModel.addRow(row);
        }
    }
    
    private void generatePayrollReport() {
        Date startDate = new Date(((java.util.Date) startDateSpinner.getValue()).getTime());
        Date endDate = new Date(((java.util.Date) endDateSpinner.getValue()).getTime());
        
        SwingWorker<List<Payroll>, Void> worker = new SwingWorker<List<Payroll>, Void>() {
            @Override
            protected List<Payroll> doInBackground() throws Exception {
                return payrollDAO.getPayrollsByDateRange(startDate, endDate);
            }
            
            @Override
            protected void done() {
                try {
                    List<Payroll> payrolls = get();
                    updatePayrollReportTable(payrolls);
                    updateButtonStates();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                        "Error generating payroll report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updatePayrollReportTable(List<Payroll> payrolls) {
        payrollTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Payroll payroll : payrolls) {
            String payPeriod = "";
            if (payroll.getPayPeriodStart() != null && payroll.getPayPeriodEnd() != null) {
                payPeriod = dateFormat.format(payroll.getPayPeriodStart()) + " to " + 
                           dateFormat.format(payroll.getPayPeriodEnd());
            }
            
            Object[] row = {
                payroll.getEmployeeCode(),
                payroll.getEmployeeName(),
                payPeriod,
                String.format("$%.2f", payroll.getBaseSalary()),
                String.format("$%.2f", payroll.getOvertimePay()),
                String.format("$%.2f", payroll.getBonus()),
                String.format("$%.2f", payroll.getGrossSalary()),
                String.format("$%.2f", payroll.getTotalDeductions()),
                String.format("$%.2f", payroll.getNetSalary()),
                payroll.getPaymentStatus()
            };
            payrollTableModel.addRow(row);
        }
    }
    
    private void generateDepartmentReport() {
        SwingWorker<List<Department>, Void> worker = new SwingWorker<List<Department>, Void>() {
            @Override
            protected List<Department> doInBackground() throws Exception {
                return departmentDAO.getAllDepartments();
            }
            
            @Override
            protected void done() {
                try {
                    List<Department> departments = get();
                    updateDepartmentReportTable(departments);
                    updateButtonStates();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ReportsPanel.this,
                        "Error generating department report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateDepartmentReportTable(List<Department> departments) {
        departmentTableModel.setRowCount(0);
        
        for (Department department : departments) {
            // Get employee count and total salary for department
            int employeeCount = departmentDAO.getEmployeeCount(department.getDepartmentId());
            
            // Calculate total salary for department employees
            List<Employee> deptEmployees = employeeDAO.getEmployeesByDepartment(department.getDepartmentId());
            BigDecimal totalSalary = BigDecimal.ZERO;
            for (Employee emp : deptEmployees) {
                totalSalary = totalSalary.add(emp.getBaseSalary());
            }
            
            // Calculate budget utilization
            String budgetUtilization = "N/A";
            if (department.getBudget() != null && department.getBudget().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal utilization = totalSalary.divide(department.getBudget(), 4, RoundingMode.HALF_UP)
                                                   .multiply(new BigDecimal("100"));
                budgetUtilization = String.format("%.1f%%", utilization);
            }
            
            Object[] row = {
                department.getDepartmentCode(),
                department.getDepartmentName(),
                department.getManagerName(),
                employeeCount,
                String.format("$%.2f", totalSalary),
                department.getBudget() != null ? String.format("$%.2f", department.getBudget()) : "N/A",
                budgetUtilization
            };
            departmentTableModel.addRow(row);
        }
    }
    
    private void exportTableToCSV(DefaultTableModel model, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    writeCSVFile(model, fileToSave);
                    return null;
                }
                
                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                            "Export Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ReportsPanel.this,
                            "Error exporting report: " + e.getMessage(),
                            "Export Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void writeCSVFile(DefaultTableModel model, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Write headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                writer.append(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    writer.append(",");
                }
            }
            writer.append("\n");
            
            // Write data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object value = model.getValueAt(i, j);
                    String cellValue = value != null ? value.toString() : "";
                    
                    // Escape commas and quotes in CSV
                    if (cellValue.contains(",") || cellValue.contains("\"")) {
                        cellValue = "\"" + cellValue.replace("\"", "\"\"") + "\"";
                    }
                    
                    writer.append(cellValue);
                    if (j < model.getColumnCount() - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }
        }
    }
    
    private void loadInitialData() {
        loadDepartments();
        loadSummaryData();
        generateEmployeeReport(); // Load default employee report
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
                    departmentFilter.removeAllItems();
                    departmentFilter.addItem(null); // "All departments" option
                    for (Department dept : departments) {
                        departmentFilter.addItem(dept);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading departments for filter: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    private void loadSummaryData() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Get employee statistics
                int[] employeeStats = employeeDAO.getEmployeeStatistics();
                
                // Get payroll statistics
                List<Payroll> allPayrolls = payrollDAO.getAllPayrolls();
                BigDecimal totalSalaryPaid = BigDecimal.ZERO;
                
                for (Payroll payroll : allPayrolls) {
                    if (payroll.getPaymentStatus() == Payroll.PaymentStatus.PAID) {
                        totalSalaryPaid = totalSalaryPaid.add(payroll.getNetSalary());
                    }
                }
                
                // Make final copy for lambda
                final BigDecimal finalTotalSalaryPaid = totalSalaryPaid;
                
                // Update labels on EDT
                SwingUtilities.invokeLater(() -> {
                    totalEmployeesLabel.setText("Total Employees: " + employeeStats[0]);
                    activeEmployeesLabel.setText("Active Employees: " + employeeStats[1]);
                    totalPayrollLabel.setText("Total Payroll Records: " + allPayrolls.size());
                    totalSalaryLabel.setText("Total Salary Paid: $" + String.format("%.2f", finalTotalSalaryPaid));
                });
                
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    System.err.println("Error loading summary data: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }
    
    public void refreshData() {
        loadInitialData();
        // Generate all reports
        generateEmployeeReport();
        generatePayrollReport();
        generateDepartmentReport();
    }
}