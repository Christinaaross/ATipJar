package appOutput;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//
public class AppData {

    private static final String URL = "jdbc:mysql://localhost:3306/mytipjar";
    private static final String USER = "root"; 
    private static final String PASSWORD = "Lavender444!"; 

    public AppData() {
        // LOADING THE DRIVER
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver Loaded!");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver could NOT be Loaded");
            e.printStackTrace();
        }
    }

    // NEW CONNECTION
    public static Connection getConnection() { // chnaged to stat
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException ex) {
            System.out.println("Database could NOT connect");
            ex.printStackTrace();
            return null;
        }
    }

    // CLOSE CONNECTION
    public void close(Connection connection) {
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