package ui;

import javax.swing.*;
import java.awt.*;

import game.*;
import ai.*;
import java.util.List;

public class GamePanel extends JPanel {

    private GUI frame;
    private Settings settings;

    private JButton[][] buttons = new JButton[3][3];
    private Board board;
    private JLabel statusLabel;
    private JLabel infoLabel;
    private JTextArea logArea;
    private boolean gameOver = false;

    private int playerWins = 0;
    private int aiWins = 0;
    private int draws = 0;
    private JLabel scoreLabel;

    private EvaluatFunction evalFunc;
    private AlphaBeta aiEngine;

    public GamePanel(GUI frame, Settings settings) {
        this.frame = frame;
        this.settings = settings;

        setLayout(new BorderLayout());
        setBackground(UI.BG_MAIN);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UI.TOPBAR_BG);

        statusLabel = new JLabel("Status", SwingConstants.LEFT);
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(UI.FONT_TEXT);

        infoLabel = new JLabel("", SwingConstants.RIGHT);
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(UI.FONT_TEXT);

        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        topBar.add(statusLabel, BorderLayout.WEST);
        topBar.add(infoLabel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        boardPanel.setBackground(UI.BG_MAIN);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                JButton b = new JButton("");
                b.setPreferredSize(new Dimension(100, 80));
                b.setFont(UI.FONT_CELL);
                b.setBackground(new Color(240, 240, 250));
                b.setOpaque(true);
                b.setBorder(BorderFactory.createLineBorder(new Color(150,150,150), 2));
                b.setForeground(Color.BLACK);
                b.setFocusPainted(false);

                final int rr = r, cc = c;
                b.addActionListener(e -> handleHumanMove(rr, cc));

                buttons[r][c] = b;
                boardPanel.add(b);
            }
        }

        centerPanel.add(boardPanel);
        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(320, 0));
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(UI.CARD_BG);


        JPanel scorePanel = new JPanel();
        scorePanel.setBackground(UI.CARD_BG);
        scorePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UI.SCORE_BORDER, 1),
                "Score",
                0, 0,
                UI.FONT_TEXT,
                Color.WHITE
        ));
        scorePanel.setLayout(new BorderLayout());

        scoreLabel = new JLabel("", SwingConstants.LEFT);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(UI.FONT_TEXT);

        updateScoreLabel();
        scorePanel.add(scoreLabel, BorderLayout.CENTER);
        rightPanel.add(scorePanel, BorderLayout.NORTH);



        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(UI.LOG_BG);
        logArea.setForeground(UI.LOG_TEXT);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));

        JScrollPane scroll = new JScrollPane(logArea);


        scroll.setPreferredSize(new Dimension(320, 200));

        rightPanel.add(scroll, BorderLayout.CENTER);



        JPanel sideBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        sideBottom.setOpaque(false);
        sideBottom.setPreferredSize(new Dimension(320, 70));


        RddButton newGameBtn = new RddButton("New Game") {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(120, 40);
            }
        };
        RddButton backBtn = new RddButton("Back to Menu") {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 40);
            }
        };

        newGameBtn.addActionListener(e -> startNewGame());
        backBtn.addActionListener(e -> frame.showMenu());

        sideBottom.add(newGameBtn);
        sideBottom.add(backBtn);

        rightPanel.add(sideBottom, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.EAST);

    }

    private void updateScoreLabel() {
        scoreLabel.setText("<html>" +
                "Player (" + settings.humanSymbol + "): " + playerWins + "<br>" +
                "AI (" + settings.aiSymbol + "): " + aiWins + "<br>" +
                "Draws: " + draws +
                "</html>");
    }

    public void startNewGame() {
        board = new Board();
        gameOver = false;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                buttons[r][c].setText("");
                buttons[r][c].setEnabled(true);
                buttons[r][c].setBackground(new Color(240, 240, 250));
                buttons[r][c].setForeground(Color.BLACK);
            }

        try {
            if (settings.evalType == EvalType.CLASSICAL)
                evalFunc = new ClassicalEval();
            else
                evalFunc = new MLEvaluat(settings.aiSymbol, settings.csvPath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading ML model. Using Classical evaluation.",
                    "ML Error", JOptionPane.WARNING_MESSAGE);
            evalFunc = new ClassicalEval();
        }

        aiEngine = new AlphaBeta(evalFunc, settings.difficulty);

        statusLabel.setText("Your turn (" + settings.humanSymbol + ")");
        infoLabel.setText("Diff: " + settings.difficulty + " | Eval: " + settings.evalType);
        logArea.setText("");
    }

    private void handleHumanMove(int r, int c) {
        if (gameOver) return;
        if (board.getCell(r, c) != ' ') return;

        board.makeMove(r, c, settings.humanSymbol);
        buttons[r][c].setText(String.valueOf(settings.humanSymbol));

        if (settings.humanSymbol == 'X')
            buttons[r][c].setForeground(new Color(0, 60, 220));
        else
            buttons[r][c].setForeground(new Color(220, 0, 0));

        if (checkGameEnd()) return;

        statusLabel.setText("AI thinking...");
        logArea.setText("AI evaluations:\n");

        SwingUtilities.invokeLater(this::makeAiMove);
    }

    private void makeAiMove() {
        if (gameOver) return;

        char ai = settings.aiSymbol;


        logArea.append("Evaluations (1-ply):\n");
        for (Move m : board.getLegalMoves()) {
            Board copy = board.copy();
            copy.makeMove(m.row, m.col, ai);
            double score = evalFunc.evaluate(copy, ai);
            logArea.append(String.format("(%d,%d) -> %.3f%n", m.row, m.col, score));
        }
        logArea.append("----------------------\n");


        Move bestMove = aiEngine.findBestMove(board, ai);

        if (bestMove != null) {
            board.makeMove(bestMove.row, bestMove.col, ai);
            JButton b = buttons[bestMove.row][bestMove.col];
            b.setText(String.valueOf(ai));

            if (ai == 'X')
                b.setForeground(new Color(0, 60, 220));
            else
                b.setForeground(new Color(220, 0, 0));
        }

        if (!checkGameEnd())
            statusLabel.setText("Your turn (" + settings.humanSymbol + ")");
    }



    private boolean checkGameEnd() {
        char winner = board.getWinner();

        if (winner == settings.humanSymbol) {
            playerWins++;
            updateScoreLabel();
            statusLabel.setText("You win!");
            JOptionPane.showMessageDialog(this, "You win!");
            gameOver = true;
        } else if (winner == settings.aiSymbol) {
            aiWins++;
            updateScoreLabel();
            statusLabel.setText("AI wins!");
            JOptionPane.showMessageDialog(this, "AI wins!");
            gameOver = true;
        } else if (board.isFull()) {
            draws++;
            updateScoreLabel();
            statusLabel.setText("Draw.");
            JOptionPane.showMessageDialog(this, "It's a draw.");
            gameOver = true;
        }

        if (gameOver)
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    buttons[r][c].setEnabled(false);

        return gameOver;
    }
}
