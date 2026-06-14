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
        System.out.println("ENTER CLEAR");
        String user = request.getUsername();
        System.out.println("CLEAR USER = " + request.getUsername());

        boolean success = humanDAO.clearByOwner(user);
        System.out.println("DAO RESULT = " + success);
        if (!success) return new Response("Не удалось удалить элементы");
        manager.getCollection().removeIf(h -> user.equals(h.getOwner()));
        return new Response("Удалены все ваши элементы");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
