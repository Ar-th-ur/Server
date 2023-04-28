package org.example.database;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.database.SqlQueries.LOGIN_USER;
import static org.example.database.SqlQueries.REGISTER_USER;

public class DataBase {
    private static final String          URL      = "jdbc:mysql://127.0.0.1:3306/chat";
    private static final String          USER     = "root";
    private static final String          PASSWORD = "";
    private static       DataBase        dataBase;
    private final        MysqlDataSource dataSource;

    public static DataBase getInstance() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }

    private DataBase() {
        dataSource = new MysqlDataSource();
        dataSource.setUrl(URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
    }

    public boolean registerUser(String name, String login, String password) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(REGISTER_USER)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, login);
            preparedStatement.setString(3, password);
            return preparedStatement.executeUpdate() != 0;
        }
    }

    public ResultSet loginUser(String login, String password) throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_USER);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);
        return preparedStatement.executeQuery();
    }
}
