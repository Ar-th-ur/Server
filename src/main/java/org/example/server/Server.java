package org.example.server;

import org.example.database.DataBase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final static List<User> users    = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9234)) {
            while (true) {
                Socket socket = serverSocket.accept();
                User user = new User(socket);
                Thread thread = new Thread(() -> {
                    try {
                        while (true) {
                            user.write("Для регистрации /reg\nДля авторизации /login", "message");
                            String response = user.getData("message");
                            if (response.equals("/reg")) {
                                if (user.registerUser()) break;
                            } else if (response.equals("/login")) {
                                if (user.loginUser()) break;
                            }
                        }
                        users.add(user);
                        user.write("Добро пожаловать на сервер, " + user.getName() + "!","message");
                        sendUsersList();
                        while (true) {
                            JSONObject jsonObject = user.getData();
                            String message = jsonObject.getString("message");
                            boolean isPublic = jsonObject.getBoolean("public");
                            if (isPublic) {
                                for (User u : users) {
                                    if (!u.equals(user)) {
                                        u.write(user.getName() + ": " + message, "message");
                                    }
                                }
                            } else {
                                String to = jsonObject.getString("to");
                                for (User u : users) {
                                    if (u.getName().equals(to) && !u.getName().equals(user.getName())) {
                                        u.write(user.getName() + ": " + message, "message");
                                        break;
                                    }
                                }
                            }

                        }
                    } catch (IOException e) {
                        users.remove(user);
                        sendUsersList();
                        System.out.println("Пользователь отключился");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Соединение потеряно");
        }
    }

    private static void sendUsersList() {
        JSONArray ja = new JSONArray();
        users.forEach(user -> ja.put(user.getName()));
        JSONObject jo = new JSONObject();
        jo.put("onlineUsers", ja);
        users.forEach(user -> {
            try {
                user.write(jo);
            } catch (IOException e) {
                System.out.println("Клиент отключен");
                e.printStackTrace();
            }
        });
    }
}
