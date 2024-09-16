package com.ridango.game.controller;

import com.ridango.game.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public String startGame() {
        gameService.startNewGame();
        return gameService.getIntro();
    }

    @PostMapping("/guess")
    public String makeGuess(@RequestParam String guess) {
        return gameService.makeGuess(guess);
    }

    @GetMapping("/status")
    public String getGameStatus() {
        if (gameService.isGameOver()) {
            return "Game over! Final score: " + gameService.getScore();
        }
        return "Current word: " + gameService.getHiddenName() + " (Attempts left: " + gameService.getAttemptsLeft() + ")";
    }
}