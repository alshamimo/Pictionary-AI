package model;

import java.util.*;
import model.Data.*;

/**
 * This class is responsible for training a feedforward neural network using backpropagation.
 */
public class Trainer {

    private NeuralNetz neuralNetz;
    private double learningRate;
    private int epochs;
    private List<TrainingSample> trainingSamples;
    private List<TrainingSample> validationSamples;
    private int batchSize;
    private int patience;
    private int shuffleSeed;
    private double bestError;
    private String category;

    /**
     * Constructor to initialize training parameters and dataset.
     */
    public Trainer(NeuralNetz neuralNetz, double learningRate, int epochs, List<TrainingSample> trainingSamples,
                   List<TrainingSample> validationSamples, int batchSize, int patience, int shuffleSeed, String category) {
        this.neuralNetz = neuralNetz;
        this.learningRate = learningRate;
        this.epochs = epochs;
        this.trainingSamples = trainingSamples;
        this.validationSamples = validationSamples;
        this.batchSize = batchSize;
        this.patience = patience;
        this.shuffleSeed = shuffleSeed;
        this.category = category;

        this.bestError = Data.loadBestError("app/src/main/java/model/Data/" + category + "/BestError.txt");
    }

    /**
     * Trains the neural network using mini-batch gradient descent and early stopping.
     */
    public void train() {
        int epochsWithoutImprovement = 0;

        for (int epoch = 0; epoch < epochs; epoch++) {
            Collections.shuffle(trainingSamples, new Random(shuffleSeed));

            for (int batchStart = 0; batchStart < trainingSamples.size(); batchStart += batchSize) {
                int batchEnd = Math.min(batchStart + batchSize, trainingSamples.size());
                List<TrainingSample> batch = trainingSamples.subList(batchStart, batchEnd);

                double[][] weightsInputHidden = neuralNetz.getWeightsInputHidden();
                double[][] weightsHiddenOutput = neuralNetz.getWeightsHiddenOutput();
                double[] biasHidden = neuralNetz.getBiasHidden();
                double[] biasOutput = neuralNetz.getBiasOutput();

                double[][] gradWeightsIH = new double[weightsInputHidden.length][weightsInputHidden[0].length];
                double[][] gradWeightsHO = new double[weightsHiddenOutput.length][weightsHiddenOutput[0].length];
                double[] gradBiasH = new double[biasHidden.length];
                double[] gradBiasO = new double[biasOutput.length];

                for (TrainingSample sample : batch) {
                    double[] inputs = sample.input;
                    double[] targets = sample.target;
                    double[] outputs = neuralNetz.forward(inputs);
                    double[] outputErrors = MathFunctions.errorVector(targets, outputs);
                    double[] hiddenOutput = neuralNetz.getHiddenOutput();
                    double[] hiddenInput = neuralNetz.getHiddenInput();

                    double[] deltaOutputs = new double[outputErrors.length];
                    for (int i = 0; i < outputErrors.length; i++) {
                        double out = outputs[i];
                        double sigmoidDerivative = out * (1.0 - out);
                        deltaOutputs[i] = outputErrors[i] * sigmoidDerivative;
                    }

                    double[] deltaHidden = new double[hiddenOutput.length];
                    for (int i = 0; i < hiddenOutput.length; i++) {
                        double sum = 0;
                        for (int j = 0; j < deltaOutputs.length; j++) {
                            sum += weightsHiddenOutput[j][i] * deltaOutputs[j];
                        }
                        deltaHidden[i] = sum * MathFunctions.reluDerivative(hiddenInput[i]);
                    }

                    for (int i = 0; i < weightsInputHidden.length; i++) {
                        for (int j = 0; j < weightsInputHidden[i].length; j++) {
                            gradWeightsIH[i][j] += deltaHidden[i] * inputs[j];
                        }
                    }

                    for (int i = 0; i < weightsHiddenOutput.length; i++) {
                        for (int j = 0; j < weightsHiddenOutput[i].length; j++) {
                            gradWeightsHO[i][j] += deltaOutputs[i] * hiddenOutput[j];
                        }
                    }

                    for (int i = 0; i < biasOutput.length; i++) {
                        gradBiasO[i] += deltaOutputs[i];
                    }

                    for (int i = 0; i < biasHidden.length; i++) {
                        gradBiasH[i] += deltaHidden[i];
                    }
                }

                double batchFactor = learningRate / batch.size();

                for (int i = 0; i < weightsInputHidden.length; i++) {
                    for (int j = 0; j < weightsInputHidden[i].length; j++) {
                        weightsInputHidden[i][j] -= batchFactor * gradWeightsIH[i][j];
                    }
                }
                for (int i = 0; i < weightsHiddenOutput.length; i++) {
                    for (int j = 0; j < weightsHiddenOutput[i].length; j++) {
                        weightsHiddenOutput[i][j] -= batchFactor * gradWeightsHO[i][j];
                    }
                }
                for (int i = 0; i < biasOutput.length; i++) {
                    biasOutput[i] -= batchFactor * gradBiasO[i];
                }
                for (int i = 0; i < biasHidden.length; i++) {
                    biasHidden[i] -= batchFactor * gradBiasH[i];
                }

                neuralNetz.setWeightsInputHidden(weightsInputHidden);
                neuralNetz.setWeightsHiddenOutput(weightsHiddenOutput);
                neuralNetz.setBiasHidden(biasHidden);
                neuralNetz.setBiasOutput(biasOutput);
            }

            double validationError = calculateAverageError();

            if (epoch % 100 == 0 || epoch == epochs - 1) {
                System.out.printf("Epoch: %d | Validation Error: %.8f%n", epoch, validationError);
            }

            if (validationError < bestError - 1e-4) {
                bestError = validationError;
                epochsWithoutImprovement = 0;

                saveBestModel();
                System.out.println("New best model saved at Epoch " + epoch + " with error: " + bestError);
            } else {
                epochsWithoutImprovement++;
                if (epochsWithoutImprovement >= patience) {
                    System.out.println("Early stopping triggered at epoch " + epoch);
                    break;
                }
            }
        }
    }

    /**
     * Calculates the average cross-entropy loss on the validation set.
     */
    private double calculateAverageError() {
        double total = 0.0;
        for (TrainingSample s : validationSamples) {
            double[] out = neuralNetz.forward(s.input);
            total += MathFunctions.crossEntropyLoss(s.target, out);
        }
        return total / validationSamples.size();
    }

    /**
     * Saves the current best model to disk.
     */
    private void saveBestModel() {
        String basePath = "app/src/main/java/model/Data/" + category + "/";
        Data.saveBestError(bestError, basePath + "BestError.txt");
        Data.saveToFile(neuralNetz.getWeightsInputHidden(), basePath + "weightsInputHidden.txt");
        Data.saveToFile(neuralNetz.getWeightsHiddenOutput(), basePath + "weightsHiddenOutput.txt");
        Data.saveToFile(neuralNetz.getBiasHidden(), basePath + "biasHidden.txt");
        Data.saveToFile(neuralNetz.getBiasOutput(), basePath + "biasOutput.txt");
    }
}
