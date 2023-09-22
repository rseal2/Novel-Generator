package edu.unl.raikes.novelgenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class that creates and trains a model.
 *
 */
public class Model {
    public int k;
    private Map<String, List<String>> model = new HashMap<String, List<String>>();

    /**
     * Constructor for a model object.
     * 
     * @param model is the model.
     * @param k is the length of the sequences.
     */
    public Model(int k) {
        this.k = k;
    }

    /**
     * Learns an author's style. Returns a map with each unique word in the input as a key. The map values are Lists of
     * the tokens that follow the key in the text.
     * 
     * @param input an ordered list of tokens
     */
    public void trainModel(List<String> input, int k) {
        // input is the ordered list of tokens
        List<String> combined = null;
        List<String> starters = new ArrayList<String>();

        // if key already has list add new token to list
        for (int i = 0; i < input.size(); i++) {
            if ((input.get(i).endsWith(".") || input.get(i).endsWith("?") || input.get(i).endsWith("!"))) {
                starters.add(input.get((i + k) % input.size()));
            }
            if (this.model.get(input.get(i)) == null) {
                // if it doesn't then make a new list
                combined = new ArrayList<String>();
                combined.add(input.get((i + k) % input.size()));
                this.model.put(input.get(i), combined);
            } else {
                combined = this.model.get(input.get(i));
                combined.add(input.get((i + k) % input.size()));
                this.model.put(input.get(i), combined);
            }
        }
        // changed this and the if statement
        this.model.put(".", starters);
        this.model.put("?", starters);
        this.model.put("!", starters);
    }

    /**
     * Reads a file, tokenizes it, and uses the tokens to train the NovelGenerator model.
     * 
     * @param filepath the filepath (including the filename) of the file from which to learn
     */
    public void learnFromFile(String filepath, int k) {
        // call read file
        // call tokenize on the return string from read file
        // generate sequences based off of the tokens returned from the string
        // train the model
        this.trainModel(Reader.generateSequences(Reader.tokenizeString(Reader.readFile(filepath)), k), k);
    }

    /**
     * Gets the training model (convenience method for use by unit testing functions).
     * 
     * @return the model
     */
    public Map<String, List<String>> getModel() {
        return this.model;
    }
}
