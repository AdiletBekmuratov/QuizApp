package Database;

import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//extending DatabaseManagement class to get connect() method
public class UserManagement extends DatabaseManagement{

    public static boolean registerUser(User user) throws SQLException {
        Connection c = connect();
        if (c != null) {
            //sql statement to get information about user
            String sql = "SELECT * FROM users WHERE username=?";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setString(1, user.getUsername());
            ResultSet resultSet = pStatement.executeQuery();

            //if there is no data, we register user
            if (!resultSet.isBeforeFirst()) {
                String sql_register = "INSERT INTO users (username, upassword, age) VALUES (?, ?, ?)";
                PreparedStatement statement = c.prepareStatement(sql_register);
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setInt(3, user.getAge());

                int rows = statement.executeUpdate();

                if (rows > 0) {
                    System.out.println("Registered Successfully!");
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static boolean loginUser(String username, String password) throws SQLException {
        //if there is user data, it means the user inputs its information correct
        return getUser(username, password) != null;
    }

    //method to get user via username and password
    public static User getUser(String username, String password) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT * FROM users WHERE username=? and upassword=?";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setString(1, username);
            pStatement.setString(2, password);
            ResultSet resultSet = pStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                return null;
            } else {
                while (resultSet.next()) {
                    User user = new User();
                    user.setUser_id(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("upassword"));
                    user.setAge(resultSet.getInt("age"));
                    return user;
                }
            }
        }
        return null;
    }

    //method to get user via id
    public static User getUserById(int user_id) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT * FROM users WHERE user_id=?";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setInt(1, user_id);
            ResultSet resultSet = pStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                return null;
            } else {
                while (resultSet.next()) {
                    User user = new User();
                    user.setUser_id(resultSet.getInt("user_id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("upassword"));
                    user.setAge(resultSet.getInt("age"));
                    return user;
                }
            }
        }
        return null;
    }

    //method to get user via username
    public static boolean getUserByName(String username) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT * FROM users WHERE username=?";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setString(1, username);
            ResultSet resultSet = pStatement.executeQuery();

            return !resultSet.isBeforeFirst();
        }
        return false;
    }
}
