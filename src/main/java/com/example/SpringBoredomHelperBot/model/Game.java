package com.example.SpringBoredomHelperBot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Game {
    String name;
    String description;
    String rate;
    String link;
}
