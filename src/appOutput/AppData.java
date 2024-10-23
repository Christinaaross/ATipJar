package appOutput;

import java.sql.Connection;
import java.sql.Statement;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppData {

    private Connection connection;
    private Statement statement;

    
    public AppData() {
        //FOR CONNECTION BRING IN THE DRIVER
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver could NOT be Loaded");
            e.printStackTrace();
        }

        // CONNECTION TO THE DATABASE
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mytipjar", "root", "Lavender444!");
            System.out.println("Database connected!");
        } catch (SQLException ex) {
            System.out.println("Database could NOT connect");
            ex.printStackTrace();
        }

        // STATEMENT OBJECT
        try {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Statement object created");
        } catch (SQLException ex) {
            System.out.println("Error in statement object");
            ex.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return connection;
    }

    // close Connection and Statement
    public void close() {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
                System.out.println("Statement closed.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close statement.");
            e.printStackTrace();
        }

        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection.");
            e.printStackTrace();
        }
    }
}
