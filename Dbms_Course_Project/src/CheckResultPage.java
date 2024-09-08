import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CheckResultPage extends JFrame {
    private String region;
    private int voterID;
    private Connection connection;
    private JLabel mpWinnerLabel;
    private JLabel mlaWinnerLabel;

    public CheckResultPage(String region, int voterID) {
        this.region = region;
        this.voterID = voterID;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbmscp", "root", "Kunal06nda@");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            System.exit(1);
        }

        setTitle("Check Result Page");
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
                VoterPage vp = new VoterPage(region, voterID);
                vp.setVisible(true);
                dispose();
            }
        });

        JLabel titleLabel = new JLabel("Check Result Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        mpWinnerLabel = new JLabel("MP Election Winner: ");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(mpWinnerLabel, constraints);

        mlaWinnerLabel = new JLabel("MLA Election Winner: ");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(mlaWinnerLabel, constraints);

        add(panel);

        // Use a Timer to periodically check for election completion and update the winners
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndUpdateWinners();
            }
        }, 0, 5000); // Check every 5 seconds

        // Initial update
        checkAndUpdateWinners();
    }

    private void checkAndUpdateWinners() {
        // Query to find the last MP election for the region
        int lastMPElectionID = getLastElectionID("MP");

        // Query to find the last MLA election for the region
        int lastMLAElectionID = getLastElectionID("MLA");

        if (isElectionFinished(lastMPElectionID)) {
            String mpWinner = getWinner(lastMPElectionID, "MP");
            mpWinnerLabel.setText("MP Election Winner: " + mpWinner);
        } else {
            mpWinnerLabel.setText("MP Election Winner: (Not Available)");
        }

        if (isElectionFinished(lastMLAElectionID)) {
            String mlaWinner = getWinner(lastMLAElectionID, "MLA");
            mlaWinnerLabel.setText("MLA Election Winner: " + mlaWinner);
        } else {
            mlaWinnerLabel.setText("MLA Election Winner: (Not Available)");
        }
    }

    private int getLastElectionID(String position) {
        try {
            // Check if 'position' column exists in the 'elections' table
            DatabaseMetaData metadata = connection.getMetaData();
            ResultSet columns = metadata.getColumns(null, null, "elections", "position");

            if (columns.next()) {
                // 'position' column exists, execute the original query
                String sql = "SELECT MAX(election_id) AS last_election_id " +
                        "FROM elections " +
                        "WHERE region = ? AND position = ?";

                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, region);
                preparedStatement.setString(2, position);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt("last_election_id");
                }
            } else {
                // 'position' column does not exist, handle gracefully
                System.err.println("'position' column does not exist in the 'elections' table.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private boolean isElectionFinished(int electionID) {
        if (electionID == -1) {
            return false;
        }

        try {
            String sql = "SELECT end_date FROM elections WHERE election_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Timestamp endDate = resultSet.getTimestamp("end_date");
                if (endDate != null) {
                    // Check if the end date is in the past
                    Date currentDate = new Date();
                    return endDate.before(currentDate);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getWinner(int electionID, String position) {
        try {
            String sql = "SELECT candidate_name, COUNT(*) AS vote_count " +
                    "FROM votes " +
                    "WHERE election_id = ? " +
                    "GROUP BY candidate_name " +
                    "ORDER BY vote_count DESC " +
                    "LIMIT 1";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String winner = resultSet.getString("candidate_name");
                int maxVotes = resultSet.getInt("vote_count");

                // Check for tie scenario
                if (maxVotes > 1) {
                    String sqlTie = "SELECT candidate_name " +
                            "FROM votes " +
                            "WHERE election_id = ? " +
                            "GROUP BY candidate_name " +
                            "HAVING COUNT(*) = ?";

                    PreparedStatement preparedStatementTie = connection.prepareStatement(sqlTie);
                    preparedStatementTie.setInt(1, electionID);
                    preparedStatementTie.setInt(2, maxVotes);
                    ResultSet resultSetTie = preparedStatementTie.executeQuery();

                    StringBuilder tieCandidates = new StringBuilder();
                    while (resultSetTie.next()) {
                        tieCandidates.append(resultSetTie.getString("candidate_name")).append(", ");
                    }

                    if (tieCandidates.length() > 0) {
                        tieCandidates.setLength(tieCandidates.length() - 2); // Remove the trailing comma and space
                        winner = "Tie between: " + tieCandidates.toString();
                    }
                }

                return winner;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "No Winner Found";
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

                new CheckResultPage("Mumbai", 1).setVisible(true);
            }
        });
    }
}
