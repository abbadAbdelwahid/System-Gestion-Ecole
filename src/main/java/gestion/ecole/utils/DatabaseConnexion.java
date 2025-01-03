package gestion.ecole.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnexion {
    private static DatabaseConnexion instance;
    private Connection connection;

    private DatabaseConnexion() {
        try {
            Dotenv dotenv = Dotenv.load();
            String host = dotenv.get("DB_HOST");
            String port = dotenv.get("DB_PORT");
            String dbName = dotenv.get("DB_NAME");
            String username = dotenv.get("DB_USERNAME");
            String password = dotenv.get("DB_PASSWORD");
            String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("PostgreSQL Connection successful");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static DatabaseConnexion getInstance() {
        if (instance == null) {
            instance = new DatabaseConnexion();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
