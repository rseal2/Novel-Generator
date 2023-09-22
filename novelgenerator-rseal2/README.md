# Novel Generator

In this assignment, your task is to write the next great classic English novel. No pressure. Because you're actually pretty busy at the moment, you've decided that you are going to train your computer to write the first draft of the novel for you.

Your task is to write a computer program that feeds on the publicly available works of authors that inspire you, books that include themes similar to those you're hoping to include in your novel, and your program will attempt to author another book similar to the inspiration.

For example, let's say you want to write a book about manners, marriage, and mystery midnight mischief, you might want to feed your program one or two of Jane Austen's novels (_Pride and Prejudice_ and _Emma_, for example) along with _The Legend of Sleepy Hollow_ by Washington Irving. Your program will record the patterns of words used in these texts and generate a new text that follows the same patterns.

### Training Your Model

In order for your machine to automatically generate a rough draft of a novel for you, it needs to learn some patterns. _(Did you catch the words "machine" and "learn" in that sentence? You're doing machine learning in this assignment!)_ In order to learn the word patterns (in machine learning, this is called "training"), your program will read through some books/texts that have linguistic patterns similar to those you want your novel to display (a.k.a. "training texts"). The goal of this "training" is to create an something that stores and records the patterns, which is called a "model".

Before creating a model, we have to perform something called _tokenization_, which is the splitting of a string up into an array individual "tokens," similarly to what we had to do in the Catching Plagiarists assignment first semester. This has already been provided to you in a method called `tokenizeString()`, but it should be noted that, for this assignment, tokens can be a word, `\n\n` (a "paragraph break"), `.`, `?`, or `!`.

After this tokenization is complete, you can go ahead and build the model. Our model is a map data structure that maps a `String` to a `List<String>`; the map key is a k-token phrase of words/tokens and the map value is all of the different k-token phrases of words/tokens that follow the key in the training texts. For example, with a k of 1, if the words `dog runs` occur together in your training text, the model map will contain an element with a key of `dog`, whose value is a list that contains the word `runs`, and the map will contain an element with the key of `runs` with the value of whatever directly follows.

**Consider this slightly larger example. If your training text was the following:**

> _The dog went to the dog groomer in the PetsMart close to The Nebraska Crossing Outlets. The dog is clean._

**The model might look something like this (k=1):**

Model Key (String)  | Model Value (List of Strings)
----------- | ------
`.`         | `The` ,`The`
`The`       | `dog`, `Nebraska`, `dog`
`dog`       | `went`, `groomer`, `is`
`went`      | `to`
`to`        | `the`, `The`
`the`       | `dog`, `PetSmart`
`groomer`   | `in`
`in`        | `the`
`PetSmart`  | `close`
`close`     | `to`
`Nebraska`  | `Crossing`
`Crossing`  | `Outlets`
`Outlets`   | `.`
`is`        | `clean`
`clean`     | `.`

Notice a few things:

1. The word `dog` follows the word `The` twice in the training text. Therefore `dog` appears twice in the list of words that have been found to follow `The`. This repetition is expected and helps with ensuring that more popular words appear in the auto-generated novel with an appropriate frequency.
2. Capitalization matters. Both `The` and `the` are listed as separate keys to the map.
3. **Handling the fencepost problem:** The first word in the training text was automatically added to the list of words that have been found to follow the `.` token. This is because, when generating a novel, every possible key **must** have a possible matching value, or your program will be susceptible to random null pointer exceptions (for reasons that will become clear later). 

Because using a model based on one-to-one sequences of tokens (one token is the key in the map, and the following one token is added to the list of values) is likely to give you jibberish, your application should provide a user the option to build their model based on k-to-k sequences of tokens (k tokens as key in map, k tokens added to list of values). Let's look at the same text input with k=2!

**The same model instead with k=2:**

Model Key (String)  | Model Value (List of Strings)
------------------- | ------
`The dog`           | `went to`, `is clean`
`dog went`          | `to the`
`went to`           | `the dog`
`to the`            | `dog groomer`
`the dog`           | `groomer in`
`dog groomer`       | `in the`
`groomer in`        | `the PetsMart`
`in the`            | `PetsMart close`
`the PetsMart`      | `close to`
`PetsMart close`    | `to The`
`close to`          | `The Nebraska`
`to The`            | `Nebraska Crossing`
`The Nebraska`      | `Crossing Outlets`
`Nebraska Crossing` | `Outlets .`
`Crossing Outlets`  | `. The`
`Outlets .`         | `The dog`
`. The`             | `dog is`, `dog went`
`dog is`            | `clean .`
`is clean`          | `. The`
`clean .`           | `The dog`

Notice a few things that came into play when increasing k above 1:

1. Both the key and value have k tokens.
2. When one of the tokens is a period, there is still a space (`clean .` instead of `clean.`). _It is fine if this "quirk" shows up in your program output—see the end of this spec for more info._
3. **Handling the fencepost problem:** If you remember from the example with k=1, we made the last token (`.`) map to the first token (`The`). Again, this is to prevent possible null pointer exceptions. 
    
    This time, instead of just mapping the last token to the first token, you must "wrap around" to create the following pairs (hint: this should be simple to do with the modulus/`%` operator):
    - `is clean` → `. The` _(the last three tokens and the first token)_
    -  `clean .` → `The dog` _(the last two tokens and the first two tokens)_
    - `. The` → `dog is` _(the last token and the first three tokens)_

### Using Your Model to Generate the Novel

Once your model is fully trained, it is time to start generating your own novel! You don't want your novel to start in the middle of a sentence. A creative way to ensure you start at the beginning of a sentence is to randomly pick a key in our model (map) that ends with a end-of-sentence punctuation mark (period, exclamation mark, or question mark). When you find one of these, you know that all of the elements in the `List<String>` are all suitable to start sentences.

With the above example with k=1, you'd just look at the element with the key `.`, meaning that you would randomly pick between `The` and `The` (_a real exciting decision, I know_). For the above example with k=2, both `Outlets .` and `clean .` end with punctuation, so you can randomly choose between... `The dog` and `The dog` (_even more exciting_).

After your program picks its starting token/series of tokens (`The` for k=1 and `The dog` for k=2), it will **randomly** pick the next k tokens using your model using `The` or `The dog` as the key (depending on the k value). In the case of k=1, the next token can be `dog`, `Nebraska`, or `dog`, and in the case of k=2, the next set of 2 tokens can be `went to` or `is clean`. This process continues until you choose to stop generating your novel.

Given the previous example, a possible auto-generated sentence using k=1 could be:

> _The dog went to The Nebraska Crossing Outlets._

or

> _The Nebraska Crossing Outlets._

or

> _The dog went to the PetsMart close to The dog is clean._

Not all of these sentences would win a [Pulitzer Prize](https://en.wikipedia.org/wiki/Pulitzer_Prize), but all three are completely valid for your program to generate!

### Program Input and Output

You will generate a novel of a user-specified minimum length of tokens (yes, you need to prompt the user for this number and yes, you need to validate it). You also need to ask the user for the `k` value that they want (yes, you also need to validate this number).

Once you have generated a novel of the requested length, continue auto-generating additional text until the next end-of-sentence punctuation is reached (a period, question mark, or exclamation point). Then, you'll have automatically generated a novel! You can either print your novel to the console or save your novel in a new file ***and*** print it to the console.

**Two notes about output length:**

1. Remember that the requested length is a minimum. For particularly high k values, the "actual" length of the output might be much longer, as it's harder to find a random series of 10 tokens ending with end-of-sentence punctuation than it is to find a random series of 2 tokens ending with end-of-sentence punctuation.
2. Note that the length is based on **tokens** and not iterations of a loop... if the user asks for a novel of length 100 with k=5, the program should strive to generate a novel of length 100, not of length 500.

### A Guide to Getting Started

Phew, that was a lot! Luckily, we've broken down the steps you need to take to complete this assignment. At a high level, here's what you need to do:

-   First, pick out a few (at least two) books on Project Gutenberg (<http://www.gutenberg.org/>). Download plaintext copies of the books and add them to the "training_texts" folder in your repository. I recommend using prose sources because auto-generated poetry or plays will not turn out as well.
    - **YOU MAY NOT USE ANY COPYRIGHTED MATERIAL IN THIS ASSIGNMENT.**
    - You can train your model on at least two training texts, but if you want to include more, you are welcome to do so (though please ensure your program runs in &lt; 30 seconds for grading purposes). The more training texts you employ, the more likely your novel will be sensical.
    - Make sure your training text isn't too short (e.g. a training text with only 4 words can cause issues with a k value of of 5).
    - Make sure that your training text has at least one end-of-sentence punctuation mark (this has caused issues prior years with poems).
-   Then, start filling in the provided functions in NovelGenerator.java:
    -   Write the `readFile()` function that reads in a file and returns the contents of the file as a String.
    -   Write the `trainModel()` function that trains your model on a series of tokens passed in. 
    -   Write a function that generates a list of k-word sequences. Your function should use the provided `tokenizeString()` function, which returns individual tokens.
    -   Write the `learnFromFile()` function that combines the functionality of the previous three functions.
    -   _Check yourself_ - how many unit tests have you written up to this point? If you haven't written any, you should modify your habits to test as you develop!
    -   In the `main()` function, create a new NovelGenerator object.
    -   Call the `learnFromFile()` function for each of the training texts that you selected (remember to use good software engineering principles for this). **Make sure that, with each new training text, you add to the existing model instead of overwriting it** (this is a common mistake made by students).
    -   Write the `generateNovel()` function that uses your model to generate a new novel of appropriate length.
-   Write a unit test that trains a model on a small testing file and generates a short novel. Ensure that the sequence of tokens in the generated novel could have been generated from the given model, and ensure that it ends appropriately.
-   Ensure that your `main()` function prompts the user for their desired novel length and their desired k.
-   Ensure that your `main()` function generates a novel of the user's desired length and outputs that novel to the user (either by printing it to the console or by saving it in a new file ***and*** printing it to the console).
-   As you look at the current class structure in the repository, it should make you uncomfortable. Refactor into multiple classes that follow the single-responsibility principle, and ensure that your functions are fewer than 100 lines long!
-   Save your favorite auto-generated novel as favoriteNovel.txt file in the top level of your repository (in the same folder as this README file).

### A Note About Tokenization
#### How Tokenization Works

Though the tokenization function has already been written for you, consider the following definition of a "token."

A token is one of the following

-   a case-sensitive contiguous series of characters a-z or A-Z
-   a case-sensitive contiguous series of characters with an apostrophe in the middle, but not at the beginning or end.
-   a blank newline that indicates the start of a new paragraph (non-blank newlines should be considered no different than a space)
-   an end-of-sentence punctuation mark, particularly ".", "?", or "!".

All other characters (spaces, newlines, and punctuation) should be considered "delimiters". A delimiter is a character or sequence of characters that provides separation between data values. In our case, our data values are words and there may be any number of delimiters between two words. For example, a double quote may be followed by a comma and a space. This should be considered one delimiter instead of three: the empty spaces between the delimiters are not considered words.

#### Caveats Due to Tokenization 

Because periods are their own token and because apostrophes are considered delineators, program output often looks like this:

```
I walked my dog to the car . I didn t see 
//       extra space -----^        ^---- another extra space  
```

**For the purposes of grading, both of these are OK.** However, as a fun challenge, if you'd like, you can try to fix some of these "quirks" in your code! We recommend doing this last, and committing your code first to ensure you do not break your code in the process of experimenting with this.

Another common "quirk" program output: if your training text has speaker tags (e.g. `"Computer science is cool!" she stated`), there is the possibility that your output has a sentence that starts with a lowercase letter, like so:

```
That is a nice dog! she stated there was a cause for the financial crisis of the world.
//                  ^---- lowercase letter "start" to a sentence
```

This is because the phrase `"Computer science is cool!" she stated` is tokenized into `Computer science is cool ! she stated`.

### Evaluation Criteria

**Below is a list of criteria for which the assignment will be evaluated and feedback will be given, using the E/S/U/N system.** _The sub-bullet points are provided as examples of what to consider when completing this assignment and are not intended to be comprehensive._

- **Meets spec**
    - At least two training texts included and used for training
    - Included favoriteNovel.txt in top of repository
- **Code structure**
    - No code copied many times within a method, many times in a class, etc.
    - Code is structured in an easy-to-read way
    - Properly handles user input validation
    - Architecture is well-formed and follows the single responsibility principle
- **Testing**
    - All tests run and pass
    - All assert statements have a unique "message" passed to them
- **Software engineering principals**
    - No linter errors
    - Well-chosen variable and method names
- **Data Structures**
    - Appropriate choices for dynamic types of `Map` and `List` in model
- **Algorithms**
    - Student demonstrates knowledge of algorithms, as taught in class