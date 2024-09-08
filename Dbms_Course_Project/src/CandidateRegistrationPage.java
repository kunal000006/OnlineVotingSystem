import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateRegistrationPage extends JFrame {
    private JTextField nameField;
    private JComboBox<String> partyComboBox;
    private JTextField otherPartyField;
    private JTextField birthDateField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> positionComboBox;
    private JTextField addressField;
    private JComboBox<String> regionComboBox;
    private JButton submitButton;
    private JLabel candidateIdLabel;
    private JButton backButton; // Back button added

    public CandidateRegistrationPage() {
        setTitle("Candidate Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel panel = new JPanel(new GridLayout(13, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Party:"));
        String[] parties = {"BJP", "NCP", "CONGRESS", "SHIVSENA", "Other"};
        partyComboBox = new JComboBox<>(parties);
        panel.add(partyComboBox);

        panel.add(new JLabel("Other Party:"));
        otherPartyField = new JTextField();
        panel.add(otherPartyField);

        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        birthDateField = new JTextField();
        panel.add(birthDateField);

        panel.add(new JLabel("Gender:"));
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        panel.add(genderComboBox);

        panel.add(new JLabel("Position:"));
        String[] positions = {"MP", "MLA"}; // Update position options
        positionComboBox = new JComboBox<>(positions);
        panel.add(positionComboBox);

        panel.add(new JLabel("Address:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Region (Maharashtra):"));
        List<String> regionNames = loadRegionNamesFromDatabase();
        regionComboBox = new JComboBox<>(regionNames.toArray(new String[0]));
        panel.add(regionComboBox);

        submitButton = new JButton("Submit");
        panel.add(submitButton);

        candidateIdLabel = new JLabel();
        panel.add(candidateIdLabel);

        backButton = new JButton("Back"); // Back button added
        panel.add(backButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long candidateId = insertCandidateDetails();
                candidateIdLabel.setText("Your Candidate ID: " + String.format("%04d", candidateId));
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the back button action (e.g., go back to the previous page)
                RegistrationPage rp = new RegistrationPage();
                rp.setVisible(true);
                dispose(); // Close the current window
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
            // Handle the database error appropriately
        }

        return regionNames;
    }

    private long insertCandidateDetails() {
        long candidateId = -1; // Initialize with an invalid value

        String name = nameField.getText();
        String party = (String) partyComboBox.getSelectedItem();
        String otherParty = otherPartyField.getText();
        String birthDateString = birthDateField.getText();

        // Validate birth date format (YYYY-MM-DD)
        if (!birthDateString.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Invalid birth date format. Please use YYYY-MM-DD.");
            return candidateId;
        }

        java.sql.Date birthDate = java.sql.Date.valueOf(birthDateString); // Convert to java.sql.Date
        String gender = (String) genderComboBox.getSelectedItem();
        String position = (String) positionComboBox.getSelectedItem();
        String address = addressField.getText();
        String regionName = (String) regionComboBox.getSelectedItem();
        int regionId = getRegionId(regionName); // Get the region_id based on the selected region

        // Calculate age based on the birth date
        long age = calculateAge(birthDate);

        // Check if the candidate's age is greater than 21
        if (age <= 21) {
            JOptionPane.showMessageDialog(this, "You are not eligible. Age must be greater than 21.");
            return candidateId;
        }

        // Check if a similar record already exists
        if (isSimilarRecordExists(party, position, regionId)) {
            JOptionPane.showMessageDialog(this, "You are not eligible as a candidate. A similar record already exists.");
            return candidateId;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "INSERT INTO candidates (name, party, birth_date, gender, position, address, region, age, region_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, party);
            preparedStatement.setDate(3, birthDate);
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, position);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, regionName); // Use regionName here
            preparedStatement.setLong(8, age);
            preparedStatement.setInt(9, regionId); // Insert region_id into the database
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                candidateId = generatedKeys.getLong(1);
            }

            JOptionPane.showMessageDialog(this, "Candidate registered successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering candidate. Please try again.");
        }

        return candidateId;
    }

    private int getRegionId(String regionName) {
        int regionId = -1;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT regionID FROM region WHERE Name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, regionName);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                regionId = resultSet.getInt("regionID");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the database error appropriately
        }

        return regionId;
    }

    private long calculateAge(java.sql.Date birthDate) {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
        long diffInMillies = Math.abs(currentDate.getTime() - birthDate.getTime());
        long diff = diffInMillies / (24 * 60 * 60 * 1000);
        return diff / 365;
    }

    private boolean isSimilarRecordExists(String party, String position, int regionId) {
        boolean exists = false;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT * FROM candidates WHERE party = ? AND position = ? AND region_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, party);
            preparedStatement.setString(2, position);
            preparedStatement.setInt(3, regionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                exists = true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Handle the database error appropriately
        }

        return exists;
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

                new CandidateRegistrationPage().setVisible(true);
            }
        });
    }
}
