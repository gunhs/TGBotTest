package ru.sharanov.SearchForMessagesBot.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DBparticipant {
    List<String> participants;
    private static final String PATH = "data/participant.txt";
    public DBparticipant() throws IOException {
//        participants = new ArrayList<>();
        participants = readFile(Path.of(PATH));

    }
    public void addParticipant(String newParticipant) throws IOException {
//        if (!participant.contains(newParticipant)){
            participants.add(newParticipant);
//        }
        StringBuilder listOfParticipant = new StringBuilder();
        participants.forEach(p-> listOfParticipant.append(p).append("\n"));
        Files.write(Path.of(PATH), listOfParticipant.toString().getBytes());
    }

    public ArrayList<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    private ArrayList<String> readFile(Path path) throws IOException {
        List<String> strings = Files.readAllLines(path);
        return new ArrayList<>(strings);
    }
}