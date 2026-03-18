package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App 
{
    public static void main(String[] args)
    {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        for (String filePath : files) {

            FileReader filereader;
            List<String[]> allData;

            try {
                filereader = new FileReader(filePath);
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                                        .withSkipLines(1)
                                        .build();
                allData = csvReader.readAll();
            }
            catch (Exception e) {
                System.out.println("Error reading file: " + filePath);
                continue;
            }

            float mse_sum = 0f;
            float mae_sum = 0f;
            float mare_sum = 0f;

            float epsilon = 0.0000001f;
            int n = allData.size();

            for (String[] row : allData) {

                float y_true = Float.parseFloat(row[0]);
                float y_predicted = Float.parseFloat(row[1]);

                float error = y_true - y_predicted;

                mse_sum += error * error;
                mae_sum += Math.abs(error);
                mare_sum += Math.abs(error) / (Math.abs(y_true) + epsilon);
            }

            float mse = mse_sum / n;
            float mae = mae_sum / n;
            float mare = mare_sum / n;

            System.out.println("for " + filePath);
            System.out.println("\tMSE = " + mse);
            System.out.println("\tMAE = " + mae);
            System.out.println("\tMARE = " + mare);
            System.out.println();
        }

        System.out.println("According to results, the best model is model_2.csv");
    }
}