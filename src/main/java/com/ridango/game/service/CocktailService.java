package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class CocktailService {

    private final RestTemplate restTemplate;

    public CocktailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Cocktail getRandomCocktail() {
        String url = "https://www.thecocktaildb.com/api/json/v1/1/random.php";
        CocktailResponse response = restTemplate.getForObject(url, CocktailResponse.class);
        return response.getDrinks().get(0);
    }
    @Setter
    @Getter
    private static class CocktailResponse {
        private List<Cocktail> drinks;

    }
}