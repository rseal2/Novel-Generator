package edu.unl.raikes.novelgenerator.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.unl.raikes.novelgenerator.Model;
import edu.unl.raikes.novelgenerator.NovelGenerator;
import edu.unl.raikes.novelgenerator.Reader;

public class NovelGeneratorTest {

    @Test
    public void testGenerateSequencesWithEvenKValue() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 2;

        tokens.add("Hello");
        tokens.add("World");
        tokens.add("My");
        tokens.add("Name");
        tokens.add("Is");
        tokens.add("Raimee");

        List<String> expected = new ArrayList<String>();
        expected.add("Hello World");
        expected.add("World My");
        expected.add("My Name");
        expected.add("Name Is");
        expected.add("Is Raimee");
        expected.add("Raimee Hello");

        // execute
        List<String> actual = Reader.generateSequences(tokens, k);

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("The sequence generator is not running as expected for even k values", expected.get(i),
                    actual.get(i));
        }
    }

    @Test
    public void testGenerateSequencesWithOddKValue() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 3;

        tokens.add("Hello");
        tokens.add("World");
        tokens.add("I'm");
        tokens.add("Raimee");

        List<String> expected = new ArrayList<String>();
        expected.add("Hello World I'm");
        expected.add("World I'm Raimee");
        expected.add("I'm Raimee Hello");
        expected.add("Raimee Hello World");

        // execute
        List<String> actual = Reader.generateSequences(tokens, k);

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("The sequence generator is not running as expected for odd k values", expected.get(i),
                    actual.get(i));
        }
    }

    @Test
    public void testGenerateSequencesWithPeriods() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 2;

        tokens.add("Hello");
        tokens.add("World");
        tokens.add(".");
        tokens.add("I'm");
        tokens.add("Raimee");
        tokens.add(".");

        List<String> expected = new ArrayList<String>();
        expected.add("Hello World");
        expected.add("World .");
        expected.add(". I'm");
        expected.add("I'm Raimee");
        expected.add("Raimee .");
        expected.add(". Hello");

        // execute
        List<String> actual = Reader.generateSequences(tokens, k);

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("The sequence generator is not running as expected for sequences with periods",
                    expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testGenerateSequencesIfKIsTheLengthOfTokens() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 2;

        tokens.add("Hello");
        tokens.add("World");

        List<String> expected = new ArrayList<String>();
        expected.add("Hello World");
        expected.add("World Hello");

        // execute
        List<String> actual = Reader.generateSequences(tokens, k);

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("The sequence generator is not running as expected for k values that are the length of tokens",
                    expected.get(i), actual.get(i));
        }
    }

    @Test
    public void testTrainModelWithOnePhrase() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 2;
        tokens.add("Hello");
        tokens.add("World");
        tokens.add("I'm");
        tokens.add("Raimee");
        tokens.add("Hello");
        tokens.add("I'm");
        tokens.add("Coding");

        List<String> combined = new ArrayList<String>();
        combined.add(tokens.get(1));
        combined.add(tokens.get(5));

        Map<String, List<String>> expected = new HashMap<String, List<String>>();
        expected.put("Hello", combined);

        Model model = new Model(k);

        // execute
        model.trainModel(tokens, k);
        Map<String, List<String>> actual = model.getModel();

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("Train model is not mapping the words as expected", actual.get(i), expected.get(i));
        }
    }

    @Test
    public void testTrainModelWithLongPhrases() {
        // setup
        List<String> tokens = new ArrayList<String>();
        int k = 3;
        tokens.add("Hello World");
        tokens.add("World I'm");
        tokens.add("I'm Raimee");
        tokens.add("Raimee Hello");
        tokens.add("Hello I'm");
        tokens.add("I'm Coding");
        tokens.add("Coding Hello");

        List<String> combined = new ArrayList<String>();
        combined.add(tokens.get(1));
        combined.add(tokens.get(5));

        Map<String, List<String>> expected = new HashMap<String, List<String>>();
        expected.put("Hello", combined);

        Model model = new Model(k);

        // execute
        model.trainModel(tokens, k);
        Map<String, List<String>> actual = model.getModel();

        // test
        for (int i = 0; i < actual.size(); i++) {
            assertEquals("Train model is not mapping the words as expected", actual.get(i), expected.get(i));
        }
    }

    @Test
    public void checkReadFileReadsCorrectly() {
        // setup
        String expected = ("This is a test file? I am testing for question marks? ");

        // execute
        String actual = Reader.readFile("./TrainingTexts/TestingFile.txt");

        // test
        assertEquals("The file was not found, so an exception was thrown", expected, actual);
    }

    @Test
    public void checkGenerateNovelGeneratesNovel() {
        // setup
        int length = 20;
        int k = 2;

        // execute
        String actual = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFile.txt",
                "./TrainingTexts/TestingFileTwo.txt");

        // checking to see if the novel generator generates an appropriate length
        boolean valid;

        if (actual.length() > length) {
            valid = true;
        } else {
            valid = false;
        }
        // test
        assertTrue("The novel generator function is not generating an appropriate length", valid);
    }

    @Test
    public void checkNovelEndsWithPeriod() {
        // setup
        int length = 20;
        int k = 2;
        char expected = '.';

        // execute
        String novel = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFileTwo.txt",
                "./TrainingTexts/TestingFileTwo.txt");
        char actual = novel.charAt(novel.length() - 2);

        // test
        assertSame("The novel generator function is not ending with a period", expected, actual);
    }

    @Test
    public void checkNovelCanEndWithAQuestionMark() {
        // setup
        int length = 20;
        int k = 2;
        char expected = '?';

        // execute
        String novel = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFile.txt",
                "./TrainingTexts/TestingFile.txt");
        char actual = novel.charAt(novel.length() - 2);

        // test
        assertSame("The novel generator function is not ending with a question mark", expected, actual);
    }

    @Test
    public void checkNovelCanEndWithAnExclamationMark() {
        // setup
        int length = 20;
        int k = 2;
        char expected = '!';

        // execute
        String novel = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFileThree.txt",
                "./TrainingTexts/TestingFileThree.txt");
        char actual = novel.charAt(novel.length() - 2);

        // test
        assertSame("The novel generator function is not ending with an exclamation mark", expected, actual);
    }

    @Test
    public void checkNovelStartsWithACapitalLetter() {
        // setup
        int length = 20;
        int k = 2;

        // execute

        String novel = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFile.txt",
                "./TrainingTexts/TestingFileTwo.txt");
        char start = novel.charAt(0);
        boolean actual;
        if (Character.isUpperCase(start)) {
            actual = true;
        } else {
            actual = false;
        }

        // test
        assertTrue("The novel is not starting with an uppercase letter", actual);
    }

    @Test
    public void testLengthGivesProperLength() {
        // setup
        int length = 20;
        int k = 2;

        // execute
        String novel = NovelGenerator.generateNovel(length, k, "./TrainingTexts/TestingFileThree.txt",
                "./TrainingTexts/TestingFileThree.txt");

        boolean valid;
        if (Reader.tokenizeString(novel.toString()).size() >= length * k) {
            valid = true;
        } else {
            valid = false;
        }

        // test
        assertTrue("The novel generator function is not generating an appropriate length based on tokens", valid);
    }

    @Test
    public void testKeyAndValuesAreCorrect() {
        // setup
        int k = 2;
        Model model = new Model(k);
        ArrayList<String> hello = new ArrayList<String>();
        hello.add("world");
        hello.add("code");
        ArrayList<String> world = new ArrayList<String>();
        world.add("code");
        world.add("hello");
        ArrayList<String> code = new ArrayList<String>();
        code.add("hello");
        code.add("world");

        Map<String, List<String>> expected = new HashMap<String, List<String>>();
        expected.put("hello", hello);
        expected.put("world", world);
        expected.put("code", code);

        // execute
        Map<String, List<String>> actual = model.getModel();

        // test
        for (String key : actual.keySet()) {
            assertEquals("The keys and values of the map are not generating as expected", expected.get(key),
                    actual.get(key));
        }
    }

    @Test
    public void testCreatingModelFromMultipleFilesIncreasesTheSizeOfTheMap() {
        // setup
        int k = 2;
        Model model = new Model(k);

        // execute
        model.learnFromFile("./TrainingTexts/keyTestingFile.txt", k);
        int before = model.getModel().keySet().size();
        model.learnFromFile("./TrainingTexts/TestingFile.txt", k);
        int after = model.getModel().keySet().size();

        // test
        assertNotEquals("The size of the map is not increasing as expected", before, after);
    }

    @Test
    public void testModelIsSameWhenInputDocumentIsLessThanKTokensLong() {
        // setup
        int k = 5;
        Model model = new Model(k);
        int expected = 6;

        // execute
        model.learnFromFile("./TrainingTexts/keyTestingFile.txt", k);
        int actual = model.getModel().size();

        // test
        assertEquals("The model is not the same when the input document is less than k tokens long", expected, actual);
    }
}
