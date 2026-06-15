package me.tihon;

import me.tihon.command.CommandType;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.model.Car;
import me.tihon.model.Coordinates;
import me.tihon.model.HumanBeing;
import me.tihon.model.WeaponType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScriptExecutor {

    private final NetworkManager networkManager;
    private final String username;
    private final String password;
    private final Set<String> executingFiles;

    public ScriptExecutor(NetworkManager networkManager, String username, String password) {
        this(networkManager, username, password, new HashSet<>());
    }

    private ScriptExecutor(NetworkManager networkManager, String username, String password,
                           Set<String> executingFiles) {
        this.networkManager = networkManager;
        this.username = username;
        this.password = password;
        this.executingFiles = executingFiles;
    }

    public List<String> execute(String filePath) {
        List<String> result = new ArrayList<>();

        String canon;
        try {
            canon = Path.of(filePath).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            return List.of("Ошибка доступа к файлу: " + filePath);
        }
        if (executingFiles.contains(canon)) {
            return List.of("Рекурсия: файл уже выполняется - " + filePath);
        }
        executingFiles.add(canon);

        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(filePath));
        } catch (IOException e) {
            executingFiles.remove(canon);
            return List.of("Не удалось прочитать файл: " + filePath);
        }

        Queue<String> q = new ArrayDeque<>();
        for (String line : lines) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                q.add(trimmed);
            }
        }

        while (!q.isEmpty()) {
            String commandLine = q.poll();
            String[] parts = commandLine.split("\\s+", 2);
            String cmd = parts[0].toLowerCase();
            String arg = parts.length > 1 ? parts[1] : "";

            try {
                String res = process(cmd, arg, q);
                result.add("[" + cmd + "] " + res);
            } catch (Exception e) {
                result.add("[" + cmd + "] Ошибка: " + e.getMessage());
            }
        }

        executingFiles.remove(canon);
        return result;
    }

    private String process(String cmd, String arg, Queue<String> q) {
        return switch (cmd) {
            case "show" -> {
                Response r = networkManager.send(new Request(CommandType.SHOW, null, username, password));
                yield r.getMessage();
            }
            case "info" -> {
                Response r = networkManager.send(new Request(CommandType.INFO, null, username, password));
                yield r.getMessage();
            }
            case "help" -> {
                Response r = networkManager.send(new Request(CommandType.HELP, null, username, password));
                yield r.getMessage();
            }
            case "clear" -> {
                Response r = networkManager.send(new Request(CommandType.CLEAR, null, username, password));
                yield r.getMessage();
            }
            case "print_descending" -> {
                Response r = networkManager.send(new Request(CommandType.PRINT_DESCENDING, null, username, password));
                yield r.getMessage();
            }
            case "remove_by_id" -> {
                int id = Integer.parseInt(arg.trim());
                Response r = networkManager.send(new Request(CommandType.REMOVE_BY_ID, id, username, password));
                yield r.getMessage();
            }
            case "filter_starts_with_name" -> {
                Response r = networkManager.send(new Request(CommandType.FILTER_STARTS_WITH_NAME, arg.trim(), username, password));
                yield r.getMessage();
            }
            case "filter_less_than_minutes_of_waiting" -> {
                long minutes = Long.parseLong(arg.trim());
                Response r = networkManager.send(new Request(CommandType.FILTER_LESS_THAN_MINUTES, minutes, username, password));
                yield r.getMessage();
            }
            case "add" -> {
                HumanBeing h = readHuman(q);
                Response r = networkManager.send(new Request(CommandType.ADD, h, username, password));
                yield r.getMessage();
            }
            case "add_if_max" -> {
                HumanBeing h = readHuman(q);
                Response r = networkManager.send(new Request(CommandType.ADD_IF_MAX, h, username, password));
                yield r.getMessage();
            }
            case "add_if_min" -> {
                HumanBeing h = readHuman(q);
                Response r = networkManager.send(new Request(CommandType.ADD_IF_MIN, h, username, password));
                yield r.getMessage();
            }
            case "update" -> {
                int id = Integer.parseInt(arg.trim());
                HumanBeing h = readHuman(q);
                h.setId(id);
                Response r = networkManager.send(new Request(CommandType.UPDATE, h, username, password));
                yield r.getMessage();
            }
            case "remove_lower" -> {
                HumanBeing h = readHuman(q);
                Response r = networkManager.send(new Request(CommandType.REMOVE_LOWER, h, username, password));
                yield r.getMessage();
            }
            case "execute_script" -> {
                ScriptExecutor nested = new ScriptExecutor(
                        networkManager, username, password, executingFiles);
                List<String> subResult = nested.execute(arg.trim());
                yield String.join("\n", subResult);
            }
            default -> "Неизвестная команда: " + cmd;
        };
    }

    private HumanBeing readHuman(Queue<String> q) {
        String name = requireNext(q, "name");
        float  x = Float.parseFloat(requireNext(q, "x"));
        int    y = Integer.parseInt(requireNext(q, "y"));
        boolean realHero = Boolean.parseBoolean(requireNext(q, "realHero"));
        boolean toothpick = Boolean.parseBoolean(requireNext(q, "hasToothpick"));
        float  impactSpeed = Float.parseFloat(requireNext(q, "impactSpeed"));
        String soundtrack = requireNext(q, "soundtrackName");
        long   minutes = Long.parseLong(requireNext(q, "minutesOfWaiting"));
        String weaponStr = requireNext(q, "weaponType");
        boolean carCool = Boolean.parseBoolean(requireNext(q, "car.cool"));
        WeaponType weapon = weaponStr.equalsIgnoreCase("null") ? null
                : WeaponType.valueOf(weaponStr.toUpperCase());

        return new HumanBeing(
                null, null, name, new Coordinates(x, y), realHero, toothpick, impactSpeed,
                soundtrack, minutes, weapon, new Car(carCool));
    }

    private String requireNext(Queue<String> queue, String fieldName) {
        String val = queue.poll();
        if (val == null) throw new IllegalArgumentException("Ожидалось поле: " + fieldName);
        return val.strip();
    }

}
