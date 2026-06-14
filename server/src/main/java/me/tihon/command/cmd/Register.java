package me.tihon.command.cmd;

import me.tihon.auth.AuthService;
import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;

public class Register implements Command {

    private final AuthService authService;
    public Register(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response execute(Request request) {
        boolean success = authService.register(request.getUsername(), request.getPassword());
        if (success) return new Response("Регистрация успешна");
        return new Response("Пользователь уже существует");
    }

    @Override
    public boolean requiresAuth() {
        return false;
    }
}
