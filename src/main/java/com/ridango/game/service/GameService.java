package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class GameService {

    private static final int MAX_ATTEMPTS = 5;

    @Getter
    private String secretCocktail;

    @Getter
    private String hiddenName;

    @Getter
    private String instructions;

    @Getter
    private String category;

    @Getter
    private String glass;

    @Getter
    private String ingredients;

    @Getter
    private String imageUrl;

    @Getter
    private int attemptsLeft;

    @Getter
    private boolean gameOver;

    @Getter
    private int score;

    private final CocktailService cocktailService;
    private final Random rand = new Random();
    private final Set<String> usedCocktails = new HashSet<>();
    private int revealStep;

    public GameService(CocktailService cocktailService) {
        this.cocktailService = cocktailService;
        this.score = 0;
        startNewGame();
    }

    // Start a new game
    public void startNewGame() {
        Cocktail cocktail = getRandomUnusedCocktail();
        this.secretCocktail = cocktail.getStrDrink().toUpperCase();
        this.hiddenName = secretCocktail.replaceAll("[a-zA-Z0-9]", "_");
        this.instructions = cocktail.getStrInstructions();
        this.category = cocktail.getStrCategory();
        this.glass = cocktail.getStrGlass();
        this.ingredients = formatIngredients(cocktail);
        this.imageUrl = cocktail.getStrDrinkThumb();
        this.attemptsLeft = MAX_ATTEMPTS;
        this.gameOver = false;
        this.revealStep = 0;
        usedCocktails.add(cocktail.getStrDrink().toUpperCase());
    }

    // Return the game introduction and initial prompt
    public String getIntro() {
        return String.format("""
                *******************************************************
                🎉 Welcome to the Cocktail Guessing Game! 🍹
                *******************************************************
                                
                Get ready to show off your bartending skills! Your task is simple:
                Guess the name of a random cocktail based on clues!
                                
                Here’s your mystery cocktail:
                Name: %s (%d letters)
                                
                Hint: Use the instructions below to figure out the cocktail name:
                                
                Instructions: 
                "%s"
                                
                Can you guess the name of the cocktail? Type one letter to start!
                """, hiddenName, secretCocktail.length(), instructions);
    }
    private Cocktail getRandomUnusedCocktail() {
        Cocktail cocktail;
        do {
            cocktail = cocktailService.getRandomCocktail();
        } while (usedCocktails.contains(cocktail.getStrDrink().toUpperCase()));

        usedCocktails.add(cocktail.getStrDrink().toUpperCase());
        return cocktail;
    }

    private String formatIngredients(Cocktail cocktail) {
        StringBuilder ingredients = new StringBuilder();
        if (cocktail.getStrIngredient1() != null) ingredients.append(cocktail.getStrIngredient1());
        if (cocktail.getStrIngredient2() != null) ingredients.append(", ").append(cocktail.getStrIngredient2());
        if (cocktail.getStrIngredient3() != null) ingredients.append(", ").append(cocktail.getStrIngredient3());
        return ingredients.toString();
    }

    public String makeGuess(String guess) {
        guess = guess.toUpperCase();

        if (gameOver)
            return "Game over! Start a new game.";

        if (guess.equals(secretCocktail)) {
            score += attemptsLeft;
            String message = "Correct! The cocktail was: " + secretCocktail + ". Your score is now: " + score;
            startNewGame();
            return message + "\n\nStarting a new round...\n" + getIntro();
        } else {
            attemptsLeft--;
            if (attemptsLeft == 0) {
                gameOver = true;
                return "Game over! The correct cocktail was: " + secretCocktail + ". Your final score is: " + score;
            } else {
                revealRandomLetter();
                return getHint();
            }
        }
    }

    private void revealRandomLetter() {
        char[] hiddenChars = hiddenName.toCharArray();
        while (true) {
            int randomIndex = rand.nextInt(secretCocktail.length());
            if (hiddenChars[randomIndex] == '_') {
                hiddenChars[randomIndex] = secretCocktail.charAt(randomIndex);
                break;
            }
        }
        hiddenName = new String(hiddenChars);
    }

    public String getHint() {
        StringBuilder hint = new StringBuilder();
        hint.append("Wrong guess! Here's more info to help:\n");
        hint.append("Name: ").append(hiddenName).append("\n");

        switch (revealStep) {
            case 0:
                hint.append("Hint: ").append("Category: ").append(category).append("\n");
                break;
            case 1:
                hint.append("Hint: ").append("Glass: ").append(glass).append("\n");
                break;
            case 2:
                hint.append("Hint: ").append("Ingredients: ").append(ingredients).append("\n");
                break;
            case 3:
                hint.append("Hint: ").append("Image: ").append(imageUrl).append("\n");
                break;
            default:
                hint.append("No more hints available.\n");
        }

        revealStep++;
        return hint.append("Attempts left: ").append(attemptsLeft).toString();
    }
}