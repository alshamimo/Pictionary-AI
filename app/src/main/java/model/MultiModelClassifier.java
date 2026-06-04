package model;

import java.util.HashMap;
import java.util.Map;

import model.Data.Data;

/**
 * Manages multiple neural network models for different categories
 * and allows simultaneous classification of an input image.
 */
public class MultiModelClassifier {

    private Map<String, NeuralNetz> models;
    private String[] categories = Data.CATEGORIES;

    /**
     * Constructor: Loads all 5 models
     */
    public MultiModelClassifier() {
        models = new HashMap<>();
        loadAllModels();
    }

    /**
     * Loads all category-specific models
     */
    private void loadAllModels() {
        for (String category : categories) {
            models.put(category, loadModel(category));
        }
    }

    /**
     * Loads a single model for the given category
     *
     * @param category category name (e.g., "apple")
     * @return initialized NeuralNetz model
     */
    private NeuralNetz loadModel(String category) {
        // Parameters for all models: 196 inputs, 5 hidden neurons, 2 outputs
        NeuralNetz model = new NeuralNetz(196, 5, 2, category);
        return model;
    }

    /**
     * Classifies an input image with all models
     *
     * @param input input image as a flat array (length 196 for 14x14)
     * @return Map of category name to [positive, negative] probability
     */
    public Map<String, double[]> classifyWithAllModels(double[] input) {
        Map<String, double[]> results = new HashMap<>();

        for (String category : categories) {
            NeuralNetz model = models.get(category);
            double[] output = model.forward(input);
            results.put(category, output);
        }

        return results;
    }

    /**
     * Returns the name of the most likely category
     *
     * @param input input image as a flat array (length 196)
     * @return category with the highest positive probability
     */
    public String getBestMatch(double[] input) {
        Map<String, double[]> allResults = classifyWithAllModels(input);
        String bestCategory = "";
        double bestProbability = 0.0;

        for (Map.Entry<String, double[]> entry : allResults.entrySet()) {
            // Index 0 is the "yes" probability (see loadTrainingSamples logic)
            double probability = entry.getValue()[0];
            if (probability > bestProbability) {
                bestProbability = probability;
                bestCategory = entry.getKey();
            }
        }

        return bestCategory;
    }

    /**
     * Returns an array of positive probabilities for all categories
     * (used for visualization in the GUI).
     *
     * @param input input image as a flat array (length 196)
     * @return array of probabilities corresponding to each category
     */
    public double[] getAllCategoryProbabilities(double[] input) {
        Map<String, double[]> allResults = classifyWithAllModels(input);
        double[] probabilities = new double[categories.length];

        for (int i = 0; i < categories.length; i++) {
            // Index 0 is the "yes" probability
            probabilities[i] = allResults.get(categories[i])[0];
        }

        return probabilities;
    }
}
