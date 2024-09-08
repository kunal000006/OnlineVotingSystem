import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MLAElectionPage extends JFrame {
    private JLabel regionLabel;
    private JLabel statusLabel;
    private JTable candidatesTable;
    private DefaultTableModel tableModel;
    private JButton voteButton;
    private JTextField voterIdField;
    private JLabel remainingTimeLabel;
    private JButton backButton; // Back button

    private final String DB_URL = "jdbc:mysql://localhost/dbmscp";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "Kunal06nda@";
    private String currentRegion;
    private int voterId;
    private TableColumn checkboxColumn;
    private Set<Integer> votedElectionIds = new HashSet<>();

    public MLAElectionPage(String region, int voterId) {
        currentRegion = region;
        this.voterId = voterId;
        setTitle("MLA Election");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        regionLabel = new JLabel("Region: " + region);
        regionLabel.setFont(new Font("Arial", Font.BOLD, 24));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(regionLabel, constraints);

        constraints.gridwidth = 1;

        statusLabel = new JLabel("Election Status: Unknown");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        panel.add(statusLabel, constraints);

        remainingTimeLabel = new JLabel("Remaining Time: N/A");
        remainingTimeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHEAST;
        panel.add(remainingTimeLabel, constraints);

        constraints.insets = new Insets(0, 10, 0, 0);
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(regionLabel, constraints);

        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("Candidate Name");
        tableModel.addColumn("Party");
        tableModel.addColumn("Select");

        class ButtonRenderer extends JButton implements TableCellRenderer {
            public ButtonRenderer() {
                setOpaque(true);
            }

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                setText((value == null) ? "" : value.toString());
                return this;
            }
        }

        candidatesTable = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 2) {
                    return new ButtonRenderer();
                }
                return super.getCellRenderer(row, column);
            }
        };

        TableCellRenderer renderer = new DefaultTableCellRenderer() {
            private final JCheckBox checkBox = new JCheckBox();

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                checkBox.setSelected((Boolean) value);
                checkBox.setHorizontalAlignment(JCheckBox.CENTER);
                return checkBox;
            }
        };

        TableCellEditor editor = new DefaultCellEditor(new JCheckBox()) {
            private final JCheckBox checkBox = new JCheckBox();

            @Override
            public Component getTableCellEditorComponent(
                    JTable table, Object value, boolean isSelected, int row, int column) {
                checkBox.setSelected((Boolean) value);
                return checkBox;
            }

            @Override
            public Object getCellEditorValue() {
                return checkBox.isSelected();
            }
        };

        checkboxColumn = candidatesTable.getColumnModel().getColumn(2);
        checkboxColumn.setCellRenderer(renderer);
        checkboxColumn.setCellEditor(editor);

        candidatesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int selectedRow = candidatesTable.getSelectedRow();
                if (selectedRow != -1) {
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (i == selectedRow) {
                            tableModel.setValueAt(true, i, 2);
                        } else {
                            tableModel.setValueAt(false, i, 2);
                        }
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(candidatesTable);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(tableScrollPane, constraints);
        candidatesTable.setVisible(false);

        voteButton = new JButton("Vote");
        voteButton.setFont(new Font("Arial", Font.BOLD, 16));
        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                voteForSelectedCandidate();
            }
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel.add(voteButton, constraints);

        voterIdField = new JTextField("Voter ID: " + voterId);
        voterIdField.setEditable(false);
        voterIdField.setFont(new Font("Arial", Font.BOLD, 16));
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(voterIdField, constraints);

        // Create a Back button
        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToGiveYourVotePage(region, voterId); // Navigate back to CheckResultPage
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.NONE;
        panel.add(backButton, constraints);

        add(panel);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                boolean isRunning = isElectionRunning(region, "MLA");
                if (isRunning) {
                    java.util.List<String[]> candidatesData = getCandidates(region, "MLA");
                    if (!candidatesData.isEmpty()) {
                        updateTable(candidatesData);
                    } else {
                        clearTable();
                    }
                    candidatesTable.setVisible(true);
                    statusLabel.setText("Election Status: Running");
                } else {
                    clearTable();
                    candidatesTable.setVisible(false);
                    statusLabel.setText("Election Status: Not Running");
                }

                updateRemainingTimeLabel(region, "MLA");
            }
        }, 0, 10000);

        // Initialize the set of voted election IDs for the current voter
        initializeVotedElectionIds();
    }

    private boolean isElectionRunning(String region, String position) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date currentDate = new java.util.Date();
            String currentDateTime = dateFormat.format(currentDate);

            String sql = "SELECT COUNT(*) FROM elections WHERE region = ? AND position = ? " +
                    "AND ? BETWEEN start_date AND end_date";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, region);
            preparedStatement.setString(2, position);
            preparedStatement.setString(3, currentDateTime);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int ongoingElectionsCount = resultSet.getInt(1);

            return ongoingElectionsCount > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private java.util.List<String[]> getCandidates(String region, String position) {
        java.util.List<String[]> candidatesList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT name, party FROM candidates WHERE region = ? AND position = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, region);
            preparedStatement.setString(2, position);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String candidateName = resultSet.getString("name");
                String partyName = resultSet.getString("party");
                candidatesList.add(new String[]{candidateName, partyName});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return candidatesList;
    }

    private void updateTable(java.util.List<String[]> candidatesData) {
        clearTable();

        for (String[] rowData : candidatesData) {
            Object[] newRow = {rowData[0], rowData[1], false};
            tableModel.addRow(newRow);
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    private void voteForSelectedCandidate() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            boolean selected = (boolean) tableModel.getValueAt(i, 2);
            if (selected) {
                String candidateName = (String) tableModel.getValueAt(i, 0);
                String partyName = (String) tableModel.getValueAt(i, 1);

                // Check if the voter has already voted in this election
                if (hasVoterAlreadyVotedInElection(currentRegion)) {
                    JOptionPane.showMessageDialog(this, "You have already voted in this election.");
                } else {
                    // Call the vote method to handle the vote
                    vote(candidateName, partyName);
                    return; // Exit the loop after voting for the selected candidate
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Please select a candidate to vote for.");
    }

    private void vote(String candidateName, String partyName) {
        // Display a dialog to confirm the vote
        int option = JOptionPane.showConfirmDialog(this, "Confirm your vote for " + candidateName + " from " + partyName + "?", "Vote Confirmation", JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            // Retrieve the election_id
            int electionId = getElectionId(currentRegion, "MLA"); // Use the stored current region

            if (electionId != -1) {
                // Insert vote details into the votes table with the correct election_id
                insertVoteDetails(candidateName, partyName, electionId);
                voteButton.setEnabled(false); // Disable the vote button after voting
            } else {
                JOptionPane.showMessageDialog(this, "No eligible election found.");
            }
        }
    }

    private int getElectionId(String region, String position) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date currentDate = new java.util.Date();
            String currentDateTime = dateFormat.format(currentDate);

            String sql = "SELECT election_id FROM elections WHERE region = ? AND position = ? " +
                    "AND ? BETWEEN start_date AND end_date";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, region);
            preparedStatement.setString(2, position);
            preparedStatement.setString(3, currentDateTime);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("election_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return a default value or handle this case as needed
    }

    private void insertVoteDetails(String candidateName, String partyName, int electionId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Get the candidate ID based on candidateName and partyName
            String getCandidateIdSQL = "SELECT candidate_id FROM candidates WHERE name = ? AND party = ? AND region = ? AND position='MLA'";
            PreparedStatement getCandidateIdStatement = connection.prepareStatement(getCandidateIdSQL);
            getCandidateIdStatement.setString(1, candidateName);
            getCandidateIdStatement.setString(2, partyName);
            getCandidateIdStatement.setString(3, currentRegion); // Use the stored current region
            ResultSet candidateIdResult = getCandidateIdStatement.executeQuery();

            if (candidateIdResult.next()) {
                int candidateId = candidateIdResult.getInt("candidate_id");

                // Check if the election ID is valid
                if (isElectionValid(electionId, candidateId)) {
                    // Check if the voter ID is valid
                    if (isVoterValid(voterId)) {
                        // Insert the vote details with the provided election_id and voter_id
                        String insertVoteSQL;
                        insertVoteSQL = "INSERT INTO votes (candidate_name, candidate_party, candidate_id, election_id, voter_id) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement insertVoteStatement = connection.prepareStatement(insertVoteSQL);
                        insertVoteStatement.setString(1, candidateName);
                        insertVoteStatement.setString(2, partyName);
                        insertVoteStatement.setInt(3, candidateId);
                        insertVoteStatement.setInt(4, electionId);
                        insertVoteStatement.setInt(5, voterId);

                        int rowsAffected = insertVoteStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(this, "Vote for " + candidateName + " from " + partyName + " registered successfully.");
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to record your vote.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid voter ID.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid election ID.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Candidate not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isElectionValid(int electionId, int candidateId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM elections WHERE election_id = ? AND region = ? AND position = 'MLA'";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, electionId);
            preparedStatement.setString(2, currentRegion); // Use the stored current region

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int validElectionCount = resultSet.getInt(1);

            return validElectionCount > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private boolean isVoterValid(int voterId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT COUNT(*) FROM voters WHERE voter_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, voterId);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int validVoterCount = resultSet.getInt(1);

            return validVoterCount > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Implement the remaining time calculation and update
    private void updateRemainingTimeLabel(String region, String position) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date currentDate = new java.util.Date();
            String currentDateTime = dateFormat.format(currentDate);

            String sql = "SELECT MIN(end_date) FROM elections WHERE region = ? AND position = ? " +
                    "AND ? BETWEEN start_date AND end_date";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, region);
            preparedStatement.setString(2, position);
            preparedStatement.setString(3, currentDateTime);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Timestamp endDate = resultSet.getTimestamp(1);
                if (endDate != null) {
                    long remainingMillis = endDate.getTime() - currentDate.getTime();
                    long seconds = remainingMillis / 1000;
                    long minutes = seconds / 60;
                    long hours = minutes / 60;

                    String remainingTime = String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
                    remainingTimeLabel.setText("Remaining Time: " + remainingTime);
                } else {
                    remainingTimeLabel.setText("Remaining Time: N/A");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void navigateToGiveYourVotePage(String region, int voterId) {
        // Create an instance of the CheckResultPage and show it
        GiveYourVotePage gyvp=new GiveYourVotePage(region,voterId);
        gyvp.setVisible(true);
        this.dispose(); // Close the current window
    }

    private void initializeVotedElectionIds() {
        // Fetch and store the election IDs for which the current voter has already voted
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT DISTINCT election_id FROM votes WHERE voter_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, voterId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                votedElectionIds.add(resultSet.getInt("election_id"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private boolean hasVoterAlreadyVotedInElection(String region) {
        int currentElectionId = getElectionId(region, "MLA");
        return votedElectionIds.contains(currentElectionId);
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

                new MLAElectionPage("Your Region", 12345).setVisible(true); // Replace with actual region and voter ID
            }
        });
    }
}
