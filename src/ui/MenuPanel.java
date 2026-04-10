package ui;

import javax.swing.*;
import java.awt.*;
import game.Settings;

public class MenuPanel extends JPanel {

    public MenuPanel(GUI frame, Settings settings) {

        setLayout(new BorderLayout());
        setBackground(UI.BG_MAIN);

        JLabel title = new JLabel("Tic-Tac-Toe", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UI.FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(40, 10, 30, 10));
        add(title, BorderLayout.NORTH);

        JPanel mainCard = new JPanel();
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setOpaque(false);

        RddButton startBtn = new RddButton("Start Game");
        RddButton settingsBtn = new RddButton("Settings");
        RddButton exitBtn = new RddButton("Exit");
        startBtn.addActionListener(e -> frame.showGame());
        settingsBtn.addActionListener(e -> frame.showSettings());
        exitBtn.addActionListener(e -> System.exit(0));

        startBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainCard.add(Box.createVerticalGlue());
        mainCard.add(startBtn);
        mainCard.add(Box.createRigidArea(new Dimension(0, 20)));
        mainCard.add(settingsBtn);
        mainCard.add(Box.createRigidArea(new Dimension(0, 20)));
        mainCard.add(exitBtn);
        mainCard.add(Box.createVerticalGlue());

        add(mainCard, BorderLayout.CENTER);

    }
}
