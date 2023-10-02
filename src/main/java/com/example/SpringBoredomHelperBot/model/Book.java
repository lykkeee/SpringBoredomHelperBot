package com.example.SpringBoredomHelperBot.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Book {
    String name;
    String author;
    String description;
    String rate;
    String link;
}
