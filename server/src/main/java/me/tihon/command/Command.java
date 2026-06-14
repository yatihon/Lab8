package me.tihon.command;

import me.tihon.dto.Request;
import me.tihon.dto.Response;

public interface Command {
    Response execute(Request request);
    boolean requiresAuth();
}
