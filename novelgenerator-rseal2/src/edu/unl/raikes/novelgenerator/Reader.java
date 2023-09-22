package edu.unl.raikes.novelgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reader class reads files and splits them into size.
 * 
 *
 */
public class Reader {

    /**
     * Reads and returns all contents of a file.
     * 
     * @param filepath the filepath (including the filename) of the file to read
     * @return a string containing all contents of the file
     */
    public static String readFile(String filepath) {
        // read the file, loop through contents, append to one big string
        Scanner input = null;
        try {
            // general file path not specific location
            input = new Scanner(new File(filepath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder fileString = new StringBuilder();
        while (input.hasNext()) {
            fileString.append(input.next());
            fileString.append(" ");
        }
        input.close();
        return fileString.toString();
    }

    /**
     * Accepts a string and returns the tokens (words, paragraph breaks, sentence-ending punctuation).
     * 
     * @param input a string that needs to be tokenized
     * @return an ordered List of tokens
     */
    public static List<String> tokenizeString(String input) {
        List<String> results = new ArrayList<String>();

        // fix issues if any input documents use Windows line endings
        input = input.replaceAll("\\r\\n?", "\n");

        // reduce any instance of two or more newlines to only two newlines
        input = input.replaceAll("(\\n\\n+)", "\n\n");

        // use regular expressions to tokenize string
        // this regular expression has 6 options:
        // [a-zA-Z]+'?[a-zA-z]+ : a word of length >=2 or a contraction of length >= 3
        // [a-zA-z] : single-character alphanumeric words
        // [.] : a period
        // [?] : a question mark
        // [!] : an exclamation point
        // [\\n]{2,} : two newlines
        Pattern pattern = Pattern.compile("[a-zA-Z]+'?[a-zA-z]+|[a-zA-z]|[.]|[?]|[!]|[\\n]{2,}");
        Matcher matcher = pattern.matcher(input);

        // collect results
        while (matcher.find()) {
            results.add(matcher.group());
        }

        // return results
        return results;
    }

    /**
     * Generating sequences of k length.
     * 
     * @param tokens is the list of tokens generated from string tokenizer.
     * @param k is the length of the sequences.
     * @param filepath is the file that is being read.
     * @return sequences of k words.
     */
    public static List<String> generateSequences(List<String> tokens, int k) {
        List<String> sequences = new ArrayList<String>();
        // looping through tokens
        for (int i = 0; i < tokens.size(); i++) {
            // string builder 
            StringBuilder str = new StringBuilder();
            for (int j = i; j < i + k; j++) {
                // current word is j mod the size of tokens
                String currWord = tokens.get(j % tokens.size());
                // adding current word
                str.append(currWord);
                if (j != i + k - 1) {
                    str.append(" ");
                }
            }
            sequences.add(str.toString());
        }
        return sequences;
    }

}
