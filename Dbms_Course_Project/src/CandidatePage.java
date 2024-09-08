import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

public class CandidatePage extends JFrame {
    private int candidateId;
    private Connection connection;

    public CandidatePage(int candidateId) {
        this.candidateId = candidateId;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbmscp", "root", "Kunal06nda@");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            System.exit(1);
        }

        setTitle("Candidate Page");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
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

        backButton.addActionListener(e -> {
            LoginPage lp = new LoginPage();
            lp.setVisible(true);
            dispose(); // Close the current window
        });

        JLabel titleLabel = new JLabel("Candidate Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, constraints);

        JLabel electionStatusLabel = new JLabel("Election Status: ");
        JLabel liveVotingLabel = new JLabel("Live Voting: ");
        JLabel electionResultsLabel = new JLabel("Election Results: ");

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(electionStatusLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(liveVotingLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(electionResultsLabel, constraints);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateElectionStatus(electionStatusLabel);
                updateLiveVotingStatus(liveVotingLabel);
                updateElectionResults(electionResultsLabel);
            }
        }, 0, 5000); // Update every 5 seconds

        add(panel);
    }

    private void updateElectionStatus(JLabel label) {
        try {
            String sql = "SELECT status FROM elections WHERE region = ? AND position = ? ORDER BY end_date DESC LIMIT 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "Mumbai"); // Replace with the correct region
            preparedStatement.setString(2, "MP"); // Replace with the correct position
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String status = resultSet.getString("status");
                label.setText("Election Status: " + status);
            } else {
                label.setText("Election Status: No election data available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLiveVotingStatus(JLabel label) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new java.util.Date());

            String sql = "SELECT * FROM elections WHERE region = ? AND position = ? AND start_date <= ? AND end_date >= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, "Mumbai"); // Replace with the correct region
            preparedStatement.setString(2, "MP"); // Replace with the correct position
            preparedStatement.setString(3, currentTime);
            preparedStatement.setString(4, currentTime);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                label.setText("Live Voting: Yes");
            } else {
                label.setText("Live Voting: No");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateElectionResults(JLabel label) {
        try {
            // Find the last election for the candidate's region and position
            String sql = "SELECT election_id FROM elections " +
                    "WHERE region = ? AND position = ? " +
                    "ORDER BY end_date DESC LIMIT 1";
            PreparedStatement electionStatement = connection.prepareStatement(sql);
            electionStatement.setString(1, "Mumbai"); // Replace with the correct region
            electionStatement.setString(2, "MP"); // Replace with the correct position
            ResultSet electionResult = electionStatement.executeQuery();

            if (electionResult.next()) {
                int lastElectionId = electionResult.getInt("election_id");

                // Check if the election result is available
                if (isElectionResultAvailable(lastElectionId)) {
                    // Get the vote count for the candidate
                    int candidateVoteCount = getCandidateVoteCount(lastElectionId, candidateId);

                    int maxVotes = getMaxVotes(lastElectionId);

                    if (candidateVoteCount == maxVotes) {
                        label.setText("Election Results: You WON with " + candidateVoteCount + " votes!");
                    } else {
                        label.setText("Election Results: You Lost with " + candidateVoteCount + " votes.");
                    }
                } else {
                    // Election result is not available
                    label.setText("You lost");
                }
            } else {
                label.setText("Election Results: Not Declared Yet");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isElectionResultAvailable(int electionId) {
        try {
            // Check if the election result is available by counting votes for the candidate in the election
            String sql = "SELECT COUNT(*) AS vote_count FROM votes WHERE election_id = ? AND candidate_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionId);
            preparedStatement.setInt(2, candidateId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int voteCount = resultSet.getInt("vote_count");
                return voteCount > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getCandidateVoteCount(int electionId, int candidateId) {
        try {
            // Count the votes for the candidate in the specified election
            String sql = "SELECT COUNT(*) AS vote_count FROM votes WHERE election_id = ? AND candidate_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionId);
            preparedStatement.setInt(2, candidateId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("vote_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getMaxVotes(int electionId) {
        try {
            String sql = "SELECT MAX(vote_count) AS max_votes FROM (SELECT COUNT(*) AS vote_count " +
                    "FROM votes WHERE election_id = ? GROUP BY candidate_id) AS vote_counts";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("max_votes");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new CandidatePage(17).setVisible(true); // Pass the candidate ID here
        });
    }
}
