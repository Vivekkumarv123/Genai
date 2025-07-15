package genai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePage extends JFrame {
    private final JButton loginButton;
    private final JButton signupButton;

    public WelcomePage() {
        // Set up the frame
        setTitle("GenAI Chat Application");
        setSize(600, 450); // Increased size for better spacing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(new BorderLayout());

        // Set background color for the content pane
        getContentPane().setBackground(Color.WHITE);

        // Create a title label (GenAI Name)
        JLabel titleLabel = new JLabel("GenAI: Your Smart Chat Companion", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 51, 102)); // Dark blue
        add(titleLabel, BorderLayout.NORTH);

        // Create a quote label
        JLabel quoteLabel = new JLabel("\"Empowering conversations with AI\"", SwingConstants.CENTER);
        quoteLabel.setFont(new Font("Serif", Font.ITALIC, 16));
        quoteLabel.setForeground(Color.GRAY);
        add(quoteLabel, BorderLayout.CENTER);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); // Padding around buttons
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Create login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 50));
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(0, 153, 76)); // Green
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false); // Remove focus border
        buttonPanel.add(loginButton, gbc);

        // Create signup button
        signupButton = new JButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(150, 50));
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setBackground(new Color(0, 102, 204)); // Blue
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false); // Remove focus border
        gbc.gridx = 1;
        buttonPanel.add(signupButton, gbc);

        // Add button panel to the frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners to the buttons
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoginWindow();
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignupWindow();
            }
        });
    }

    private void openLoginWindow() {
        // Open the login screen
        this.dispose(); // Close the main page
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.setVisible(true);
    }

    private void openSignupWindow() {
        // Open the signup screen
        this.dispose(); // Close the main page
        SignupScreen signupScreen = new SignupScreen();
        signupScreen.setVisible(true);
    }

    public static void main(String[] args) {
        // Run the main page
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WelcomePage().setVisible(true);
            }
        });
    }
}
