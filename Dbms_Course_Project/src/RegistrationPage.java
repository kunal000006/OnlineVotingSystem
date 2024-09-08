import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegistrationPage extends JFrame {
    public RegistrationPage() {
        // Set window title
        setTitle("Registration Page");

        // Create buttons
        JButton voterButton = new JButton("Register as Voter");
        JButton candidateButton = new JButton("Register as Candidate");
        JButton authorityButton = new JButton("Register as Authority");

        // Set button dimensions
        Dimension buttonSize = new Dimension(200, 50);
        voterButton.setPreferredSize(buttonSize);
        candidateButton.setPreferredSize(buttonSize);
        authorityButton.setPreferredSize(buttonSize);

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(voterButton);
        buttonPanel.add(candidateButton);
        buttonPanel.add(authorityButton);

        // Add action listeners to the buttons
        voterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement registration logic for voters
                // You can open a new registration form or perform any necessary actions here
                VoterRegistrationPage vrp = new VoterRegistrationPage();
                vrp.setVisible(true);
                dispose();
            }
        });

        candidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CandidateRegistrationPage crp = new CandidateRegistrationPage();
                crp.setVisible(true);
                dispose();
            }
        });

        authorityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement registration logic for authorities
                // You can open a new registration form or perform any necessary actions here
                AuthorityRegistrationPage arp = new AuthorityRegistrationPage();
                arp.setVisible(true);
                dispose();
            }
        });

        // Create a panel to center the button panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonPanel);

        // Create a label that acts as a hyperlink
        JLabel loginLinkLabel = new JLabel("Already Registered? Go to login page");
        loginLinkLabel.setForeground(Color.BLUE);
        loginLinkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                LoginPage arp = new LoginPage();
                arp.setVisible(true);
                dispose();
                // Implement logic to navigate to the login page
                // You can open a login form or perform any necessary actions here
                // For demonstration purposes, let's just print a message
                System.out.println("Navigating to login page...");
            }
        });

        // Create a panel to hold the login link label
        JPanel loginLinkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginLinkPanel.add(loginLinkLabel);

        // Add the button panel and login link panel to the frame
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(loginLinkPanel, BorderLayout.SOUTH);
        add(contentPanel);

        // Set window size and close operation
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set the look and feel to the system's default (e.g., Windows, macOS)
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new RegistrationPage().setVisible(true);
            }
        });
    }
}
