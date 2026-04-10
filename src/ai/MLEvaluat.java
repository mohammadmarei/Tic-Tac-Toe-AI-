package ai;

import game.Board;
import java.io.*;
import java.util.*;

public class MLEvaluat implements EvaluatFunction {

    private final char aiPlayer;
    private final int featureCount = 6;
    private final double[] weights;
    private double bias;

    public MLEvaluat(char aiPlayer, String csvPath) throws IOException {
        this.aiPlayer = aiPlayer;
        this.weights = new double[featureCount];
        this.bias = 0.0;

        Random rnd = new Random();
        for (int i = 0; i < featureCount; i++) {
            weights[i] = (rnd.nextDouble() - 0.5) * 0.1;
        }

        if (csvPath != null && !csvPath.isEmpty()) {
            trainPerceptron(csvPath, 30, 0.01);

        }
    }

    @Override
    public double evaluate(Board board, char maxPlayer) {
        double[] x = extractFeatures(board, maxPlayer);
        double z = bias;
        for (int i = 0; i < featureCount; i++) {
            z += weights[i] * x[i];
        }
        return z;
    }

    private void trainPerceptron(String csvPath, int epochs, double lr) throws IOException {
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                CsvExample ex = readCsvRow(line);
                if (ex == null) continue;
                features.add(ex.features);
                labels.add(ex.label);
            }
        }

        int total = features.size();
        int trainSize = (int)(total * 0.8);

        List<double[]> trainFeatures = features.subList(0, trainSize);
        List<Integer> trainLabels = labels.subList(0, trainSize);
        List<double[]> testFeatures = features.subList(trainSize, total);
        List<Integer> testLabels = labels.subList(trainSize, total);

        System.out.println("ML training samples = " + trainFeatures.size());
        System.out.println("ML testing samples = " + testFeatures.size());

        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < trainFeatures.size(); i++) {
                double[] x = trainFeatures.get(i);
                int y = trainLabels.get(i);

                double z = bias;
                for (int j = 0; j < featureCount; j++) {
                    z += weights[j] * x[j];
                }

                if (y * z <= 0) {
                    for (int j = 0; j < featureCount; j++) {
                        weights[j] += lr * y * x[j];
                    }
                    bias += lr * y;
                }
            }
        }

        int correct = 0;
        for (int i = 0; i < testFeatures.size(); i++) {
            double[] x = testFeatures.get(i);
            int y = testLabels.get(i);

            double z = bias;
            for (int j = 0; j < featureCount; j++) {
                z += weights[j] * x[j];
            }

            int pred = (z >= 0) ? 1 : -1;
            if (pred == y) correct++;
        }

        double accuracy = (correct * 100.0) / testFeatures.size();
        System.out.println("ML Test Accuracy = " + accuracy + "%");
    }

    private CsvExample readCsvRow(String line) {
        String[] parts = line.split(",");
        if (parts.length < featureCount + 1) return null;

        double[] feats = new double[featureCount];
        try {
            for (int i = 0; i < featureCount; i++) {
                feats[i] = Double.parseDouble(parts[i].trim());
            }
        } catch (NumberFormatException e) {
            return null;
        }

        int label;
        String labelStr = parts[featureCount].trim();
        if (labelStr.equals("1") || labelStr.equals("+1")) label = 1;
        else if (labelStr.equals("-1")) label = -1;
        else return null;

        return new CsvExample(feats, label);
    }

    private static class CsvExample {
        double[] features;
        int label;

        CsvExample(double[] features, int label) {
            this.features = features;
            this.label = label;
        }
    }

    private double[] extractFeatures(Board board, char maxPlayer) {
        double[] f = new double[featureCount];
        char minPlayer = (maxPlayer == 'X') ? 'O' : 'X';

        int countMax = 0, countMin = 0;
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                char ch = board.getCell(r, c);
                if (ch == maxPlayer) countMax++;
                if (ch == minPlayer) countMin++;
            }

        f[0] = countMax;
        f[1] = countMin;
        f[2] = (board.getCell(1,1) == maxPlayer) ? 1 : 0;
        f[3] = (board.getCell(1,1) == minPlayer) ? 1 : 0;
        f[4] = countCorners(board, maxPlayer);
        f[5] = countCorners(board, minPlayer);

        return f;
    }

    private int countCorners(Board board, char p) {
        int[][] corners = {{0,0},{0,2},{2,0},{2,2}};
        int c = 0;
        for (int[] pos : corners) {
            if (board.getCell(pos[0], pos[1]) == p) c++;
        }
        return c;
    }
}
