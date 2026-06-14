package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class FilterLessThanMinutes implements Command {

    private final CollectionManager manager;
    public FilterLessThanMinutes(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute (Request request) {
        Long value = (Long) request.getArg();
        String res = manager.getCollection().stream()
                .filter(h -> h.getMinutesOfWaiting() != null)
                .filter(h -> h.getMinutesOfWaiting() < value)
                .map(Object::toString).reduce("", (a, b) -> a + b + "\n");
        return new Response(res);
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
