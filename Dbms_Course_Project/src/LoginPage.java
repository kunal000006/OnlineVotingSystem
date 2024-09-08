import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginPage extends JFrame {
    private JButton voterButton;
    private JButton candidateButton;
    private JButton authorityButton;
    private JLabel timeDateLabel;
    private JLabel backButton; // Use JLabel for "Back" text

    public LoginPage() {
        // Set window title
        setTitle("Login Page");

        // Create buttons with custom styles
        voterButton = createStyledButton("Login as Voter");
        candidateButton = createStyledButton("Login as Candidate");
        authorityButton = createStyledButton("Login as Authority");

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        buttonPanel.add(voterButton);
        buttonPanel.add(candidateButton);
        buttonPanel.add(authorityButton);

        // Create a panel for the "Back" text in the top left corner (as a JLabel)
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backButton = new JLabel("Back");
        backButton.setForeground(Color.RED);
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Make it look clickable
        backButtonPanel.add(backButton);

        // Create a panel for the time and date display in the top right corner
        JPanel timeDatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timeDateLabel = new JLabel();
        updateTimeDateLabel();
        timeDatePanel.add(timeDateLabel);

        // Create a panel to center the button panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonPanel);

        // Add components to the frame
        add(backButtonPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(timeDatePanel, BorderLayout.SOUTH);

        // Set window size and close operation
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Start a timer to update the time and date label every second
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTimeDateLabel();
            }
        });
        timer.start();

        // Add a click event to the "Back" label
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RegistrationPage rp = new RegistrationPage();
                rp.setVisible(true);
                dispose();
                // Handle the "Back" action here (e.g., returning to a previous screen)
            }
        });

        voterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                VoterLoginPage vlp=new VoterLoginPage();
                vlp.setVisible(true);
                dispose();
            }
        });

        candidateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CandidateLoginPage clp=new CandidateLoginPage();
                clp.setVisible(true);
                dispose();
            }
        });

        authorityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AuthorityLoginPage alp=new AuthorityLoginPage();
                alp.setVisible(true);
                dispose();
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 50));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.BLACK);
        return button;
    }

    private void updateTimeDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = sdf.format(new Date());
        timeDateLabel.setText(dateTime);
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

                new LoginPage().setVisible(true);
            }
        });
    }
}
