import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AuthorityLoginPage extends JFrame {
    private JTextField authorityIdField;
    private JTextField nameField;
    private JButton loginButton;
    private JButton backButton; // Back button
    private String loggedInRegion; // Store the logged-in authority's region
    private String loggedInName; // Store the logged-in authority's name

    public AuthorityLoginPage() {
        setTitle("Authority Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Back button
        backButton = new JButton("Back");
        backButton.setBorderPainted(false); // Remove button border
        backButton.setContentAreaFilled(false); // Remove button background
        backButton.setForeground(Color.BLUE); // Set button text color
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(backButton, constraints);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle back button action, e.g., return to the previous page
                // Here, we can simply close the current page for demonstration purposes
                LoginPage lp=new LoginPage();
                lp.setVisible(true);
                dispose();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3; // Span three columns for the login components
        panel.add(new JLabel("Authority ID:"), constraints);

        constraints.gridy = 2;
        authorityIdField = new JTextField(20);
        panel.add(authorityIdField, constraints);

        constraints.gridy = 3;
        panel.add(new JLabel("Name:"), constraints);

        constraints.gridy = 4;
        nameField = new JTextField(20);
        panel.add(nameField, constraints);

        constraints.gridy = 5;
        constraints.gridwidth = 3;
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(Color.WHITE); // Set button background to white
        loginButton.setForeground(Color.BLACK); // Set button text color to black
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String authorityIdText = authorityIdField.getText();
                String name = nameField.getText();

                try {
                    int authorityId = Integer.parseInt(authorityIdText);

                    if (login(authorityId, name)) {
                        JOptionPane.showMessageDialog(AuthorityLoginPage.this, "Login successful.");
                        // Proceed to the next page or perform actions based on loggedInRegion
                        // For example, you can open the ScheduleElectionPage with loggedInRegion.
                        new ScheduleElectionPage(loggedInRegion).setVisible(true);
                        dispose(); // Close the login page
                    } else {
                        JOptionPane.showMessageDialog(AuthorityLoginPage.this, "Login failed. Please check your Authority ID and Name.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(AuthorityLoginPage.this, "Invalid Authority ID.");
                }
            }
        });

        add(panel);
    }

    // Method to perform the login logic and retrieve authority information
    private boolean login(int authorityId, String name) {
        // Replace these with your actual database connection details
        String DB_URL = "jdbc:mysql://localhost/dbmscp";
        String DB_USER = "root";
        String DB_PASSWORD = "Kunal06nda@";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT Region, Name FROM authority WHERE Authority_ID = ? AND Name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, authorityId);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                loggedInRegion = resultSet.getString("Region");
                loggedInName = resultSet.getString("Name");
                return true; // Login successful
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false; // Login failed
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Use the system's default look and feel for a professional appearance
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new AuthorityLoginPage().setVisible(true);
            }
        });
    }
}
