package ru.sharanov.SearchForMessagesBot.Storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DBparticipant {
    List<String> participants;
    HashMap<String, String> participantsMap;
    List<String> nickNames;
    private static final String PATH = "data/participant.txt";
    private static final String PATHTONICKNAMES = "data/nicknames.txt";

    public DBparticipant() throws IOException {
        participants = readFile(Path.of(PATH));
        nickNames = readFile(Path.of(PATHTONICKNAMES));
    }

    public String addParticipant(String newParticipant, String nickName) throws IOException {
        if (nickNames.contains(nickName)) {
            return newParticipant;
        }
        nickNames.add(nickName);
        participants.add(newParticipant + " (@" + nickName + ")");

        Files.write(Path.of(PATH), participants);
        Files.write(Path.of(PATHTONICKNAMES), nickNames);
        return "";
    }

    public void removeParticipants(String userName) throws IOException {
        participants.removeIf(p -> p.contains("@" + userName));
        nickNames.removeIf(n -> n.equals(userName));
        Files.write(Path.of(PATH), participants);
        Files.write(Path.of(PATHTONICKNAMES), nickNames);
    }

    public ArrayList<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    private ArrayList<String> readFile(Path path) throws IOException {
        List<String> strings = Files.readAllLines(path);
        return new ArrayList<>(strings);
    }
}