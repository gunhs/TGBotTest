package ru.sharanov.SearchForMessagesBot.security;

import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class TelegramAuthenticationServlet extends HttpServlet {

    private String id;
    private String name;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(req.getRequestURL());
        if (req.getRequestURL().toString().equals("https://eventjavaspb.ru/sign")) {
            id = (String) req.getAttribute("id");
            name = (String) req.getAttribute("name");
            System.out.println("Id in servlet" + id);
            System.out.println("Name in servlet" + name);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
