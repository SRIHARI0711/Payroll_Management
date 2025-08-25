package gui;

import config.DatabaseConfig;
import dao.UserDAO;
import models.User;
import utils.DatabaseConnection;
import utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Login frame for user authentication
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Test database connection on startup
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to database. Please check your database configuration.", 
                "Database Connection Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeComponents() {
        setTitle(DatabaseConfig.APP_NAME + " - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Create components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        
        // Set component properties
        loginButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setPreferredSize(new Dimension(100, 30));
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title
        JLabel titleLabel = new JLabel(DatabaseConfig.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(51, 102, 153));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please enter your credentials");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);
        
        // Version info
        JLabel versionLabel = new JLabel("Version " + DatabaseConfig.APP_VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        versionLabel.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(versionLabel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Pack and center
        pack();
        setLocationRelativeTo(null);
    }
    
    private void setupEventHandlers() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Exit button action
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Enter key listener for password field
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        // Enter key listener for username field
        usernameField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocus();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }
    
    private void performLogin() {
        String username = ValidationUtils.sanitizeInput(usernameField.getText());
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (ValidationUtils.isNullOrEmpty(username)) {
            showError("Please enter your username.");
            usernameField.requestFocus();
            return;
        }
        
        if (ValidationUtils.isNullOrEmpty(password)) {
            showError("Please enter your password.");
            passwordField.requestFocus();
            return;
        }
        
        // Show loading cursor
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        loginButton.setEnabled(false);
        
        // Perform authentication in background thread
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return userDAO.authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    
                    // Reset cursor and button
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                    
                    if (user != null) {
                        // Login successful
                        dispose();
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(user).setVisible(true);
                        });
                    } else {
                        // Login failed
                        showError("Invalid username or password.");
                        passwordField.setText("");
                        usernameField.requestFocus();
                    }
                } catch (Exception e) {
                    setCursor(Cursor.getDefaultCursor());
                    loginButton.setEnabled(true);
                    showError("Login failed: " + e.getMessage());
                }
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}