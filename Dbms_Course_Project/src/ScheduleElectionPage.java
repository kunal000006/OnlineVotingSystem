import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleElectionPage extends JFrame {
    private JComboBox<String> positionComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton scheduleButton;
    private JButton backButton; // Added back button
    private JLabel regionLabel;

    private final String DB_URL = "jdbc:mysql://localhost/dbmscp";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "Kunal06nda@";
    private final String loggedInRegion;

    public ScheduleElectionPage(String loggedInRegion) {
        this.loggedInRegion = loggedInRegion;
        setTitle("Schedule Election");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        regionLabel = new JLabel("Region: " + loggedInRegion);
        regionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(regionLabel, constraints);

        constraints.gridwidth = 1;

        panel.add(new JLabel("Position:"));
        String[] positions = {"MP", "MLA"}; // Update position options to MP and MLA
        positionComboBox = new JComboBox<>(positions);
        panel.add(positionComboBox);

        panel.add(new JLabel("Start Date (YYYY-MM-DD HH:mm):"));
        startDateField = new JTextField(20);
        panel.add(startDateField);

        panel.add(new JLabel("End Date (YYYY-MM-DD HH:mm):"));
        endDateField = new JTextField(20);
        panel.add(endDateField);

        scheduleButton = new JButton("Schedule Election");
        panel.add(scheduleButton);

        backButton = new JButton("Back"); // Initialize the back button
        panel.add(backButton);

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scheduleElection();
            }
        });

        // Add ActionListener for the back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorityPage authorityPage = new AuthorityPage();
                authorityPage.setVisible(true);
                dispose(); // Close the current window
            }
        });

        add(panel);
    }

    private void scheduleElection() {
        String position = (String) positionComboBox.getSelectedItem();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check for existing elections with overlapping time intervals
            String checkSql = "SELECT COUNT(*) FROM elections WHERE region = ? AND position = ?" +
                    " AND ((? BETWEEN start_date AND end_date) OR (? BETWEEN start_date AND end_date))";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, loggedInRegion);
            checkStmt.setString(2, position);
            checkStmt.setString(3, startDate);
            checkStmt.setString(4, endDate);
            ResultSet resultSet = checkStmt.executeQuery();
            resultSet.next();
            int overlappingElectionsCount = resultSet.getInt(1);

            if (overlappingElectionsCount == 0) {
                // If there are no overlapping elections, proceed to schedule
                String sql = "INSERT INTO elections (region, position, start_date, end_date) VALUES (?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, loggedInRegion);
                preparedStatement.setString(2, position);
                preparedStatement.setString(3, startDate);
                preparedStatement.setString(4, endDate);
                preparedStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Election Scheduled Successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Cannot schedule. There is an overlapping election for the same region and position.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error scheduling election. Please try again.");
        }
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

                new ScheduleElectionPage("Pune").setVisible(true); // Example region name
            }
        });
    }
}
