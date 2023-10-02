package com.example.SpringBoredomHelperBot.service;

import com.example.SpringBoredomHelperBot.config.BotConfig;
import com.example.SpringBoredomHelperBot.model.Book;
import com.example.SpringBoredomHelperBot.model.Game;
import com.example.SpringBoredomHelperBot.model.Movie;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final Requests requests;
    static final String HELP_TEXT = EmojiParser.parseToUnicode("Это небольшой бот, который поможет тебе выбрать фильм, игру или книгу.\n\n" +
            ":exclamation:" + "БОТ СОЗДАН ИСКЛЮЧИТЕЛЬНО В ЦЕЛЯХ ОБУЧЕНИЯ" + ":exclamation:\n\n" +
            "Список команд: \n" +
            "/movie - " + ":movie_camera:" + "получить случайный фильм\n" +
            "/game - " + ":video_game:" + "получить случайную игру\n" +
            "/book - " + ":orange_book:" + "получить случайную книгу");

    public TelegramBot(BotConfig config, Requests requests) {
        this.config = config;
        this.requests = requests;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Приветствие"));
        listOfCommands.add(new BotCommand("/movie", "Случайный фильм"));
        listOfCommands.add(new BotCommand("/game", "Случайная игра"));
        listOfCommands.add(new BotCommand("/book", "Случайная книга"));
        listOfCommands.add(new BotCommand("/help", "Помощь"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (text) {
                case "/start":
                    startCommand(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                case "Помощь":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/movie":
                case "Фильм":
                    movieCommand(chatId);
                    break;
                case "/game":
                case "Игра":
                    gameCommand(chatId);
                    break;
                case "/book":
                case "Книга":
                    bookCommand(chatId);
                    break;
                default:
                    sendMessage(chatId, EmojiParser.parseToUnicode("Эта команда пока не поддерживается" + ":dizzy_face:"));
            }

        }
    }

    private void startCommand(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode("Привет, " + name + ", рад тебя видеть!" + ":blush:\n" +
                "Воспользуйся экранной клавиатурой" + ":point_down:");
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(answer);
        message.setReplyMarkup(getKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setReplyMarkup(getKeyboard());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    private ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add(EmojiParser.parseToUnicode("Фильм"));
        row1.add(EmojiParser.parseToUnicode("Игра"));
        keyboardRows.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add(EmojiParser.parseToUnicode("Книга"));
        row2.add(EmojiParser.parseToUnicode("Помощь"));
        keyboardRows.add(row2);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    private void movieCommand(long chatId) {
        try {
            Movie movie = requests.getMovieInformation();
            String response = movie.getName() + "\n\n" +
                    movie.getDescription() + "\n\n" +
                    "Рейтинг: " + movie.getRate() + "\n\n" +
                    movie.getLink();
            sendMessage(chatId, response);
        } catch (IOException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    private void gameCommand(long chatId) {
        try {
            Game game = requests.getGameInformation();
            String response = game.getName() + "\n\n" +
                    game.getDescription() + "\n\n" +
                    "Рейтинг " + game.getRate() + "\n\n" +
                    game.getLink();
            sendMessage(chatId, response);
        } catch (IOException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }

    private void bookCommand(long chatId) {
        try {
            Book book = requests.getBookInformation();
            String response = book.getName() + "\n" +
                    book.getAuthor() + "\n\n" +
                    book.getDescription() + "\n\n" +
                    "Просмотры: " + book.getRate() + "\n\n" +
                    book.getLink();
            sendMessage(chatId, response);
        } catch (IOException e) {
            log.error("Ошибка: " + e.getMessage());
        }
    }
}
