package me.tihon;

import me.tihon.dto.Request;
import me.tihon.dto.Response;

import java.io.*;
import java.net.*;

public class NetworkManager {

    private static final String HOST = "localhost";
    private static final int PORT = 6769;
    private final DatagramSocket socket;

    public NetworkManager() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(5000);
    }

    public Response send(Request request) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(request);
            oos.flush();

            byte[] requestBytes = baos.toByteArray();
            DatagramPacket requestPacket = new DatagramPacket(requestBytes,
                            requestBytes.length, InetAddress.getByName(HOST), PORT);
            socket.send(requestPacket);

            byte[] buffer = new byte[65535];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            ByteArrayInputStream bais = new ByteArrayInputStream(
                            responsePacket.getData(), 0, responsePacket.getLength());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Response) ois.readObject();

        } catch (SocketTimeoutException e) {
            return new Response("Сервер не отвечает");
        } catch (Exception e) {
            return new Response("Ошибка: " + e.getMessage());
        }
    }

    public void close() {
        if (!socket.isClosed()) socket.close();
    }
}