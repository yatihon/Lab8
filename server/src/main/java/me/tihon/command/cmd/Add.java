package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class Add implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO dao;
    public Add(CollectionManager manager, HumanBeingDAO dao) {
        this.manager = manager;
        this.dao = dao;
    }

    @Override
    public Response execute(Request request) {

        HumanBeing h = (HumanBeing) request.getArg();
        h.setOwner(request.getUsername());
        boolean success = dao.insert(h);
        if (!success) return new Response("Ошибка добавления в базу данных");
        manager.add(h);
        return new Response("Элемент добавлен");

    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}

