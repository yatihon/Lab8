package me.tihon.command.cmd;

import me.tihon.command.Command;
import me.tihon.database.HumanBeingDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;

public class Update implements Command {

    private final CollectionManager manager;
    private final HumanBeingDAO humanDAO;
    public Update(CollectionManager manager, HumanBeingDAO humanDAO) {
        this.manager = manager;
        this.humanDAO = humanDAO;
    }

    @Override
    public Response execute(Request request) {
        HumanBeing upd = (HumanBeing) request.getArg();
        if (upd == null) return new Response("Объект не передан");
        Integer id = upd.getId();
        HumanBeing old = manager.getById(id);
        if (old == null) return new Response("Элемент не найден");
        if (!old.getOwner().equals(request.getUsername())) {
            return new Response("Нельзя изменять чужой объект");
        }
        upd.setOwner(request.getUsername());
        upd.setCreationDate(old.getCreationDate());

        boolean success = humanDAO.update(upd);
        if (!success) return new Response("Ошибка обновления в БД");
        manager.removeById(id);
        manager.add(upd);
        return new Response("Элемент обновлен");
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}

