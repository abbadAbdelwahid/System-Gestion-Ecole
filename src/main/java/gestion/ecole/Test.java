package gestion.ecole;

import gestion.ecole.utils.DatabaseConnexion;

import java.sql.Connection;

public class Test {
    public static void main(String[] args) {
        Connection connection = DatabaseConnexion.getInstance().getConnection();
    }
}
