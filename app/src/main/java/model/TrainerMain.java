package model;

import java.util.*;

import model.Data.*;

/**
 * Main class to train and test a neural network classifier.
 */
public class TrainerMain {

    public static void main(String[] args) {
        String pathTrain = "model/Data/M/quickdraw_project/converted_vectors_train/";
        String pathTest = "model/Data/M/quickdraw_project/converted_vectors_test/";

        // Load training and validation samples (binary classification: star vs. not-star)
        List<TrainingSample> trainingSamples = Data.loadTrainingSamples(2000, pathTrain, "star");
        List<TrainingSample> validationSamples = Data.loadTrainingSamples(200, pathTest, "star");

        // Initialize neural network with 196 inputs, 5 hidden neurons, and 2 output classes
        NeuralNetz neuralNetz = new NeuralNetz(196, 5, 2, "star");

        // Train the model
        Trainer trainer = new Trainer(neuralNetz, 0.0001, 3000, trainingSamples, validationSamples, 16, 300, 42, "star");
        trainer.train();

        // Run basic sanity check
        sanityCheckSamples(10, neuralNetz, validationSamples);
    }

    /**
     * Helper method to print an array nicely
     */
    public static void printArray(double[] a) {
        System.out.print("[");
        for (int i = 0; i < a.length; i++) {
            System.out.printf("%.4f", a[i]);
            if (i < a.length - 1) System.out.print(",  ");
        }
        System.out.println("]");
    }

    /**
     * Prints out predictions and corresponding targets for a few samples
     * to visually verify that training works correctly.
     */
    public static void sanityCheckSamples(int n, NeuralNetz neuralNetz, List<TrainingSample> validationSamples) {
        System.out.println("\nSanity Check Predictions:");
        for (int i = 0; i < n && i < validationSamples.size(); i++) {
            TrainingSample sample = validationSamples.get(i);
            double[] prediction = neuralNetz.forward(sample.input);
            System.out.print("Prediction: ");
            printArray(prediction);
            System.out.print("Target:     ");
            printArray(sample.target);
            System.out.println();
        }
    }
}
