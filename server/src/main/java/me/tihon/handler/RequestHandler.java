package me.tihon.handler;

import me.tihon.auth.AuthService;
import me.tihon.command.Command;
import me.tihon.command.CommandFactory;
import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;

public class RequestHandler {

    private final CommandFactory factory;
    private final AuthService authService;

    public RequestHandler(CommandFactory factory, AuthService authService) {
        this.factory = factory;
        this.authService = authService;
    }

    public Response handle(Request request) {

        if (request == null) return new Response("Пустой запрос");
        CommandType type = request.getType();
        if (type == null) return new Response("Команда не указана");

        Command command = factory.getCommand(type);
        if (command == null) return new Response("Неизвестная команда");

        if (command.requiresAuth()) {
            boolean authed = authService.login(request.getUsername(), request.getPassword());
            if (!authed) return new Response("Ошибка авторизации");
        }

        return command.execute(request);
    }
}

