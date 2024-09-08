import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CandidateLoginPage extends JFrame {

    private JTextField nameField;
    private JTextField candidateIdField;
    private String candidateName; // Declare an instance variable
    private int candidateId; // Declare an instance variable

    public CandidateLoginPage() {
        setTitle("Candidate Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
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
                dispose(); // Close the current window
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

        JLabel nameLabel = new JLabel("Name:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(nameLabel, constraints);

        nameField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(nameField, constraints);

        JLabel candidateIdLabel = new JLabel("Candidate ID:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(candidateIdLabel, constraints);

        candidateIdField = new JTextField(20);
        constraints.gridx = 1;
        panel.add(candidateIdField, constraints);

        JButton loginButton = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                candidateName = nameField.getText(); // Assign values to instance variables
                candidateId = Integer.parseInt(candidateIdField.getText());

                if (validateCandidate(candidateId, candidateName)) {
                    JOptionPane.showMessageDialog(CandidateLoginPage.this, "Login Successful!");
                    CandidatePage cp = new CandidatePage(candidateId); // Pass candidateId to CandidatePage
                    cp.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(CandidateLoginPage.this, "Invalid Candidate ID or Name.");
                }
            }
        });

        add(panel);
    }

    private boolean validateCandidate(int candidateId, String candidateName) {
        boolean isValid = false;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/dbmscp", "root", "Kunal06nda@")) {
            String sql = "SELECT * FROM candidates WHERE candidate_id = ? AND name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, candidateId);
            preparedStatement.setString(2, candidateName);
            ResultSet resultSet = preparedStatement.executeQuery();

            isValid = resultSet.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return isValid;
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

                new CandidateLoginPage().setVisible(true);
            }
        });
    }
}
