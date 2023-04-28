package org.example.database;

public class SqlQueries {
    private static final String TABLE_NAME    = "users";
    public static final  String REGISTER_USER = """
            INSERT INTO %s (name, login, password)
            VALUES (?, ?, ?)
            """.formatted(TABLE_NAME);
    public static final  String LOGIN_USER    = """
            SELECT * FROM %s
            WHERE login = ? AND password = ?
            """.formatted(TABLE_NAME);
}
