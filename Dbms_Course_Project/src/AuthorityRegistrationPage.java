import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorityRegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> regionComboBox;
    private JLabel authorityIdLabel;
    private JButton submitButton;
    private JButton backButton;

    public AuthorityRegistrationPage() {
        setTitle("Authority Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(new JLabel("Name:"), constraints);

        constraints.gridx = 1;
        nameField = new JTextField(20);
        panel.add(nameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(new JLabel("Phone Number:"), constraints);

        constraints.gridx = 1;
        phoneField = new JTextField(20);
        panel.add(phoneField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(new JLabel("Email:"), constraints);

        constraints.gridx = 1;
        emailField = new JTextField(20);
        panel.add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(new JLabel("Region:"), constraints);

        constraints.gridx = 1;
        regionComboBox = new JComboBox<>(loadRegionNamesFromDatabase().toArray(new String[0]));
        panel.add(regionComboBox, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(new JLabel("Authority ID:"), constraints);

        constraints.gridx = 1;
        authorityIdLabel = new JLabel();
        panel.add(authorityIdLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        submitButton = new JButton("Submit");
        panel.add(submitButton, constraints);

        backButton = new JButton("Back");
        constraints.gridy = 6;
        panel.add(backButton, constraints);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String region = (String) regionComboBox.getSelectedItem();
                int regionId = getRegionIdByName(region);
                if (regionId != -1) {
                    if (isRegionAlreadyExists(region)) {
                        JOptionPane.showMessageDialog(AuthorityRegistrationPage.this, "Region already exists.");
                    } else {
                        int generatedAuthorityID = insertAuthorityDetails(regionId);
                        if (generatedAuthorityID != -1) {
                            authorityIdLabel.setText(String.valueOf(generatedAuthorityID));
                            JOptionPane.showMessageDialog(AuthorityRegistrationPage.this, "Authority registration successful.");
                            clearFields();
                        } else {
                            JOptionPane.showMessageDialog(AuthorityRegistrationPage.this, "Error registering authority. Please try again.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(AuthorityRegistrationPage.this, "Selected region not found.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the back button action (e.g., go back to the previous page)
                RegistrationPage rp = new RegistrationPage();
                rp.setVisible(true);
                dispose();
            }
        });

        add(panel);
    }

    private List<String> loadRegionNamesFromDatabase() {
        List<String> regionNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT Name FROM region";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                regionNames.add(resultSet.getString("Name"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return regionNames;
    }

    private boolean isRegionAlreadyExists(String regionName) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT COUNT(*) FROM authority WHERE Region = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, regionName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    private int getRegionIdByName(String name) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT regionID FROM region WHERE Name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("regionID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return -1;
    }

    private int insertAuthorityDetails(int regionId) {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "INSERT INTO authority (Name, Phone, Email, Region, region_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, (String) regionComboBox.getSelectedItem());
            preparedStatement.setInt(5, regionId);
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return -1;
    }


    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        regionComboBox.setSelectedIndex(0);
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

                new AuthorityRegistrationPage().setVisible(true);
            }
        });
    }
}
