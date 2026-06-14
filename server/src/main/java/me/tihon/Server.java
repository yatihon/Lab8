package me.tihon;

import me.tihon.auth.AuthService;
import me.tihon.command.CommandFactory;
import me.tihon.database.DatabaseManager;
import me.tihon.database.HumanBeingDAO;
import me.tihon.database.UserDAO;
import me.tihon.dto.Request;
import me.tihon.dto.Response;
import me.tihon.handler.RequestHandler;
import me.tihon.manager.CollectionManager;
import me.tihon.model.HumanBeing;
import me.tihon.thread.ThreadPools;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.TreeSet;

public class Server {

    private final int PORT = 6769;
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final HumanBeingDAO humanDAO = new HumanBeingDAO(databaseManager);
    private final UserDAO userDAO = new UserDAO(databaseManager);
    private final AuthService authService = new AuthService(userDAO);
    private final CollectionManager manager = new CollectionManager();
    private final CommandFactory factory;
    private final RequestHandler handler;

    public Server() {
        factory = new CommandFactory(manager, humanDAO, authService);
        handler = new RequestHandler(factory, authService);
    }

    public void start() {

        System.out.println("Загрузка коллекции из БД...");
        TreeSet<HumanBeing> data = humanDAO.loadAll();
        manager.setCollection(data);
        System.out.println("Сервер запущен на порту " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread(ThreadPools::shutdown));

        try (DatagramChannel channel = DatagramChannel.open()) {
            channel.bind(new InetSocketAddress(PORT));
            Scanner scanner = new Scanner(System.in);

            while (true) {

                if (System.in.available() > 0) {
                    String cmd = scanner.nextLine().trim();
                    if (cmd.equalsIgnoreCase("exit")) {
                        System.out.println("Сервер остановлен");
                        break;
                    }
                }

                ByteBuffer buffer = ByteBuffer.allocate(65536);
                SocketAddress client = channel.receive(buffer);
                if (client == null) continue;
                buffer.flip();
                byte[] reqData = new byte[buffer.remaining()];
                buffer.get(reqData);

                ThreadPools.readPool.submit(() -> {
                    try {
                        Request request = deserialize(reqData);
                        ThreadPools.requestPool.submit(() -> {
                            Response response = handler.handle(request);
                            ThreadPools.sendPool.submit(() -> {
                                try {
                                    send(channel, client, response);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Request deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Request) ois.readObject();
        }
    }

    private static byte[] serialize(Response response) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            oos.flush();
            return baos.toByteArray();
        }
    }

    private synchronized void send(DatagramChannel channel,
                                   SocketAddress client, Response response) throws IOException {
        byte[] bytes = serialize(response);
        ByteBuffer out = ByteBuffer.wrap(bytes);
        channel.send(out, client);
    }
}

