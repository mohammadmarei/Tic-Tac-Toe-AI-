package ui;

import javax.swing.*;
import java.awt.*;
import game.Settings;

public class GUI extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private MenuPanel mainMenu;
    private SettingsPanel configPanel;
    private GamePanel gamePanel;

    private Settings settings = new Settings();

    public GUI() {
        setTitle("Tic-Tac-Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 650));
        setSize(1000, 650);

        setLocationRelativeTo(null);

        mainPanel.setBackground(UI.BG_MAIN);

        mainMenu = new MenuPanel(this, settings);
        configPanel = new SettingsPanel(this, settings);
        gamePanel = new GamePanel(this, settings);

        mainPanel.add(mainMenu, "MENU");
        mainPanel.add(configPanel, "SETTINGS");
        mainPanel.add(gamePanel, "GAME");

        setContentPane(mainPanel);
        showMenu();

        setVisible(true);
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
    }

    public void showSettings() {
        cardLayout.show(mainPanel, "SETTINGS");
    }

    public void showGame() {
        gamePanel.startNewGame();
        cardLayout.show(mainPanel, "GAME");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(GUI::new);
    }
}
