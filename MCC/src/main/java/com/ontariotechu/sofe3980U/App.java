package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Multiclass Classification
 */
public class App 
{
    public static void main(String[] args)
    {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        }
        catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        double epsilon = 1e-15;
        double ceSum = 0.0;

        int[][] confusionMatrix = new int[5][5];

        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]);
            double[] y_predicted = new double[5];

            for (int i = 0; i < 5; i++) {
                y_predicted[i] = Double.parseDouble(row[i + 1]);
            }

            double pTrue = Math.max(epsilon, y_predicted[y_true - 1]);
            ceSum += -Math.log(pTrue);

            int predictedClass = 1;
            double maxProbability = y_predicted[0];

            for (int i = 1; i < 5; i++) {
                if (y_predicted[i] > maxProbability) {
                    maxProbability = y_predicted[i];
                    predictedClass = i + 1;
                }
            }

            confusionMatrix[predictedClass - 1][y_true - 1]++;
        }

        double CE = ceSum / allData.size();

        System.out.println("CE = " + CE);
        System.out.println("Confusion matrix");
        System.out.println("\t\ty=1\t\ty=2\t\ty=3\t\ty=4\t\ty=5");

        for (int i = 0; i < 5; i++) {
            System.out.print("\ty^=" + (i + 1));
            for (int j = 0; j < 5; j++) {
                System.out.print("\t" + confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }
}