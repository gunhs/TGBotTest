package ru.sharanov.SearchForMessagesBot.utils;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.jackson.JsonComponent;
//import org.springframework.boot.json.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;

//@JsonComponent
//public class CustomDateSerializer {
//
//    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
//
//        @Override
//        public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
//            return null;
//        }
//    }
//}