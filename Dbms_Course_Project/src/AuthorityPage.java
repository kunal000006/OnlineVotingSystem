import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthorityPage extends JFrame {

    public AuthorityPage() {
        setTitle("Authority Page");
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
                AuthorityLoginPage alp = new AuthorityLoginPage();
                alp.setVisible(true);
                dispose(); // Close the current window
            }
        });

        JLabel titleLabel = new JLabel("Authority Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        JButton scheduleElectionButton = new JButton("Schedule Election for Your Region");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(scheduleElectionButton, constraints);

        JButton viewResultButton = new JButton("View Result of Your Region");
        constraints.gridx = 1;
        panel.add(viewResultButton, constraints);

        scheduleElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement the action for scheduling an election here
                // Assuming you have a ScheduleElectionPage class, you can create an instance and make it visible
                ScheduleElectionPage sep = new ScheduleElectionPage("Your Region"); // Pass the region as needed
                sep.setVisible(true);
                dispose();
            }
        });

        viewResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement the action for viewing the result of your region here
                JOptionPane.showMessageDialog(AuthorityPage.this, "Viewing Result of Your Region.");
            }
        });

        add(panel);
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

                new AuthorityPage().setVisible(true);
            }
        });
    }
}
