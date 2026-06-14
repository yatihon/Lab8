package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class RemoveLower implements Command {

    private final CollectionManager manager;
    public RemoveLower(CollectionManager manager) {
        this.manager = manager;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing sample = (HumanBeing) request.getArg();
        String user = request.getUsername();
        int before = manager.getCollection().size();
        manager.getCollection().
                removeIf(h -> h.getOwner().equals(user) && h.compareTo(sample) < 0);
        int after = manager.getCollection().size();
        return new Response("Удалено " + (before - after) + " элементов");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}


