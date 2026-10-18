import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class App {
    public static void main(String[] args) {
        // 1. Initialize the frame and remove desktop borders
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); 
        frame.setBackground(null);

        // 2. Create a container panel using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 3. Configure layout to pin the button to the exact top-right corner
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;          // Pushes empty space to the left
        gbc.weighty = 1.0;          // Pushes empty space to the bottom
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Locks it to top-right
        gbc.insets = new Insets(15, 0, 0, 15);          // 15px margin from the screen edges

        // 4. Create the exit button
        JButton exitButton = new JButton("X");
        exitButton.addActionListener(e -> System.exit(0));

        // 5. Assemble the interface
        panel.add(exitButton, gbc);
        frame.add(panel);

        // 6. Request full screen control from the operating system
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        
        frame.setVisible(true);
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(frame);
        } else {
            // Fallback strategy if hardware blocks true full screen
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }
}
