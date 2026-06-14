package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class AddIfMax implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO humanDAO;
    public AddIfMax(CollectionManager manager, HumanBeingDAO humanDAO) {
        this.manager = manager;
        this.humanDAO = humanDAO;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing hum = (HumanBeing) request.getArg();
        hum.setOwner(request.getUsername());
        HumanBeing max = manager.getCollection().stream().max(HumanBeing::compareTo).orElse(null);
        if (max != null && hum.compareTo(max) <= 0) return new Response("Элемент не максимальный");

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

