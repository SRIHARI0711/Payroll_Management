package gui;

import config.DatabaseConfig;
import models.User;
import utils.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application frame
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private User currentUser;
    private JTabbedPane tabbedPane;
    private EmployeeManagementPanel employeePanel;
    private PayrollManagementPanel payrollPanel;
    private ReportsPanel reportsPanel;
    private JLabel statusLabel;
    private JLabel userLabel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupMenuBar();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle(DatabaseConfig.APP_NAME + " - Main Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        employeePanel = new EmployeeManagementPanel(currentUser);
        payrollPanel = new PayrollManagementPanel(currentUser);
        reportsPanel = new ReportsPanel(currentUser);
        
        // Add tabs
        tabbedPane.addTab("Employee Management", null, employeePanel, "Manage employee information");
        tabbedPane.addTab("Payroll Management", null, payrollPanel, "Manage employee payroll");
        tabbedPane.addTab("Reports", null, reportsPanel, "View and generate reports");
        
        // Create status bar
        statusLabel = new JLabel("Ready");
        userLabel = new JLabel("Logged in as: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        userLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Add main content
        add(tabbedPane, BorderLayout.CENTER);
        
        // Create status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(userLabel, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.setMnemonic('R');
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> refreshCurrentPanel());
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());
        
        fileMenu.add(refreshItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        
        JMenuItem employeeViewItem = new JMenuItem("Employee Management");
        employeeViewItem.setMnemonic('E');
        employeeViewItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
        employeeViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        JMenuItem payrollViewItem = new JMenuItem("Payroll Management");
        payrollViewItem.setMnemonic('P');
        payrollViewItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
        payrollViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        
        JMenuItem reportsViewItem = new JMenuItem("Reports");
        reportsViewItem.setMnemonic('R');
        reportsViewItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
        reportsViewItem.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        
        viewMenu.add(employeeViewItem);
        viewMenu.add(payrollViewItem);
        viewMenu.add(reportsViewItem);
        
        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        
        JMenuItem dbConnectionItem = new JMenuItem("Test Database Connection");
        dbConnectionItem.addActionListener(e -> testDatabaseConnection());
        
        toolsMenu.add(dbConnectionItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });
        
        // Tab change listener
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            String tabName = tabbedPane.getTitleAt(selectedIndex);
            updateStatus("Switched to " + tabName);
        });
    }
    
    // Removed createTabIcon method as we are not using JLabel as tab icon
    
    private void refreshCurrentPanel() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        
        switch (selectedIndex) {
            case 0:
                employeePanel.refreshData();
                break;
            case 1:
                payrollPanel.refreshData();
                break;
            case 2:
                reportsPanel.refreshData();
                break;
        }
        
        updateStatus("Data refreshed");
    }
    
    private void testDatabaseConnection() {
        updateStatus("Testing database connection...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return DatabaseConnection.testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Database connection successful!",
                            "Connection Test",
                            JOptionPane.INFORMATION_MESSAGE);
                        updateStatus("Database connection successful");
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Database connection failed!",
                            "Connection Test",
                            JOptionPane.ERROR_MESSAGE);
                        updateStatus("Database connection failed");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Database connection error: " + e.getMessage(),
                        "Connection Test",
                        JOptionPane.ERROR_MESSAGE);
                    updateStatus("Database connection error");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showAboutDialog() {
        String message = String.format(
            "%s\nVersion %s\n\n" +
            "A comprehensive payroll management system\n" +
            "built with Java Swing and MySQL.\n\n" +
            "Features:\n" +
            "• Employee Management\n" +
            "• Payroll Processing\n" +
            "• Report Generation\n" +
            "• User Authentication\n\n" +
            "Developed for efficient HR operations.",
            DatabaseConfig.APP_NAME,
            DatabaseConfig.APP_VERSION
        );
        
        JOptionPane.showMessageDialog(this,
            message,
            "About " + DatabaseConfig.APP_NAME,
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exitApplication() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            // Close database connection
            DatabaseConnection.closeConnection();
            
            // Exit application
            System.exit(0);
        }
    }
    
    public void updateStatus(String message) {
        statusLabel.setText(message);
        
        // Clear status message after 5 seconds
        Timer timer = new Timer(5000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
}