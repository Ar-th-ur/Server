package org.example.server;

import org.example.database.DataBase;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class User {
    private       String           name;
    private final DataInputStream  in;
    private final DataOutputStream out;

    public User(Socket socket) throws IOException {
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void write(Object data, String key) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(key, data);
        out.writeUTF(jsonObject.toString());
    }

    public void write(JSONObject data) throws IOException {
        out.writeUTF(data.toString());
    }

    public JSONObject getData() throws IOException {
        return new JSONObject(in.readUTF());
    }

    public <T> T getData(String key) throws IOException {
        JSONObject jo = getData();
        return (T) jo.get(key);
    }

    public boolean registerUser() throws IOException, SQLException {
        write("Введите имя:", "message");
        String name = getData("message");
        write("Введите логин:", "message");
        String login = getData("message");
        write("Введите пароль:", "message");
        String password = getData("message");
        if (DataBase.getInstance().registerUser(name, login, password)) {
            setName(name);
            return true;
        }
        return false;
    }

    public boolean loginUser() throws IOException, SQLException {
        write("Введите логин:", "message");
        String login = getData("message");
        write("Введите пароль:", "message");
        String password = getData("message");
        ResultSet resultSet = DataBase.getInstance().loginUser(login, password);
        if (resultSet.next()) {
            setName(resultSet.getString("name"));
            resultSet.close();
            return true;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        if (!Objects.equals(name, user.name)) return false;
        if (!in.equals(user.in)) return false;
        return out.equals(user.out);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + in.hashCode();
        result = 31 * result + out.hashCode();
        return result;
    }
}
