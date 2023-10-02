package com.example.SpringBoredomHelperBot.service;

import com.example.SpringBoredomHelperBot.model.Book;
import com.example.SpringBoredomHelperBot.model.Game;
import com.example.SpringBoredomHelperBot.model.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Requests {
    public Movie getMovieInformation() throws IOException {
        Document document = Jsoup.connect("https://www.kinoafisha.info/rating/movies/").get();
        Elements movies = document.getElementsByClass("movieList_item movieItem  movieItem-rating movieItem-position ");
        Element randomMovie = movies.get((int) (Math.random() * 99));
        String url = randomMovie.child(0).attr("href");
        Document movieDocument = Jsoup.connect(url).get();
        Movie movie = Movie.builder()
                .name(Objects.requireNonNull(movieDocument.getElementsByClass("trailer_title").first()).text())
                .description(Objects.requireNonNull(movieDocument.getElementsByClass("visualEditorInsertion filmDesc_editor more_content").get(2)).text())
                .rate(Double.valueOf(Objects.requireNonNull(movieDocument.getElementsByClass("ratingBlockCard_local").first()).text()))
                .link(url)
                .build();
        return movie;
    }

    public Game getGameInformation() throws IOException {
        Document document = Jsoup.connect("https://store.steampowered.com/search/?ignore_preferences=1&supportedlang=russian&category1=998&ndl=1").get();
        Elements games = document.getElementsByClass("ignore_preferences");
        String url = games.get(0).child(2).child((int) (Math.random() * 49)).attr("href");
        Document gameDocument = Jsoup.connect(url).get();
        Game game = Game.builder()
                .name(Objects.requireNonNull(gameDocument.getElementsByClass("apphub_AppName").first()).text())
                .description(Objects.requireNonNull(gameDocument.getElementsByClass("game_description_snippet").first()).text())
                .rate((gameDocument.getElementsByClass("nonresponsive_hidden responsive_reviewdesc").get(1).text()))
                .link(url)
                .build();
        return game;
    }

    public Book getBookInformation() throws IOException {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Document document = Jsoup.connect("https://readli.net/page/" + i + "/?sort=2").get();
            documents.add(document);
        }
        Elements books = documents.get((int) (Math.random() * 5)).getElementsByClass("authors__list_type-1");
        Element randomBook = books.get(0).child((int) (Math.random() * 20)).child(0);
        String url = randomBook.child(0).child(0).child(0).child(0).child(0).attr("href");
        Document bookDocument = Jsoup.connect(url).get();
        Book book = Book.builder()
                .name(Objects.requireNonNull(bookDocument.getElementsByClass("main-info__title").first()).text())
                .author(Objects.requireNonNull(bookDocument.getElementsByClass("main-info__link").first()).text())
                .description(Objects.requireNonNull(bookDocument.getElementsByClass("seo__content").first()).text())
                .rate(Objects.requireNonNull(bookDocument.getElementsByClass("rating-numbers__item rating-numbers__item_icon-1").first()).text())
                .link(url)
                .build();
        return book;
    }

}
