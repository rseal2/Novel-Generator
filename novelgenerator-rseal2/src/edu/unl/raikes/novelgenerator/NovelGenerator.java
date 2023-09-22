package edu.unl.raikes.novelgenerator;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/**
 * Automatically generates novels based on provided inspiration. File processing should be a separate class (tokenize
 * string, read file, etc.)
 * 
 */
public class NovelGenerator {

    /**
     * Generates a novel with approximately as many tokens as the requested length. The novel may be longer than the
     * requested length (it will finish its last sentence), but it will never be shorter.
     * 
     * @param length a requested minimum number of tokens of the auto-generated novel
     * @return an auto-generated novel
     */
    public static String generateNovel(int length, int k, String fileOne, String fileTwo) {
        Model model = new Model(k);
        model.learnFromFile(fileOne, k);
        model.learnFromFile(fileTwo, k);
        StringBuilder novel = new StringBuilder();

        // hash map
        Map<String, List<String>> md = model.getModel();

        Random random = new Random();
        String prev = md.get(".").get(random.nextInt(md.get(".").size()));
        novel.append(prev);
        novel.append(" ");

        // while the novel is not at the length, keep generating
        while (Reader.tokenizeString(novel.toString()).size() < length * k) {
            String temp = md.get(prev).get(random.nextInt(md.get(prev).size()));
            novel.append(temp);
            prev = temp;
            novel.append(" ");
        }
        // while the key doesn't end with proper punctuation, keep generating punctuation
        while (!(prev.endsWith(".") || prev.endsWith("!") || prev.endsWith("?"))) {
            String temp = md.get(prev).get(random.nextInt(md.get(prev).size()));
            novel.append(temp);
            novel.append(" ");
            prev = temp;
        }
        return novel.toString();
    }

    /**
     * Main function that reads files, generates models, etc.
     * 
     * @param args does not accept any args.
     */
    public static void main(String[] args) {
        System.out.println("Hello! Welcome to the Novel Generator");
        System.out.println("Please enter your desired k value for the tokenizer");
        Scanner scnr = new Scanner(System.in);

        int k = scnr.nextInt();
        while (k <= 0) {
            System.out.println("Please enter a valid k value.");
            k = scnr.nextInt();
        }
        System.out.println("Please enter the desired novel length");
        int length = scnr.nextInt();
        while (k > length || k <= 0) {
            System.out.println("Please enter a length that is greater than k or 0.");
            k = scnr.nextInt();
        }
        // printing out the novel
        System.out.println(
                generateNovel(length, k, "./TrainingTexts/AChristmasCarol.txt", "./TrainingTexts/TheOdyssey.txt"));

        scnr.close();
    }
}
