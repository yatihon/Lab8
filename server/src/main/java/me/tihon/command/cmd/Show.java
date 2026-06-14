package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class Show implements Command {

    private final CollectionManager manager;
    public Show(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        return new Response("коллекция загружена", manager.getCollection());
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
