package genai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignupScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signupButton;
    private JButton backButton;

    // Database connection
    private Connection connection;

    // Database URL, username, and password
    private static final String DB_URL = "jdbc:derby://localhost:1527/GenData";
    private static final String DB_USER = "vivek123";
    private static final String DB_PASSWORD = "vivek123";

    public SignupScreen() {
        // Connect to the database
        connectToDatabase();

        // Set up the frame
        setTitle("Sign Up for GenAI Chat");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create a panel with padding and a modern background color
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.decode("#F0F4F7"));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // GridBag constraints for custom layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add a title label
        JLabel titleLabel = new JLabel("Create a New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.decode("#333333"));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Add username field
        gbc.gridwidth = 1;
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        // Add password field
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        // Add confirm password field
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(15);
        panel.add(confirmPasswordField, gbc);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.decode("#F0F4F7"));
        backButton = new JButton("← Back");
        backButton.setBackground(Color.decode("#D9E1E8"));
        backButton.setForeground(Color.BLACK);
        signupButton = new JButton("Sign Up");
        signupButton.setBackground(Color.decode("#4CAF50"));
        signupButton.setForeground(Color.WHITE);
        buttonPanel.add(backButton);
        buttonPanel.add(signupButton);

        // Add panels to the frame
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Back button action listener to go back to MainPage
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new WelcomePage().setVisible(true);
                dispose(); // Close the signup window
            }
        });

        // Add button click handler for signup
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // Check if password meets length requirement
                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(SignupScreen.this, "Password must be at least 8 characters long!");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SignupScreen.this, "Passwords do not match!");
                    return;
                }

                // Encrypt the password and register user
                String encryptedPwd = encryptPassword(password);
                //System.out.println("Encrypted password: " + encryptedPwd);

                if (registerUser(username, encryptedPwd)) {
                    JOptionPane.showMessageDialog(SignupScreen.this, "Signup successful! You can now log in.");
                    openLoginWindow();
                } else {
                    JOptionPane.showMessageDialog(SignupScreen.this, "Signup failed. Please try again.");
                }
            }
        });
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean registerUser(String username, String encryptedPwd) {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection is not available.");
            return false;
        }

        try {
            String sql = "INSERT INTO USERS (USERNAME, PASSWORD) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, encryptedPwd);
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openLoginWindow() {
        this.dispose();
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
    }

    private String encryptPassword(String password) {
        StringBuilder encryptedPwd = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);
            char encryptedChar = (i % 2 == 0) ? (char) (currentChar + 2) : (char) (currentChar + 1);
            encryptedPwd.append(encryptedChar);
        }
        return encryptedPwd.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SignupScreen().setVisible(true);
            }
        });
    }
}
