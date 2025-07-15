package genai;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenAI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton newChatButton;
    private JList<String> chatList;
    private DefaultListModel<String> listModel;
    private JLabel profileIcon;
    private List<String> chatHistory = new ArrayList<>();
    private List<String> savedChats = new ArrayList<>(); // Store previous chats
    private Connection connection;
    private String username; // Store username

    public GenAI(String username) {
        this.username = username;

        // Initialize database connection
        initializeDatabase();

        // Set up the frame
        setTitle("GenAI Chat");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel for previous chats and profile
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(new Color(230, 230, 230)); // Light gray background

        // Profile icon with a more modern look
        profileIcon = new JLabel("👤"); // Use an icon
        profileIcon.setPreferredSize(new Dimension(40, 40));
        profileIcon.setHorizontalAlignment(JLabel.CENTER);
        profileIcon.setVerticalAlignment(JLabel.CENTER);
        profileIcon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around icon
        sidebarPanel.add(profileIcon, BorderLayout.NORTH);

        // Add a popup menu for the profile icon (with logout option)
        JPopupMenu profileMenu = new JPopupMenu();
        JMenuItem logoutItem = new JMenuItem("Logout");
        profileMenu.add(logoutItem);

        profileIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) || SwingUtilities.isLeftMouseButton(e)) {
                    profileMenu.show(profileIcon, e.getX(), e.getY()); // Show the menu at the click location
                }
            }
        });

        // Handle logout click event
        logoutItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(null, "Logging out...");
                dispose(); // Close the application
                new LoginScreen().setVisible(true); // Return to login screen (optional)
            }
        });

        // List of previous chats
        listModel = new DefaultListModel<>();
        chatList = new JList<>(listModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane chatListScrollPane = new JScrollPane(chatList);
        sidebarPanel.add(chatListScrollPane, BorderLayout.CENTER);

        // New chat button at the bottom of the sidebar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        newChatButton = new JButton("New Chat");
        newChatButton.setIcon(new ImageIcon("path/to/new-chat-icon.png")); // Use an icon
        newChatButton.setPreferredSize(new Dimension(150, 40));
        newChatButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        newChatButton.setForeground(Color.WHITE);
        newChatButton.setFont(new Font("Arial", Font.BOLD, 14));
        newChatButton.setFocusPainted(false); // Remove focus border
        buttonPanel.add(newChatButton);
        sidebarPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Create chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Border around chat area
        chatArea.setBackground(new Color(245, 245, 245)); // Light gray background

        // Create input field and send button
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(34, 139, 34)); // Forest green
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setPreferredSize(new Dimension(100, 40));
        sendButton.setFocusPainted(false); // Remove focus border

        // Panel for input field and send button
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the frame
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(sidebarPanel, BorderLayout.WEST);

        // Handle send button click or pressing Enter
        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        };
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction); // Enables sending by pressing Enter

        // Handle new chat button click
        newChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chatArea.getText().isEmpty()) {
                    // Move current chat to sidebar history
                    savedChats.add(chatArea.getText()); // Save current chat
                    listModel.addElement("Chat " + (listModel.size() + 1)); // Add new chat to sidebar
                    // Save current chat to the database
                    saveChatToDatabase(username, chatArea.getText()); // Replace "User" with actual username
                }
                chatArea.setText(""); // Clear current chat area
            }
        });

        // Add a ListSelectionListener to chatList to open previous chats
        chatList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // Prevent double triggering
                int selectedIndex = chatList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < savedChats.size()) {
                    chatArea.setText(savedChats.get(selectedIndex)); // Load the selected chat
                }
            }
        });
    }

    private void initializeDatabase() {
        try {
            // Connect to the Derby database
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/GenData", "vivek123", "vivek123");
            System.out.println("Connected to the database.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChatToDatabase(String username, String message) {
        String query = "INSERT INTO ChatMessages (username, message) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to save chat: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        String userInput = inputField.getText();
        if (!userInput.isEmpty()) {
            inputField.setText("");
            chatHistory.add("You: " + userInput);
            chatArea.append("You: " + userInput + "\n");

            // Create a new thread to handle the Node.js script call asynchronously
            new Thread(() -> {
                try {
                    String response = sendInputToNodeJs(userInput); // Call your Node.js script
                    SwingUtilities.invokeLater(() -> {
                        chatHistory.add("Shinchan: " + response);
                        chatArea.append("Shinchan: " + response + "\n");
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                    chatArea.append("Error: Could not connect to the Node.js backend.\n");
                }
            }).start();
        }
    }

    private String sendInputToNodeJs(String userInput) throws IOException {
        String scriptPath = "C:\\vivekkumar_2304\\GenAI\\src\\genai\\genai_chat.js";
        ProcessBuilder pb = new ProcessBuilder("node", scriptPath, userInput);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read response from the Node.js script
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString().trim();
        }
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        WelcomePage welcomePage = new WelcomePage();  // Open the welcome page
        welcomePage.setVisible(true);
    });
}
}
