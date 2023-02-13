package ru.sharanov.SearchForMessagesBot.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DBEvents {
    private final List<String> events;
    private static final String PATH = "data/events.txt";

    public DBEvents() throws IOException {
        events = Files.readAllLines(Path.of(PATH));
    }

    private String readFile(Path path) throws IOException {
        List<String> strings = Files.readAllLines(path);
        StringBuilder result = new StringBuilder();
        strings.forEach(s -> result.append(s).append("\n"));
        return result.toString();
    }

    public void addEvents(String event) {
        events.add(event);
    }

    public ArrayList<String> getEvents() {
        return new ArrayList<>(events);
    }
}
