package me.tihon.command.cmd;

import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.command.Command;

public class Help implements Command {

    @Override
    public Response execute(Request request) {
        return new Response(String.join("\n",
                "Список команд:",
                "help: Вывести справку по доступным командам",
                "show: Вывести все элементы коллекции в строковом представлении",
                "add: Добавить новый элемент в коллекцию",
                "clear: Очистить коллекцию",
                "info: Вывести информацию о коллекции",
                "remove_by_id id: Удалить элемент из коллекции по его id",
                "update id: Обновить значение элемента коллекции",
                "execute_script: Считать и исполнить скрипт из указанного файла",
                "print_descending: Вывести элементы коллекции в порядке убывания",
                "filter_starts_with_name name: Вывести элементы, значение поля name которых начинается с заданной подстроки",
                "filter_less_than_minutes_of_waiting minutesOfWaiting: Вывести элементы, значение поля minutesOfWaiting которых меньше заданного",
                "add_if_max: Добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции",
                "add_if_min: Добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции",
                "remove_lower: Удалить из коллекции все элементы, меньшие, чем заданный"
        ));
    }

    @Override
    public boolean requiresAuth() {
        return true;
    }
}
