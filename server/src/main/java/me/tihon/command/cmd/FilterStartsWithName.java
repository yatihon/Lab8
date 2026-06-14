package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class FilterStartsWithName implements Command {

    private final CollectionManager manager;
    public FilterStartsWithName(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute (Request request) {
        String prefix = (String) request.getArg();
        String res = manager.getCollection().stream()
                .filter(h -> h.getName().startsWith(prefix))
                .map(Object::toString).reduce("", (a, b) -> a + b + "\n");
        return new Response(res);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}