package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Binary Regression
 */
public class App 
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        double bestBCE = Double.MAX_VALUE;
        double bestAccuracy = -1;
        double bestPrecision = -1;
        double bestRecall = -1;
        double bestF1 = -1;
        double bestAUC = -1;

        String bestBCEModel = "";
        String bestAccuracyModel = "";
        String bestPrecisionModel = "";
        String bestRecallModel = "";
        String bestF1Model = "";
        String bestAUCModel = "";

        for (String filePath : files) {
            FileReader filereader;
            List<String[]> allData;

            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                allData = csvReader.readAll();
            }
            catch (Exception e) {
                System.out.println("Error reading file: " + filePath);
                continue;
            }

            int n = allData.size();
            double epsilon = 1e-15;
            double threshold = 0.5;

            double bceSum = 0.0;

            int TP = 0;
            int FP = 0;
            int FN = 0;
            int TN = 0;

            int nPositive = 0;
            int nNegative = 0;

            int[] yTrueArray = new int[n];
            double[] yPredArray = new double[n];

            for (int i = 0; i < n; i++) {
                String[] row = allData.get(i);

                int y_true = Integer.parseInt(row[0]);
                double y_predicted = Double.parseDouble(row[1]);

                yTrueArray[i] = y_true;
                yPredArray[i] = y_predicted;

                double p = Math.max(epsilon, Math.min(1.0 - epsilon, y_predicted));
                bceSum += -(y_true * Math.log(p) + (1 - y_true) * Math.log(1 - p));

                int y_binary;
                if (y_predicted >= threshold) {
                    y_binary = 1;
                } else {
                    y_binary = 0;
                }

                if (y_true == 1) {
                    nPositive++;
                    if (y_binary == 1) {
                        TP++;
                    } else {
                        FN++;
                    }
                } else {
                    nNegative++;
                    if (y_binary == 1) {
                        FP++;
                    } else {
                        TN++;
                    }
                }
            }

            double BCE = bceSum / n;
            double accuracy = (double)(TP + TN) / (TP + TN + FP + FN);
            double precision = (TP + FP == 0) ? 0 : (double)TP / (TP + FP);
            double recall = (TP + FN == 0) ? 0 : (double)TP / (TP + FN);
            double f1 = (precision + recall == 0) ? 0 : (2.0 * precision * recall) / (precision + recall);

            double[] x = new double[101];
            double[] y = new double[101];

            for (int i = 0; i <= 100; i++) {
                double th = i / 100.0;

                int rocTP = 0;
                int rocFP = 0;

                for (int j = 0; j < n; j++) {
                    if (yTrueArray[j] == 1 && yPredArray[j] >= th) {
                        rocTP++;
                    }
                    if (yTrueArray[j] == 0 && yPredArray[j] >= th) {
                        rocFP++;
                    }
                }

                double TPR = (double)rocTP / nPositive;
                double FPR = (double)rocFP / nNegative;

                y[i] = TPR;
                x[i] = FPR;
            }

            double auc = 0.0;
            for (int i = 1; i <= 100; i++) {
                auc += (y[i - 1] + y[i]) * Math.abs(x[i - 1] - x[i]) / 2.0;
            }

            System.out.println("for " + filePath);
            System.out.println("\tBCE = " + BCE);
            System.out.println("\tConfusion matrix");
            System.out.println("\t\t\ty=1\t\ty=0");
            System.out.println("\t\ty^=1\t" + TP + "\t\t" + FP);
            System.out.println("\t\ty^=0\t" + FN + "\t\t" + TN);
            System.out.println("\tAccuracy = " + accuracy);
            System.out.println("\tPrecision = " + precision);
            System.out.println("\tRecall = " + recall);
            System.out.println("\tf1 score = " + f1);
            System.out.println("\tauc roc = " + auc);
            System.out.println();

            if (BCE < bestBCE) {
                bestBCE = BCE;
                bestBCEModel = filePath;
            }
            if (accuracy > bestAccuracy) {
                bestAccuracy = accuracy;
                bestAccuracyModel = filePath;
            }
            if (precision > bestPrecision) {
                bestPrecision = precision;
                bestPrecisionModel = filePath;
            }
            if (recall > bestRecall) {
                bestRecall = recall;
                bestRecallModel = filePath;
            }
            if (f1 > bestF1) {
                bestF1 = f1;
                bestF1Model = filePath;
            }
            if (auc > bestAUC) {
                bestAUC = auc;
                bestAUCModel = filePath;
            }
        }

        System.out.println("According to BCE, The best model is " + bestBCEModel);
        System.out.println("According to Accuracy, The best model is " + bestAccuracyModel);
        System.out.println("According to Precision, The best model is " + bestPrecisionModel);
        System.out.println("According to Recall, The best model is " + bestRecallModel);
        System.out.println("According to F1 score, The best model is " + bestF1Model);
        System.out.println("According to AUC ROC, The best model is " + bestAUCModel);
    }
}