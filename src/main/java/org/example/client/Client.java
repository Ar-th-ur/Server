package org.example.client;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("192.168.1.38", 9234);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        System.out.println(in.readUTF());
                    } catch (IOException e) {
                        System.out.println("Соединение с сервером потеряно");
                    }
                }
            });
            thread.start();
            JSONObject jsonObject = new JSONObject();
            String message;
            while (true) {
                message = scanner.nextLine().trim();
                if (message.startsWith("/m")) {
                    String[] array = message.split(" ", 3);
                    jsonObject.put("private", true);
                    jsonObject.put("to", array[1]);
                    jsonObject.put("message", array[2]);
                } else {
                    jsonObject.put("private", false);
                    jsonObject.put("message", message);
                }
                out.writeUTF(jsonObject.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
