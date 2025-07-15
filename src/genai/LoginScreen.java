package genai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton backButton; // Back button to go back to WelcomePage

    // Database connection
    private Connection connection;
    
    // Database URL, username, and password
    private static final String DB_URL = "jdbc:derby://localhost:1527/GenData";
    private static final String DB_USER = "vivek123";
    private static final String DB_PASSWORD = "vivek123";

    public LoginScreen() {
        // Connect to the database
        connectToDatabase();

        // Set up the frame
        setTitle("Login to GenAI Chat");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());
        setUndecorated(false); // Optional: removes window border if set to true

        // Create a panel for the form
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Add padding
        panel.setBackground(new Color(235, 240, 255)); // Light background color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Add spacing between components

        // Add a header label
        JLabel headerLabel = new JLabel("Login to GenAI Chat");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(50, 50, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(headerLabel, gbc);

        // Add username label and field
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameField, gbc);

        // Add password label and field
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordField, gbc);

        // Add login button
        loginButton = new JButton("Login");
        loginButton.setBackground(new Color(50, 50, 150));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));

        // Create a panel for buttons (Back and Login)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(235, 240, 255)); // Same background as form

        backButton = new JButton("← Back"); // Text-based back button
        backButton.setFont(new Font("Arial", Font.PLAIN, 16));

        buttonPanel.add(backButton);
        buttonPanel.add(loginButton);

        // Add panels to the frame
        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH); // Add button panel at the bottom

        // Back button action listener to go back to MainPage
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to MainPage
                new WelcomePage().setVisible(true);
                dispose(); // Close the login window
            }
        });

        // Login button click handler
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Encrypt the password as per the encryption scheme
                String encryptedPassword = encryptPassword(password);

                if (validateLogin(username, encryptedPassword)) {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Login successful!");
                    // Open chat window and pass the username
                    openChatWindow(username);
                } else {
                    JOptionPane.showMessageDialog(LoginScreen.this, "Invalid username or password.");
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

    private boolean validateLogin(String username, String encryptedPassword) {
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection is not available.");
            return false;
        }

        try {
            String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, encryptedPassword);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next(); // If a result is found, login is successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void openChatWindow(String username) {
        // Pass the username to the chat screen
        this.dispose(); // Close the login window
        GenAI chatScreen = new GenAI(username); // Pass username to GenAI constructor
        chatScreen.setVisible(true);
    }

    private String encryptPassword(String password) {
        StringBuilder encryptedPwd = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            char currentChar = password.charAt(i);
            char encryptedChar;
            if (i % 2 == 0) {
                encryptedChar = (char) (currentChar + 2);
                encryptedPwd.append(encryptedChar);
            } else {
                encryptedChar = (char) (currentChar + 1);
                encryptedPwd.append(encryptedChar);
            }
        }
        return encryptedPwd.toString();
    }

    public static void main(String[] args) {
        // Run the login screen
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}
