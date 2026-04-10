package ui;

import game.Difficulty;
import game.EvalType;
import game.Settings;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SettingsPanel extends JPanel {

    private Settings settings;

    private JRadioButton xRadio;
    private JRadioButton oRadio;
    private JComboBox<Difficulty> diffBox;
    private JRadioButton classicalRadio;
    private JRadioButton mlRadio;
    private JTextField csvField;

    public SettingsPanel(GUI frame, Settings settings) {
        this.settings = settings;

        setLayout(new BorderLayout());
        setBackground(UI.BG_MAIN);

        JLabel title = new JLabel("Settings", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(UI.FONT_TITLE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(title, BorderLayout.NORTH);

        JPanel container = new JPanel();
        container.setOpaque(false);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel card = new JPanel();
        card.setBackground(UI.CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));


        JPanel symbolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        symbolPanel.setOpaque(false);
        JLabel symbolLabel = new JLabel("Your symbol:");
        symbolLabel.setForeground(Color.WHITE);
        symbolLabel.setFont(UI.FONT_TEXT);
        xRadio = createRadio("X");
        oRadio = createRadio("O");

        ButtonGroup symbolGroup = new ButtonGroup();
        symbolGroup.add(xRadio);
        symbolGroup.add(oRadio);
        xRadio.setSelected(settings.humanSymbol == 'X');
        oRadio.setSelected(settings.humanSymbol == 'O');

        symbolPanel.add(symbolLabel);
        symbolPanel.add(xRadio);
        symbolPanel.add(oRadio);


        JPanel diffPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        diffPanel.setOpaque(false);
        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setForeground(Color.WHITE);
        diffLabel.setFont(UI.FONT_TEXT);
        diffBox = new JComboBox<>(Difficulty.values());
        diffBox.setSelectedItem(settings.difficulty);
        diffPanel.add(diffLabel);
        diffPanel.add(diffBox);


        JPanel evalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        evalPanel.setOpaque(false);
        JLabel evalLabel = new JLabel("Evaluation:");
        evalLabel.setForeground(Color.WHITE);
        evalLabel.setFont(UI.FONT_TEXT);
        classicalRadio = createRadio("Classical");
        mlRadio = createRadio("ML (CSV)");
        ButtonGroup evalGroup = new ButtonGroup();
        evalGroup.add(classicalRadio);
        evalGroup.add(mlRadio);
        classicalRadio.setSelected(settings.evalType == EvalType.CLASSICAL);
        mlRadio.setSelected(settings.evalType == EvalType.ML);

        evalPanel.add(evalLabel);
        evalPanel.add(classicalRadio);
        evalPanel.add(mlRadio);

        JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        csvPanel.setOpaque(false);
        JLabel csvLabel = new JLabel("CSV Path:");
        csvLabel.setForeground(Color.WHITE);
        csvLabel.setFont(UI.FONT_TEXT);
        csvField = new JTextField(25);
        csvField.setText(settings.csvPath);
        RddButton browseBtn = new RddButton("Browse...");
        browseBtn.addActionListener(e -> chooseCsvFile());

        csvPanel.add(csvLabel);
        csvPanel.add(csvField);
        csvPanel.add(browseBtn);

        card.add(symbolPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(diffPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(evalPanel);
        card.add(Box.createVerticalStrut(15));
        card.add(csvPanel);

        container.add(Box.createVerticalGlue());
        container.add(card);
        container.add(Box.createVerticalGlue());
        add(container, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        RddButton backBtn = new RddButton("Back");
        RddButton saveBtn = new RddButton("Save");

        backBtn.addActionListener(e -> frame.showMenu());
        saveBtn.addActionListener(e -> {
            applySettings();
            JOptionPane.showMessageDialog(this, "Settings saved.");
            frame.showMenu();
        });

        bottom.add(backBtn);
        bottom.add(saveBtn);
        add(bottom, BorderLayout.SOUTH);
    }

    private JRadioButton createRadio(String text) {
        JRadioButton rb = new JRadioButton(text);
        rb.setOpaque(false);
        rb.setForeground(Color.WHITE);
        rb.setFont(UI.FONT_TEXT);
        return rb;
    }

    private void chooseCsvFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            csvField.setText(f.getAbsolutePath());
        }
    }

    private void applySettings() {
        settings.humanSymbol = xRadio.isSelected() ? 'X' : 'O';
        settings.aiSymbol = (settings.humanSymbol == 'X') ? 'O' : 'X';
        settings.difficulty = (Difficulty) diffBox.getSelectedItem();
        settings.evalType = classicalRadio.isSelected() ? EvalType.CLASSICAL : EvalType.ML;
        settings.csvPath = csvField.getText().trim();
    }
}
