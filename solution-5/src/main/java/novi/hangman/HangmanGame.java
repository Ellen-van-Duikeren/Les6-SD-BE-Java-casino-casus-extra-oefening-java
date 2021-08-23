package novi.hangman;

import novi.hangman.exceptions.DuplicateLetterException;

import java.util.*;

public class HangmanGame {
    private static List<String> WORDS = List.of("funny", "subway", "uptown", "beekeeper", "buffalo", "buzzard",
            "jackpot", "ivy", "strength", "zipper", "whiskey", "kiosk");

    private Scanner inputScanner;
    private ImageFactory imageFactory;
    private boolean gameRunning;
    private String wordToGuess;
    private String guessState;
    private List<Character> guessedLetters;
    private int numberOfWrongGuesses;

    public HangmanGame(Scanner inputScanner) {
        this.inputScanner = inputScanner;
    }

    public void playGame() {
        this.gameRunning = true;

        this.wordToGuess = WORDS.get(new Random().nextInt(WORDS.size()));
        this.guessState = "*".repeat(this.wordToGuess.length());
        this.guessedLetters = new ArrayList<>();
        this.numberOfWrongGuesses = 0;

        System.out.println("Fill in a letter to start guessing");
        while (this.gameRunning) {
            if (inputScanner.hasNextLine()) {
                String guess = inputScanner.nextLine();

                try {
                    applyGuess(guess);
                } catch (DuplicateLetterException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void applyGuess(String guess) {
        var wrongGuessMessage = "";

        if (guess.length() != 1) {
            System.out.println("You can only type letters!");
            return;
        }

        var letter = guess.toCharArray()[0];

        // If the letter was already guessed, exit here
        if (guessedLetters.contains(letter)) {
            throw new DuplicateLetterException(letter);
        }

        this.guessedLetters.add(letter);
        var matchingIndices = findLetterInWord(this.wordToGuess, letter);

        // if the letter is present in our word
        if (matchingIndices.size() > 0) {
            var builder = new StringBuilder(this.guessState);
            for (var index : matchingIndices) {
                builder.setCharAt(index, letter);
            }
            this.guessState = builder.toString();
            System.out.printf("Correct! The letter %s is used\n", guess);
            System.out.println(this.guessState);

            if (guessState.equals(wordToGuess)) {
                System.out.printf("Correct! the word was %s, you won!\n", wordToGuess);
                gameRunning = false;
            }

            return;
        } else {
            numberOfWrongGuesses++;
            wrongGuessMessage = String.format("Nope! The letter %s is not used", guess);
        }

        // If it's game over we stop the game
        if (this.numberOfWrongGuesses > 6) {
            wrongGuessMessage = String.format("You lost! the word was: %s", this.wordToGuess);
            gameRunning = false;
        }

        var hangmanImage = ImageFactory.getImage(this.numberOfWrongGuesses);

        for (String imageLine : hangmanImage) {
            System.out.println(imageLine);
        }
        System.out.println(this.guessState);
        System.out.println(wrongGuessMessage);
    }

    private List<Integer> findLetterInWord(String word, char letter) {
        var indices = new ArrayList<Integer>();

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                indices.add(i);
            }
        }

        return indices;
    }
}