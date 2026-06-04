package model.Data;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import model.*;

/**
 * A utility class for handling data persistence operations including saving and loading
 * matrices and vectors to/from files, creating backups, and managing best error values.
 */
public class Data {

    /**
     * Central list of categories used throughout the application.
     */
    public static final String[] CATEGORIES = {"apple", "candle", "eyeglasses", "fork", "star"};

    /**
     * Saves a 2D matrix to a text file.
     * Each row of the matrix is written as a line in the file, with values separated by spaces.
     */
    public static void saveToFile(double[][] matrix, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (double[] row : matrix) {
                for (double value : row) {
                    writer.write(value + " ");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving to file: " + filename);
        }
    }

    /**
     * Saves a 1D vector to a text file.
     * All values are written on a single line, separated by spaces.
     */
    public static void saveToFile(double[] vector, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (double value : vector) {
                writer.write(value + " ");
            }
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving to file: " + filename);
        }
    }

    /**
     * Loads a 2D matrix from a text file.
     * Assumes values are whitespace-separated.
     */
    public static double[][] loadMatrixFromFile(String path, int rows, int cols) {
        double[][] matrix = new double[rows][cols];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (int i = 0; i < rows; i++) {
                String line = br.readLine();
                if (line == null) break;
                String[] values = line.trim().split("\\s+");
                for (int j = 0; j < Math.min(cols, values.length); j++) {
                    matrix[i][j] = Double.parseDouble(values[j]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from " + path + ": " + e.getMessage());
        }
        return matrix;
    }

    /**
     * Loads a 1D vector from a file containing a single line of space-separated values.
     */
    public static double[] loadVectorFromFile(String path, int length) {
        double[] vector = new double[length];
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            if (line != null) {
                String[] values = line.trim().split("\\s+");
                for (int i = 0; i < Math.min(length, values.length); i++) {
                    vector[i] = Double.parseDouble(values[i]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from " + path + ": " + e.getMessage());
        }
        return vector;
    }

    /**
     * Saves the current best error value to a file.
     * Overwrites the previous value if the file already exists.
     */
    public static void saveBestError(double currentError, String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(Double.toString(currentError));
            writer.close();
            System.out.println("New best error saved: " + currentError);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the best error value from a file.
     * If no value exists, returns Double.MAX_VALUE.
     */
    public static double loadBestError(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return Double.MAX_VALUE;
        }
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNext()) {
                String token = scanner.next();
                try {
                    return Double.parseDouble(token);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return Double.MAX_VALUE;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Double.MAX_VALUE;
    }

    /**
     * Loads a matrix from a JSON file.
     * Handles both nested matrices (List<List<List<Double>>>) and flat ones.
     */
    public static double[][] loadMatrix(String filePath) {
        try {
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            Type listOfMatricesType = new TypeToken<List<List<List<Double>>>>() {}.getType();
            List<List<List<Double>>> data = null;
            try {
                data = gson.fromJson(br, listOfMatricesType);
            } catch (Exception e) {
                br.close();
                br = new BufferedReader(new FileReader(filePath));
            }

            if (data != null && !data.isEmpty()) {
                List<List<Double>> firstMatrixList = data.get(0);
                return convertListToMatrix(firstMatrixList);
            } else {
                Type matrixType = new TypeToken<List<List<Double>>>() {}.getType();
                List<List<Double>> matrixList = gson.fromJson(br, matrixType);
                return convertListToMatrix(matrixList);
            }

        } catch (IOException e) {
            System.out.println("Failed to load file: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts a list of lists into a 2D matrix.
     */
    private static double[][] convertListToMatrix(List<List<Double>> list) {
        int rows = list.size();
        int cols = list.get(0).size();
        double[][] matrix = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            List<Double> row = list.get(i);
            for (int j = 0; j < cols; j++) {
                matrix[i][j] = row.get(j);
            }
        }
        return matrix;
    }

    /**
     * Loads a list of vectors from a JSON file.
     * Used to load input features from converted QuickDraw samples.
     */
    public static List<double[]> loadVectorsFromJson(String filePath) {
        List<double[]> vectors = new ArrayList<>();
        try {
            Gson gson = new Gson();
            BufferedReader br = new BufferedReader(new FileReader(filePath));

            Type listType = new TypeToken<List<List<Double>>>() {}.getType();
            List<List<Double>> rawVectors = gson.fromJson(br, listType);

            for (List<Double> rawVector : rawVectors) {
                double[] vector = new double[rawVector.size()];
                for (int i = 0; i < rawVector.size(); i++) {
                    vector[i] = rawVector.get(i);
                }
                vectors.add(vector);
            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vectors;
    }

    /**
     * Loads training data in binary classification format.
     * 40% of the samples come from the positive class, 60% from others.
     *
     * @param totalSamples total number of training examples to return
     * @param pathString base path to the JSON vector files
     * @param positiveCategory the filename of the category to label as positive
     * @return a shuffled list of training samples with balanced positive/negative distribution
     */
    public static List<TrainingSample> loadTrainingSamples(int totalSamples, String pathString, String positiveCategory) {
        Random random = new Random();
        int numCategories = CATEGORIES.length;

        int posCount = (int) (0.4 * totalSamples);
        int negTotal = totalSamples - posCount;
        int negPerCat = negTotal / (numCategories - 1);
        int negRemainder = negTotal % (numCategories - 1);

        Map<String, List<double[]>> dataMap = new HashMap<>();
        for (String category : CATEGORIES) {
            String catFile = category + "_vector_14.json";
            String fullPath = pathString + catFile;
            List<double[]> vecs = Data.loadVectorsFromJson(fullPath);
            if (vecs == null || vecs.isEmpty()) {
                throw new RuntimeException("Could not load data from: " + catFile);
            }
            dataMap.put(category, vecs);
        }

        List<TrainingSample> samples = new ArrayList<>(totalSamples);

        // Add positive samples
        List<double[]> posVecs = dataMap.get(positiveCategory);
        if (posVecs == null) {
            throw new RuntimeException("Positive category data not found for: " + positiveCategory);
        }
        for (int i = 0; i < posCount; i++) {
            double[] input = posVecs.get(random.nextInt(posVecs.size()));
            double[] output = new double[] {1.0, 0.0};
            samples.add(new TrainingSample(input, output));
        }

        // Add negative samples
        for (String category : CATEGORIES) {
            if (category.equals(positiveCategory)) continue;

            int count = negPerCat;
            if (negRemainder > 0) {
                count++;
                negRemainder--;
            }

            List<double[]> vecs = dataMap.get(category);
            for (int i = 0; i < count; i++) {
                double[] input = vecs.get(random.nextInt(vecs.size()));
                double[] output = new double[] {0.0, 1.0};
                samples.add(new TrainingSample(input, output));
            }
        }

        Collections.shuffle(samples, random);
        return samples;
    }
}


