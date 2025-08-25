package gui;

import dao.EmployeeDAO;
import dao.PayrollDAO;
import models.Employee;
import models.Payroll;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel for managing payroll
 */
public class PayrollManagementPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private User currentUser;
    private PayrollDAO payrollDAO;
    private EmployeeDAO employeeDAO;
    
    // Components
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton addButton, editButton, deleteButton, refreshButton, markPaidButton;
    private JLabel totalPayrollsLabel;
    
    // Table columns
    private final String[] columnNames = {
        "ID", "Employee Code", "Employee Name", "Pay Period Start", "Pay Period End",
        "Base Salary", "Overtime Pay", "Bonus", "Gross Salary", "Deductions", "Net Salary", "Status", "Payment Date"
    };
    
    public PayrollManagementPanel(User user) {
        this.currentUser = user;
        this.payrollDAO = new PayrollDAO();
        this.employeeDAO = new EmployeeDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadPayrollData();
    }
    
    private void initializeComponents() {
        // Create table
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        payrollTable = new JTable(tableModel);
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.setRowHeight(25);
        payrollTable.getTableHeader().setReorderingAllowed(false);
        
        // Setup table sorter
        tableSorter = new TableRowSorter<>(tableModel);
        payrollTable.setRowSorter(tableSorter);
        
        // Hide ID column
        payrollTable.getColumnModel().getColumn(0).setMinWidth(0);
        payrollTable.getColumnModel().getColumn(0).setMaxWidth(0);
        payrollTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        payrollTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Employee Code
        payrollTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Employee Name
        payrollTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Pay Period Start
        payrollTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Pay Period End
        payrollTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Base Salary
        payrollTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Overtime Pay
        payrollTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Bonus
        payrollTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Gross Salary
        payrollTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Deductions
        payrollTable.getColumnModel().getColumn(10).setPreferredWidth(100); // Net Salary
        payrollTable.getColumnModel().getColumn(11).setPreferredWidth(80);  // Status
        payrollTable.getColumnModel().getColumn(12).setPreferredWidth(100); // Payment Date
        
        // Create components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search payroll by employee code or name");
        
        statusFilter = new JComboBox<>(new String[]{"All", "PENDING", "PAID", "CANCELLED"});
        statusFilter.setToolTipText("Filter by payment status");
        
        addButton = new JButton("Add Payroll");
        editButton = new JButton("Edit Payroll");
        deleteButton = new JButton("Delete Payroll");
        markPaidButton = new JButton("Mark as Paid");
        refreshButton = new JButton("Refresh");
        
        totalPayrollsLabel = new JLabel("Total Payrolls: 0");
        
        // Set button properties
        addButton.setPreferredSize(new Dimension(120, 30));
        editButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.setPreferredSize(new Dimension(120, 30));
        markPaidButton.setPreferredSize(new Dimension(120, 30));
        refreshButton.setPreferredSize(new Dimension(100, 30));
        
        // Initially disable edit, delete, and mark paid buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        markPaidButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with search, filter, and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Search and filter panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusFilter);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(markPaidButton);
        buttonPanel.add(refreshButton);
        
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Table panel
        JScrollPane scrollPane = new JScrollPane(payrollTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Bottom panel with statistics
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(totalPayrollsLabel);
        
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
        
        // Status filter
        statusFilter.addActionListener(e -> filterTable());
        
        // Table selection listener
        payrollTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = payrollTable.getSelectedRow() != -1;
                editButton.setEnabled(hasSelection);
                deleteButton.setEnabled(hasSelection);
                
                // Enable mark paid button only for pending payrolls
                if (hasSelection) {
                    int modelRow = payrollTable.convertRowIndexToModel(payrollTable.getSelectedRow());
                    String status = (String) tableModel.getValueAt(modelRow, 11);
                    markPaidButton.setEnabled("PENDING".equals(status));
                } else {
                    markPaidButton.setEnabled(false);
                }
            }
        });
        
        // Button listeners
        addButton.addActionListener(e -> showPayrollDialog(null));
        editButton.addActionListener(e -> editSelectedPayroll());
        deleteButton.addActionListener(e -> deleteSelectedPayroll());
        markPaidButton.addActionListener(e -> markSelectedPayrollAsPaid());
        refreshButton.addActionListener(e -> refreshData());
        
        // Double-click to edit
        payrollTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && payrollTable.getSelectedRow() != -1) {
                    editSelectedPayroll();
                }
            }
        });
    }
    
    private void filterTable() {
        String searchText = searchField.getText().trim();
        String statusText = (String) statusFilter.getSelectedItem();
        
        RowFilter<DefaultTableModel, Object> searchFilter = null;
        RowFilter<DefaultTableModel, Object> statusFilterObj = null;
        
        if (!searchText.isEmpty()) {
            searchFilter = RowFilter.regexFilter("(?i)" + searchText, 1, 2); // Employee Code and Name columns
        }
        
        if (!"All".equals(statusText)) {
            statusFilterObj = RowFilter.regexFilter(statusText, 11); // Status column
        }
        
        if (searchFilter != null && statusFilterObj != null) {
            tableSorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(searchFilter, statusFilterObj)));
        } else if (searchFilter != null) {
            tableSorter.setRowFilter(searchFilter);
        } else if (statusFilterObj != null) {
            tableSorter.setRowFilter(statusFilterObj);
        } else {
            tableSorter.setRowFilter(null);
        }
        
        updatePayrollCount();
    }
    
    private void loadPayrollData() {
        SwingWorker<List<Payroll>, Void> worker = new SwingWorker<List<Payroll>, Void>() {
            @Override
            protected List<Payroll> doInBackground() throws Exception {
                return payrollDAO.getAllPayrolls();
            }
            
            @Override
            protected void done() {
                try {
                    List<Payroll> payrolls = get();
                    updateTable(payrolls);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                        "Error loading payroll data: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void updateTable(List<Payroll> payrolls) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Payroll payroll : payrolls) {
            Object[] row = {
                payroll.getPayrollId(),
                payroll.getEmployeeCode(),
                payroll.getEmployeeName(),
                payroll.getPayPeriodStart() != null ? dateFormat.format(payroll.getPayPeriodStart()) : "",
                payroll.getPayPeriodEnd() != null ? dateFormat.format(payroll.getPayPeriodEnd()) : "",
                String.format("$%.2f", payroll.getBaseSalary()),
                String.format("$%.2f", payroll.getOvertimePay()),
                String.format("$%.2f", payroll.getBonus()),
                String.format("$%.2f", payroll.getGrossSalary()),
                String.format("$%.2f", payroll.getTotalDeductions()),
                String.format("$%.2f", payroll.getNetSalary()),
                payroll.getPaymentStatus().toString(),
                payroll.getPaymentDate() != null ? dateFormat.format(payroll.getPaymentDate()) : ""
            };
            tableModel.addRow(row);
        }
        
        updatePayrollCount();
    }
    
    private void updatePayrollCount() {
        int totalRows = tableModel.getRowCount();
        int visibleRows = payrollTable.getRowCount();
        
        if (visibleRows == totalRows) {
            totalPayrollsLabel.setText("Total Payrolls: " + totalRows);
        } else {
            totalPayrollsLabel.setText("Showing " + visibleRows + " of " + totalRows + " payrolls");
        }
    }
    
    private void showPayrollDialog(Payroll payroll) {
        PayrollDialog dialog = new PayrollDialog((Frame) SwingUtilities.getWindowAncestor(this), payroll);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            refreshData();
        }
    }
    
    private void editSelectedPayroll() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = payrollTable.convertRowIndexToModel(selectedRow);
        int payrollId = (Integer) tableModel.getValueAt(modelRow, 0);
        
        // Load payroll details
        SwingWorker<Payroll, Void> worker = new SwingWorker<Payroll, Void>() {
            @Override
            protected Payroll doInBackground() throws Exception {
                return payrollDAO.getPayrollById(payrollId);
            }
            
            @Override
            protected void done() {
                try {
                    Payroll payroll = get();
                    if (payroll != null) {
                        showPayrollDialog(payroll);
                    } else {
                        JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                            "Payroll record not found.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                        "Error loading payroll: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void deleteSelectedPayroll() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = payrollTable.convertRowIndexToModel(selectedRow);
        String employeeCode = (String) tableModel.getValueAt(modelRow, 1);
        String employeeName = (String) tableModel.getValueAt(modelRow, 2);
        String payPeriod = tableModel.getValueAt(modelRow, 3) + " to " + tableModel.getValueAt(modelRow, 4);
        
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete the payroll record for:\n" +
            "Employee: " + employeeCode + " (" + employeeName + ")\n" +
            "Pay Period: " + payPeriod + "\n\n" +
            "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            int payrollId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return payrollDAO.deletePayroll(payrollId);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                                "Payroll record deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                                "Failed to delete payroll record.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                            "Error deleting payroll: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void markSelectedPayrollAsPaid() {
        int selectedRow = payrollTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert view row to model row
        int modelRow = payrollTable.convertRowIndexToModel(selectedRow);
        int payrollId = (Integer) tableModel.getValueAt(modelRow, 0);
        String employeeCode = (String) tableModel.getValueAt(modelRow, 1);
        String employeeName = (String) tableModel.getValueAt(modelRow, 2);
        
        int option = JOptionPane.showConfirmDialog(this,
            "Mark payroll as PAID for:\n" +
            "Employee: " + employeeCode + " (" + employeeName + ")\n\n" +
            "Payment date will be set to today.",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            Date paymentDate = new Date(System.currentTimeMillis());
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return payrollDAO.updatePaymentStatus(payrollId, Payroll.PaymentStatus.PAID, paymentDate);
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                                "Payroll marked as paid successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshData();
                        } else {
                            JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                                "Failed to update payment status.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PayrollManagementPanel.this,
                            "Error updating payment status: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    public void refreshData() {
        loadPayrollData();
    }
    
    // Inner class for Payroll Dialog
    private class PayrollDialog extends JDialog {
        private Payroll payroll;
        private boolean confirmed = false;
        
        // Form components
        private JComboBox<Employee> employeeCombo;
        private JSpinner payPeriodStartSpinner;
        private JSpinner payPeriodEndSpinner;
        private JTextField baseSalaryField;
        private JTextField overtimeHoursField;
        private JTextField overtimeRateField;
        private JTextField bonusField;
        private JTextField allowancesField;
        private JTextField taxDeductionField;
        private JTextField insuranceDeductionField;
        private JTextField otherDeductionsField;
        private JTextField grossSalaryField;
        private JTextField totalDeductionsField;
        private JTextField netSalaryField;
        private JComboBox<Payroll.PaymentStatus> statusCombo;
        private JButton calculateButton;
        
        public PayrollDialog(Frame parent, Payroll payroll) {
            super(parent, payroll == null ? "Add Payroll" : "Edit Payroll", true);
            this.payroll = payroll;
            
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            loadEmployees();
            
            if (payroll != null) {
                populateFields();
            }
            
            pack();
            setLocationRelativeTo(parent);
        }
        
        private void initializeComponents() {
            employeeCombo = new JComboBox<>();
            
            // Date spinners
            payPeriodStartSpinner = new JSpinner(new SpinnerDateModel());
            payPeriodEndSpinner = new JSpinner(new SpinnerDateModel());
            
            JSpinner.DateEditor startEditor = new JSpinner.DateEditor(payPeriodStartSpinner, "yyyy-MM-dd");
            JSpinner.DateEditor endEditor = new JSpinner.DateEditor(payPeriodEndSpinner, "yyyy-MM-dd");
            
            payPeriodStartSpinner.setEditor(startEditor);
            payPeriodEndSpinner.setEditor(endEditor);
            
            baseSalaryField = new JTextField(15);
            overtimeHoursField = new JTextField(15);
            overtimeRateField = new JTextField(15);
            bonusField = new JTextField(15);
            allowancesField = new JTextField(15);
            taxDeductionField = new JTextField(15);
            insuranceDeductionField = new JTextField(15);
            otherDeductionsField = new JTextField(15);
            grossSalaryField = new JTextField(15);
            totalDeductionsField = new JTextField(15);
            netSalaryField = new JTextField(15);
            
            statusCombo = new JComboBox<>(Payroll.PaymentStatus.values());
            calculateButton = new JButton("Calculate");
            
            // Set default values
            overtimeHoursField.setText("0.00");
            overtimeRateField.setText("1.5");
            bonusField.setText("0.00");
            allowancesField.setText("0.00");
            taxDeductionField.setText("0.00");
            insuranceDeductionField.setText("0.00");
            otherDeductionsField.setText("0.00");
            
            // Make calculated fields read-only
            grossSalaryField.setEditable(false);
            totalDeductionsField.setEditable(false);
            netSalaryField.setEditable(false);
            grossSalaryField.setBackground(Color.LIGHT_GRAY);
            totalDeductionsField.setBackground(Color.LIGHT_GRAY);
            netSalaryField.setBackground(Color.LIGHT_GRAY);
        }
        
        private void setupLayout() {
            setLayout(new BorderLayout());
            
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // Employee
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Employee:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(employeeCombo, gbc);
            
            // Pay Period Start
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Pay Period Start:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(payPeriodStartSpinner, gbc);
            
            // Pay Period End
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Pay Period End:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(payPeriodEndSpinner, gbc);
            
            // Base Salary
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Base Salary:*"), gbc);
            gbc.gridx = 1;
            formPanel.add(baseSalaryField, gbc);
            
            // Overtime Hours
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Overtime Hours:"), gbc);
            gbc.gridx = 1;
            formPanel.add(overtimeHoursField, gbc);
            
            // Overtime Rate
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Overtime Rate:"), gbc);
            gbc.gridx = 1;
            formPanel.add(overtimeRateField, gbc);
            
            // Bonus
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Bonus:"), gbc);
            gbc.gridx = 1;
            formPanel.add(bonusField, gbc);
            
            // Allowances
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Allowances:"), gbc);
            gbc.gridx = 1;
            formPanel.add(allowancesField, gbc);
            
            // Tax Deduction
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Tax Deduction:"), gbc);
            gbc.gridx = 1;
            formPanel.add(taxDeductionField, gbc);
            
            // Insurance Deduction
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Insurance Deduction:"), gbc);
            gbc.gridx = 1;
            formPanel.add(insuranceDeductionField, gbc);
            
            // Other Deductions
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Other Deductions:"), gbc);
            gbc.gridx = 1;
            formPanel.add(otherDeductionsField, gbc);
            
            // Calculate button
            row++;
            gbc.gridx = 1; gbc.gridy = row;
            gbc.anchor = GridBagConstraints.CENTER;
            formPanel.add(calculateButton, gbc);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Gross Salary
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Gross Salary:"), gbc);
            gbc.gridx = 1;
            formPanel.add(grossSalaryField, gbc);
            
            // Total Deductions
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Total Deductions:"), gbc);
            gbc.gridx = 1;
            formPanel.add(totalDeductionsField, gbc);
            
            // Net Salary
            row++;
            gbc.gridx = 0; gbc.gridy = row;
            formPanel.add(new JLabel("Net Salary:"), gbc);
            gbc.gridx = 1;
            formPanel.add(netSalaryField, gbc);
            
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
            
            saveButton.addActionListener(e -> savePayroll());
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            add(formPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
        
        private void setupEventHandlers() {
            // Calculate button
            calculateButton.addActionListener(e -> calculateSalary());
            
            // Employee selection - auto-fill base salary
            employeeCombo.addActionListener(e -> {
                Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
                if (selectedEmployee != null) {
                    baseSalaryField.setText(selectedEmployee.getBaseSalary().toString());
                }
            });
            
            // Set default button
            getRootPane().setDefaultButton((JButton) ((JPanel) getContentPane().getComponent(1)).getComponent(0));
        }
        
        private void loadEmployees() {
            SwingWorker<List<Employee>, Void> worker = new SwingWorker<List<Employee>, Void>() {
                @Override
                protected List<Employee> doInBackground() throws Exception {
                    return employeeDAO.getAllEmployees();
                }
                
                @Override
                protected void done() {
                    try {
                        List<Employee> employees = get();
                        employeeCombo.removeAllItems();
                        for (Employee emp : employees) {
                            employeeCombo.addItem(emp);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PayrollDialog.this,
                            "Error loading employees: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
        
        private void populateFields() {
            // Select employee
            for (int i = 0; i < employeeCombo.getItemCount(); i++) {
                Employee emp = employeeCombo.getItemAt(i);
                if (emp.getEmployeeId() == payroll.getEmployeeId()) {
                    employeeCombo.setSelectedIndex(i);
                    break;
                }
            }
            
            if (payroll.getPayPeriodStart() != null) {
                payPeriodStartSpinner.setValue(payroll.getPayPeriodStart());
            }
            
            if (payroll.getPayPeriodEnd() != null) {
                payPeriodEndSpinner.setValue(payroll.getPayPeriodEnd());
            }
            
            baseSalaryField.setText(payroll.getBaseSalary().toString());
            overtimeHoursField.setText(payroll.getOvertimeHours().toString());
            overtimeRateField.setText(payroll.getOvertimeRate().toString());
            bonusField.setText(payroll.getBonus().toString());
            allowancesField.setText(payroll.getAllowances().toString());
            taxDeductionField.setText(payroll.getTaxDeduction().toString());
            insuranceDeductionField.setText(payroll.getInsuranceDeduction().toString());
            otherDeductionsField.setText(payroll.getOtherDeductions().toString());
            grossSalaryField.setText(payroll.getGrossSalary().toString());
            totalDeductionsField.setText(payroll.getTotalDeductions().toString());
            netSalaryField.setText(payroll.getNetSalary().toString());
            statusCombo.setSelectedItem(payroll.getPaymentStatus());
        }
        
        private void calculateSalary() {
            try {
                BigDecimal baseSalary = new BigDecimal(baseSalaryField.getText());
                BigDecimal overtimeHours = new BigDecimal(overtimeHoursField.getText());
                BigDecimal overtimeRate = new BigDecimal(overtimeRateField.getText());
                BigDecimal bonus = new BigDecimal(bonusField.getText());
                BigDecimal allowances = new BigDecimal(allowancesField.getText());
                BigDecimal taxDeduction = new BigDecimal(taxDeductionField.getText());
                BigDecimal insuranceDeduction = new BigDecimal(insuranceDeductionField.getText());
                BigDecimal otherDeductions = new BigDecimal(otherDeductionsField.getText());
                
                // Calculate overtime pay (assuming 160 hours per month)
                BigDecimal overtimePay = overtimeHours.multiply(baseSalary.divide(new BigDecimal("160")))
                                                    .multiply(overtimeRate);
                
                // Calculate gross salary
                BigDecimal grossSalary = baseSalary.add(overtimePay).add(bonus).add(allowances);
                
                // Calculate total deductions
                BigDecimal totalDeductions = taxDeduction.add(insuranceDeduction).add(otherDeductions);
                
                // Calculate net salary
                BigDecimal netSalary = grossSalary.subtract(totalDeductions);
                
                // Update fields
                grossSalaryField.setText(String.format("%.2f", grossSalary));
                totalDeductionsField.setText(String.format("%.2f", totalDeductions));
                netSalaryField.setText(String.format("%.2f", netSalary));
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for all salary fields.",
                    "Calculation Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void savePayroll() {
            if (!validateInput()) {
                return;
            }
            
            try {
                Payroll pay = payroll != null ? payroll : new Payroll();
                
                Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
                pay.setEmployeeId(selectedEmployee.getEmployeeId());
                pay.setPayPeriodStart(new Date(((java.util.Date) payPeriodStartSpinner.getValue()).getTime()));
                pay.setPayPeriodEnd(new Date(((java.util.Date) payPeriodEndSpinner.getValue()).getTime()));
                pay.setBaseSalary(new BigDecimal(baseSalaryField.getText()));
                pay.setOvertimeHours(new BigDecimal(overtimeHoursField.getText()));
                pay.setOvertimeRate(new BigDecimal(overtimeRateField.getText()));
                pay.setBonus(new BigDecimal(bonusField.getText()));
                pay.setAllowances(new BigDecimal(allowancesField.getText()));
                pay.setTaxDeduction(new BigDecimal(taxDeductionField.getText()));
                pay.setInsuranceDeduction(new BigDecimal(insuranceDeductionField.getText()));
                pay.setOtherDeductions(new BigDecimal(otherDeductionsField.getText()));
                pay.setPaymentStatus((Payroll.PaymentStatus) statusCombo.getSelectedItem());
                pay.setCreatedBy(currentUser.getUserId());
                
                // Recalculate to ensure consistency
                pay.calculateSalary();
                
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (payroll == null) {
                            return payrollDAO.createPayroll(pay);
                        } else {
                            return payrollDAO.updatePayroll(pay);
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
                                JOptionPane.showMessageDialog(PayrollDialog.this,
                                    "Failed to save payroll.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(PayrollDialog.this,
                                "Error saving payroll: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error preparing payroll data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private boolean validateInput() {
            // Employee
            if (employeeCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select an employee.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                employeeCombo.requestFocus();
                return false;
            }
            
            // Pay period dates
            Date startDate = new Date(((java.util.Date) payPeriodStartSpinner.getValue()).getTime());
            Date endDate = new Date(((java.util.Date) payPeriodEndSpinner.getValue()).getTime());
            
            if (startDate.after(endDate)) {
                JOptionPane.showMessageDialog(this,
                    "Pay period start date must be before end date.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                payPeriodStartSpinner.requestFocus();
                return false;
            }
            
            // Check for overlapping payroll periods
            Employee selectedEmployee = (Employee) employeeCombo.getSelectedItem();
            if (payrollDAO.payrollExistsForPeriod(selectedEmployee.getEmployeeId(), startDate, endDate, 
                                                 payroll != null ? payroll.getPayrollId() : 0)) {
                JOptionPane.showMessageDialog(this,
                    "A payroll record already exists for this employee in the specified period.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Validate numeric fields
            try {
                new BigDecimal(baseSalaryField.getText());
                new BigDecimal(overtimeHoursField.getText());
                new BigDecimal(overtimeRateField.getText());
                new BigDecimal(bonusField.getText());
                new BigDecimal(allowancesField.getText());
                new BigDecimal(taxDeductionField.getText());
                new BigDecimal(insuranceDeductionField.getText());
                new BigDecimal(otherDeductionsField.getText());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for all salary fields.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            return true;
        }
        
        public boolean isConfirmed() {
            return confirmed;
        }
    }
}