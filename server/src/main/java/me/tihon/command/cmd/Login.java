package me.tihon.command.cmd;

import me.tihon.auth.AuthService;
import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;

public class Login implements Command {

    private final AuthService authService;
    public Login(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response execute(Request request) {
        boolean success = authService.login(request.getUsername(), request.getPassword());
        if (success) return new Response("Авторизация успешна");
        return new Response("Неверный логин или пароль");
    }

    @Override
    public boolean requiresAuth() {
        return false;
    }
}
