import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiveYourVotePage extends JFrame {
    private JButton backButton;
    private JButton mpElectionButton;
    private JButton mlaElectionButton;
    private JLabel regionLabel;

    public GiveYourVotePage(String region, int voterId) {
        setTitle("Give Your Vote");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Display the voter's region
        regionLabel = new JLabel("Region: " + region);
        regionLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        panel.add(regionLabel, constraints);

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "Back" button action
                VoterPage vp = new VoterPage(region,voterId);
                vp.setVisible(true);
                dispose();
            }
        });

        mpElectionButton = new JButton("MPElection");
        mpElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "MPElection" button action
                MPElectionPage mpep = new MPElectionPage(region, voterId);
                mpep.setVisible(true);
                dispose();
            }
        });

        mlaElectionButton = new JButton("MLAElection");
        mlaElectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "MLAElection" button action
                // Add your logic for MLAElection here
                MLAElectionPage mlaep=new MLAElectionPage(region,voterId);
                mlaep.setVisible(true);
                dispose();
            }
        });

        // Add the "Back" button
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(backButton, constraints);

        // Add the "MPElection" button
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(mpElectionButton, constraints);

        // Add the "MLAElection" button
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.WEST;
        panel.add(mlaElectionButton, constraints);

        // Add your additional UI elements and logic for the "Give Your Vote" page here

        add(panel);
    }

    // Add additional methods and UI elements for the "Give Your Vote" page as needed

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                new GiveYourVotePage("Your Region", 12345).setVisible(true); // Replace with actual region and voter ID
            }
        });
    }
}
