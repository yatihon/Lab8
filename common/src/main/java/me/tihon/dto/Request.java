package me.tihon.dto;

import me.tihon.command.CommandType;
import java.io.Serializable;

public class Request implements Serializable {
    private CommandType type;
    private Object arg;
    private String username;
    private String password;
    private static final long serialVersionUID = 1L;

    public Request(CommandType type, Object arg, String username, String password) {
        this.type = type;
        this.arg = arg;
        this.username = username;
        this.password = password;
    }

    public CommandType getType() {
        return type;
    }

    public Object getArg() {
        return arg;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
