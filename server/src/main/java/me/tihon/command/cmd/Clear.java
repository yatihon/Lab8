package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;

public class Clear implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO humanDAO;
    public Clear(CollectionManager manager, HumanBeingDAO humanDAO) {
        this.manager = manager;
        this.humanDAO = humanDAO;
    }

    @Override
    public Response execute(Request request) {
        String user = request.getUsername();
        boolean success = humanDAO.clearByOwner(user);
        if (!success) return new Response("Не удалось удалить элементы");
        manager.removeByOwner(user);
        return new Response("Удалены все ваши элементы");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
