package Database;

import Model.Quiz;
import Model.User;

import java.sql.*;

import static Database.QuizManagement.getFullQuiz;
import static Database.UserManagement.getUserById;

public class ScoreManagement extends DatabaseManagement{

    //insert user's score
    public static void insertScore(Quiz quiz, User user, int correct) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "INSERT INTO high_scores (score, user_id, quiz_id) VALUES (?, ?, ?)";
            PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, correct);
            statement.setInt(2, user.getUser_id());
            statement.setInt(3, quiz.getQuiz_id());

            int rows = statement.executeUpdate();

            if (rows <= 0) {
                System.out.println("Error while inserting score");
            }
        }
    }

    //get only current user's high scores for passed quizzes
    public static void getMyHighScores(User user) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT *, MAX(score) FROM high_scores WHERE user_id=? GROUP BY quiz_id";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setInt(1, user.getUser_id());
            ResultSet resultSet = pStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                System.out.println("You didn't pass any quiz");
            }
            else {
                while (resultSet.next()){
                    int quiz_id = resultSet.getInt("quiz_id");
                    Quiz quiz = getFullQuiz(quiz_id);
                    int score = resultSet.getInt("MAX(score)");
                    System.out.println(quiz_id + ") " + quiz.getQuiz_text() + "; Your high score is " + score);
                }
            }
        }

    }

    //get only the highest scores for all quizzes
    public static void getHighScores() throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT a.* FROM high_scores a INNER JOIN (select * from high_scores group by hscore_id ORDER BY score DESC) b ON a.hscore_id = b.hscore_id GROUP BY quiz_id";
            Statement statement = c.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No data available");
            } else {
                while (resultSet.next()) {
                    int quiz_id = resultSet.getInt("quiz_id");
                    Quiz quiz = getFullQuiz(quiz_id);
                    int user_id = resultSet.getInt(("user_id"));
                    int score = resultSet.getInt("score");

                    User user = getUserById(user_id);

                    System.out.println(quiz_id + ") " + quiz.getQuiz_text() + "; High score is " + score + " by " + user.getUsername());
                }
            }
        }
    }
}
