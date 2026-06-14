package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class AddIfMin implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO humanDAO;
    public AddIfMin(CollectionManager manager, HumanBeingDAO humanDAO) {
        this.manager = manager;
        this.humanDAO = humanDAO;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing hum = (HumanBeing) request.getArg();
        hum.setOwner(request.getUsername());
        HumanBeing m = manager.getCollection().stream().min(HumanBeing::compareTo).orElse(null);
        if (m != null && hum.compareTo(m) >= 0) return new Response("Элемент не минимальный");

        boolean success = humanDAO.insert(hum);
        if (!success) return new Response("Ошибка сохранения");
        manager.add(hum);
        return new Response("Элемент добавлен");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}

