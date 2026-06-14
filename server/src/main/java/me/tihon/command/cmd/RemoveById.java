package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class RemoveById implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO humanDAO;
    public RemoveById(CollectionManager manager, HumanBeingDAO humanDAO) {
        this.manager = manager;
        this.humanDAO = humanDAO;
    }

    @Override
    public Response execute(Request request) {
        Integer id = (Integer) request.getArg();
        HumanBeing hum = manager.getById(id);
        if (hum == null) return new Response("Элемент не найден");
        if (!hum.getOwner().equals(request.getUsername())) {
            return new Response("Нельзя удалять чужой объект");
        }
        boolean success = humanDAO.delete(id);
        if (!success) return new Response("Ошибка удаления");
        manager.removeById(id);
        return new Response("Элемент удален");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}

