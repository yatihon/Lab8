package me.tihon.command;

import me.tihon.auth.AuthService;
import me.tihon.command.cmd.*;
import me.tihon.database.HumanBeingDAO;
import me.tihon.manager.CollectionManager;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {

    private final Map<CommandType, Command> commands = new HashMap<>();
    public CommandFactory(CollectionManager manager, HumanBeingDAO dao, AuthService authService) {
        commands.put(CommandType.ADD, new Add(manager, dao));
        commands.put(CommandType.SHOW, new Show(manager));
        commands.put(CommandType.LOGIN, new Login(authService));
        commands.put(CommandType.REGISTER, new Register(authService));
        commands.put(CommandType.INFO, new Info(manager));
        commands.put(CommandType.CLEAR, new Clear(manager, dao));
        commands.put(CommandType.REMOVE_BY_ID, new RemoveById(manager, dao));
        commands.put(CommandType.UPDATE, new Update(manager, dao));
        commands.put(CommandType.REMOVE_LOWER, new RemoveLower(manager));
        commands.put(CommandType.PRINT_DESCENDING, new PrintDescending(manager));
        commands.put(CommandType.FILTER_STARTS_WITH_NAME, new FilterStartsWithName(manager));
        commands.put(CommandType.FILTER_LESS_THAN_MINUTES, new FilterLessThanMinutes(manager));
        commands.put(CommandType.ADD_IF_MAX, new AddIfMax(manager, dao));
        commands.put(CommandType.ADD_IF_MIN, new AddIfMin(manager, dao));
        commands.put(CommandType.HELP, new Help());
    }

    public Command getCommand(CommandType type) {
        return commands.get(type);
    }

}
