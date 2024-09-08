import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoterLoginPage extends JFrame {
    private JTextField nameField;
    private JTextField voterIdField;
    private JButton loginButton;

    public VoterLoginPage() {
        setTitle("Voter Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton backButton = new JButton("Back");
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setOpaque(false);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(backButton, constraints);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginPage lp = new LoginPage();
                lp.setVisible(true);
                dispose();
            }
        });

        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Name:"), constraints);

        constraints.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Voter ID:"), constraints);

        constraints.gridx = 1;
        voterIdField = new JTextField(20);
        panel.add(voterIdField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        loginButton = new JButton("Login");
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String voterIdText = voterIdField.getText();

                if (name.isEmpty() || voterIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(VoterLoginPage.this, "Please enter both name and voter ID.");
                    return;
                }

                try {
                    int voterId = Integer.parseInt(voterIdText);
                    int voterIdFromLogin = getVoterId(name, voterId); // Get the voter ID
                    if (voterIdFromLogin != -1) {
                        // Pass the region name to the next page (GiveYourVotePage)
                        String region = getRegionForVoterId(voterId);
                        new VoterPage(region,voterId).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(VoterLoginPage.this, "Invalid name or voter ID.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(VoterLoginPage.this, "Invalid voter ID format.");
                }
            }
        });

        add(panel);
    }

    private int getVoterId(String name, int voterId) {
        // Replace with your database connection details and SQL query
        String DB_URL = "jdbc:mysql://localhost/dbmscp";
        String DB_USER = "root";
        String DB_PASSWORD = "Kunal06nda@";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT voter_id FROM voters WHERE name = ? AND voter_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, voterId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("voter_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Voter not found or authentication failed
    }

    private String getRegionForVoterId(int voterId) {
        // Replace with your database connection details and SQL query to get the region for the voter ID
        String DB_URL = "jdbc:mysql://localhost/dbmscp";
        String DB_USER = "root";
        String DB_PASSWORD = "Kunal06nda@";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT region FROM Voters WHERE voter_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, voterId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("region");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null; // Region not found for the voter ID
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new VoterLoginPage().setVisible(true);
            }
        });
    }
}
