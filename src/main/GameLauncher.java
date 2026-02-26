package main;

import settings.Settings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Objects;

//YOU CAN LAUNCH THE GAME EITHER WITH Game.java OR GameLauncher.java

/**
 * A launcher sets user-configurable settings (resolution, fullscreen, debug mode),
 * and a "Start Game" button that creates and runs the Game.
 */
public class GameLauncher {

    /**
     * Launches the UI on the Swing Event Dispatch Thread.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {

        //make it thread safe as specified in the swing docs
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameLauncher launcher = new GameLauncher();
                launcher.createLauncher();
            }//end run
        });//end invokeLater
    }//end main

    private JFrame frame;

    /**
     * Creates the UI for the Game Launcher
     * <p>
     * Apply user-selected settings, and then a new Game instance
     * is started on a background thread.
     */
    public void createLauncher() {
        frame = new JFrame("Endless Adventure - Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Color backgroundColor = new Color(30, 30, 30);
        Color foregroundColor = Color.WHITE;

        //TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(new EmptyBorder(20, 10, 10, 10));

        JLabel titleLabel = new JLabel("Endless Adventure", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(foregroundColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        topPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        ImageIcon bannerIcon = new ImageIcon("images/banner.gif");
        JLabel bannerLabel = new JLabel(bannerIcon);
        bannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(bannerLabel);

        frame.add(topPanel, BorderLayout.NORTH);

        //CENTER PANEL
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        //Resolution
        JLabel resolutionLabel = new JLabel("Resolution:");
        resolutionLabel.setForeground(foregroundColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(resolutionLabel, gbc);

        JComboBox<Settings.Resolution> resolutionBox = new JComboBox<>(Settings.Resolution.values());
        resolutionBox.setSelectedItem(Settings.Resolution.HD_1080);
        resolutionBox.setBackground(Color.DARK_GRAY);
        resolutionBox.setForeground(foregroundColor);
        resolutionBox.setOpaque(true);
        gbc.gridx = 1;
        centerPanel.add(resolutionBox, gbc);

        //Options
        JLabel optionsLabel = new JLabel("Options:");
        optionsLabel.setForeground(foregroundColor);
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(optionsLabel, gbc);

        JCheckBox fullscreenBox = new JCheckBox("Fullscreen (not supported)");
        JCheckBox debugBox = new JCheckBox("Debug Mode");
        JCheckBox fastLoadingBox = new JCheckBox("Fast Loading Screens");

        JCheckBox[] checkboxes = {fullscreenBox, debugBox, fastLoadingBox};
        for (JCheckBox cb : checkboxes) {
            cb.setForeground(foregroundColor);
            cb.setBackground(backgroundColor);
            cb.setOpaque(true);
            cb.setFocusable(false);
        }//end for loop

        JPanel optionsPanel = new JPanel(new GridLayout(3, 1));
        optionsPanel.setOpaque(false);
        optionsPanel.add(fullscreenBox);
        optionsPanel.add(debugBox);
        optionsPanel.add(fastLoadingBox);

        gbc.gridx = 1;
        centerPanel.add(optionsPanel, gbc);

        frame.add(centerPanel, BorderLayout.CENTER);

        //BOTTOM PANEL
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        startButton.setPreferredSize(new Dimension(160, 45));
        startButton.setBackground(new Color(70, 130, 180));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitButton.setPreferredSize(new Dimension(100, 40));
        exitButton.setBackground(Color.DARK_GRAY);
        exitButton.setForeground(Color.LIGHT_GRAY);
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        startButton.addActionListener(e -> {
            Settings.setScreenResolution((Settings.Resolution) Objects.requireNonNull(resolutionBox.getSelectedItem()));
            Settings.setDebugMode(debugBox.isSelected());
            Settings.setScreenFullscreen(fullscreenBox.isSelected());
            Settings.setLongLoadingScreen(!fastLoadingBox.isSelected()); // fast loading = false = skip long loading screen
            frame.dispose();
            new Thread(() -> new Game()).start();
        });//end addActionListener

        exitButton.addActionListener(e -> System.exit(0));

        bottomPanel.add(startButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(exitButton);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        //Show UI
        frame.setVisible(true);
    }//end createLauncher

}//end class
