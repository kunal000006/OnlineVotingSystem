import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class VoterRegistrationPage extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField birthDateField;
    private JTextField addressField;
    private JComboBox<String> genderComboBox;
    private JComboBox<String> regionComboBox;
    private JTextField phoneField;
    private JButton submitButton;
    private JButton backButton; // Back button added

    public VoterRegistrationPage() {
        setTitle("Voter Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        birthDateField = new JTextField();
        panel.add(birthDateField);

        panel.add(new JLabel("Address:"));
        addressField = new JTextField();
        panel.add(addressField);

        panel.add(new JLabel("Gender:"));
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        panel.add(genderComboBox);

        panel.add(new JLabel("Region:"));
        List<String> regionNames = loadRegionNamesFromDatabase();
        regionComboBox = new JComboBox<>(regionNames.toArray(new String[0]));
        panel.add(regionComboBox);

        panel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        panel.add(phoneField);

        submitButton = new JButton("Submit");
        panel.add(submitButton);

        backButton = new JButton("Back"); // Back button added
        panel.add(backButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertVoterDetails();
            }
        });

        backButton.addActionListener(new ActionListener() { // Back button action listener
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the back button action (e.g., go back to the previous page)
                RegistrationPage rp=new RegistrationPage();
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

    private void insertVoterDetails() {
        String name = nameField.getText();
        String email = emailField.getText();
        String birthDate = birthDateField.getText();
        String address = addressField.getText();
        String gender = (String) genderComboBox.getSelectedItem();
        String region = (String) regionComboBox.getSelectedItem();
        String phone = phoneField.getText();

        int age = calculateAge(birthDate);

        if (age < 18) {
            JOptionPane.showMessageDialog(this, "You must be 18 years or older to register for voting.");
            return;
        }

        int regionId = getRegionId(region);

        if (regionId == -1) {
            JOptionPane.showMessageDialog(this, "Invalid region selected.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "INSERT INTO voters (name, email, birth_date, address, gender, region_id, region, phone, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, birthDate);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, gender);
            preparedStatement.setInt(6, regionId);
            preparedStatement.setString(7, region);
            preparedStatement.setString(8, phone);
            preparedStatement.setInt(9, age);
            preparedStatement.executeUpdate();

            // Retrieve the generated voter ID
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            int voterId = -1;
            if (generatedKeys.next()) {
                voterId = generatedKeys.getInt(1);
            }

            if (voterId != -1) {
                // Display the Voter ID with a message
                JOptionPane.showMessageDialog(this, "This is your '" + String.format("%05d", voterId) + "' 5-digit Voter ID. Note it for future.");
            } else {
                JOptionPane.showMessageDialog(this, "Error registering voter. Please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering voter. Please try again.");
        }
    }

    private int calculateAge(String birthDate) {
        int age = 0;

        try {
            java.util.Date dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
            Calendar dob = Calendar.getInstance();
            dob.setTime(dateOfBirth);
            Calendar now = Calendar.getInstance();
            age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

            if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return age;
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
        }

        return regionId;
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

                new VoterRegistrationPage().setVisible(true);
            }
        });
    }
}
