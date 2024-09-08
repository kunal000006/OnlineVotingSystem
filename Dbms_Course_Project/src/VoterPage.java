import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoterPage extends JFrame {
    private String region;
    private int voterID;

    public VoterPage(String region, int voterID) {
        this.region = region;
        this.voterID=voterID;
        setTitle("Voter Page");
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
                VoterLoginPage vlp = new VoterLoginPage();
                vlp.setVisible(true);
                dispose();
            }
        });

        JLabel titleLabel = new JLabel("Voter Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        constraints.insets = new Insets(0, 0, 20, 0);
        panel.add(titleLabel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(5, 5, 5, 5);

        JButton giveVoteButton = new JButton("Give Your Vote");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(giveVoteButton, constraints);

        JButton checkResultButton = new JButton("Check Result");
        constraints.gridx = 1;
        panel.add(checkResultButton, constraints);

        giveVoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GiveYourVotePage giveYourVotePage = new GiveYourVotePage(region,voterID);
                giveYourVotePage.setVisible(true);
                dispose();
            }
        });

        checkResultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "Check Result" button action here.
                // You can open a new window or perform the necessary actions for checking results.
                CheckResultPage crp=new CheckResultPage(region,voterID);
                crp.setVisible(true);
                dispose();
                JOptionPane.showMessageDialog(VoterPage.this, "You clicked 'Check Result' button.");

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

                new VoterPage("Pune",1).setVisible(true); // Example region name
            }
        });
    }
}
