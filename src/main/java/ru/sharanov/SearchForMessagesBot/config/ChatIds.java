package ru.sharanov.SearchForMessagesBot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

@Configuration
public class ChatIds {
    private Environment environment;

    public void writePropertyToFile(String propertyName, String propertyValue) {
        String configFile = "C:\\Users\\VSH\\IdeaProjects\\TGBotTest\\src\\main\\resources\\application.yml";
        Properties props = new Properties();

        try {
            // Загрузка существующего файла конфигурации
            InputStream inputStream = new FileInputStream(configFile);
            props.load(inputStream);
            inputStream.close();

            // Запись значения переменной в файл конфигурации
            props.setProperty(propertyName, propertyValue);

            // Сохранение изменений в файле конфигурации
            OutputStream outputStream = new FileOutputStream(configFile);
            props.store(outputStream, null);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
