package model;

import java.util.*;
import model.Data.*;

/**
 * This class represents a simple feedforward neural network with one hidden layer.
 */
public class NeuralNetz {

    private double[][] weightsInputHidden;
    private double[] biasHidden;
    private double[] hiddenOutput;
    private double[] hiddenInput;

    private double[][] weightsHiddenOutput;
    private double[] biasOutput;

    private int inputSize;
    private int hiddenSize;
    private int outputSize;

    /**
     * Constructor that initializes the network by loading weights and biases from files.
     *
     * @param inputs     number of input nodes
     * @param hidden     number of hidden nodes
     * @param outputs    number of output nodes
     * @param category   the category name to locate the correct model files
     */
    public NeuralNetz(int inputs, int hidden, int outputs, String category) {
        this.inputSize = inputs;
        this.hiddenSize = hidden;
        this.outputSize = outputs;
        
        // Use an absolute path to ensure files are found regardless of working directory
        String userDir = System.getProperty("user.dir");
        String basePath;
        if (userDir.endsWith("app")) {
            basePath = userDir + "/src/main/java/model/Data/" + category + "/";
        } else {
            basePath = userDir + "/app/src/main/java/model/Data/" + category + "/";
        }
        
        this.biasHidden = Data.loadVectorFromFile(basePath + "biasHidden.txt", hidden);
        this.weightsInputHidden = Data.loadMatrixFromFile(basePath + "weightsInputHidden.txt", hidden, inputs);
        this.hiddenOutput = new double[hidden];
        this.weightsHiddenOutput = Data.loadMatrixFromFile(basePath + "weightsHiddenOutput.txt", outputs, hidden);
        this.biasOutput = Data.loadVectorFromFile(basePath + "biasOutput.txt", outputs);
    }

    /**
     * Sets the weights between input and hidden layer.
     */
    public void setWeightsInputHidden(double[][] updateWeightsInputHidden) {
        this.weightsInputHidden = updateWeightsInputHidden;
    }

    /**
     * Returns the weights between input and hidden layer.
     */
    public double[][] getWeightsInputHidden() {
        return this.weightsInputHidden;
    }

    /**
     * Sets the biases for the hidden layer.
     */
    public void setBiasHidden(double[] updateBiasHidden) {
        this.biasHidden = updateBiasHidden;
    }

    /**
     * Returns the biases for the hidden layer.
     */
    public double[] getBiasHidden() {
        return this.biasHidden;
    }

    /**
     * Returns the output values from the hidden layer.
     */
    public double[] getHiddenOutput() {
        return this.hiddenOutput;
    }

    /**
     * Sets the weights between hidden and output layer.
     */
    public void setWeightsHiddenOutput(double[][] updateWeightsHiddenOutput) {
        this.weightsHiddenOutput = updateWeightsHiddenOutput;
    }

    /**
     * Returns the weights between hidden and output layer.
     */
    public double[][] getWeightsHiddenOutput() {
        return this.weightsHiddenOutput;
    }

    /**
     * Returns the raw input values to the hidden layer (before activation).
     */
    public double[] getHiddenInput() {
        return this.hiddenInput;
    }

    /**
     * Sets the biases for the output layer.
     */
    public void setBiasOutput(double[] updateBiasOutput) {
        this.biasOutput = updateBiasOutput;
    }

    /**
     * Returns the biases for the output layer.
     */
    public double[] getBiasOutput() {
        return this.biasOutput;
    }

    /**
     * Creates a random matrix initialized with small Gaussian values scaled by input size.
     */
    public double[][] randomMatrix(int size1, int size2) {
        Random rand = new Random();
        double[][] result = new double[size1][size2];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                double scale = Math.sqrt(2.0 / size2);
                result[i][j] = rand.nextGaussian() * scale;
            }
        }
        return result;
    }

    /**
     * Creates a random vector initialized with small Gaussian values scaled by size.
     */
    public double[] randomVector(int number) {
        Random rand = new Random();
        double[] result = new double[number];
        for (int i = 0; i < number; i++) {
            double scale = Math.sqrt(2.0 / number);
            result[i] = rand.nextGaussian() * scale;
        }
        return result;
    }

    /**
     * Performs forward propagation on the input and returns the output after applying softmax.
     */
    public double[] forward(double[] inputs) {
        hiddenInput = MathFunctions.add(MathFunctions.multiply(weightsInputHidden, inputs), biasHidden);
        hiddenOutput = MathFunctions.applyReLU(hiddenInput);
        double[] finalInput = MathFunctions.add(MathFunctions.multiply(weightsHiddenOutput, hiddenOutput), biasOutput);
        return MathFunctions.softmax(finalInput);
    }
}
