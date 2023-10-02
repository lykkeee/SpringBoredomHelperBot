package com.example.SpringBoredomHelperBot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Movie {
    String name;
    String description;
    Double rate;
    String link;
}
