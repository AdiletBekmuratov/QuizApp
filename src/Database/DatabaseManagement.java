package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManagement {

    //method to get connection with MySQL database
    public static Connection connect() {
        String url = "jdbc:mysql://localhost:3306/quiz";
        String user = "root";
        String pass = "";

        try {
            //get connection
            Connection connection = DriverManager.getConnection(url, user, pass);
            //if there is no connection, print an appropriate message
            if (connection == null) {
                System.out.println("No Connection!!!");
            }
            return connection;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}