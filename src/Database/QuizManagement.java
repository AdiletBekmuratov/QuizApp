package Database;

import Model.Question;
import Model.Quiz;
import Model.User;

import java.sql.*;

public class QuizManagement extends DatabaseManagement{

    public static void insertQuiz(Quiz quiz, User user) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "INSERT INTO quiz (quiz_topic, time_to_question, creator_id) VALUES (?, ?, ?)";
            //Statement.RETURN_GENERATED_KEYS is necessary to get id of inserted data
            PreparedStatement statement = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, quiz.getQuiz_text());
            statement.setInt(2, quiz.getTime_to_question());
            statement.setInt(3, user.getUser_id());

            int rows = statement.executeUpdate();

            if (rows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        //get inserted quiz's id
                        quiz.setQuiz_id(generatedKeys.getInt(1));
                        System.out.println(quiz.getQuiz_id());
                    }
                    else {
                        throw new SQLException("Creating quiz failed, no ID obtained.");
                    }
                }
                //inserting all questions of quiz object
                for (int i = 0; i < quiz.getQuestions().size(); i++) {
                    String sql_inner = "INSERT INTO questions (ques_text, answer, quiz_id) VALUES (?, ?, ?)";
                    PreparedStatement statement_inner = c.prepareStatement(sql_inner, Statement.RETURN_GENERATED_KEYS);
                    statement_inner.setString(1, quiz.getQuestions().get(i).getQuestion());
                    statement_inner.setInt(2, quiz.getQuestions().get(i).getAnswer());
                    statement_inner.setInt(3, quiz.getQuiz_id());
                    int rows_inner = statement_inner.executeUpdate();

                    if (rows_inner > 0) {
                        try (ResultSet generatedKeys_inner = statement_inner.getGeneratedKeys()) {
                            if (generatedKeys_inner.next()) {
                                quiz.getQuestions().get(i).setQues_id(generatedKeys_inner.getInt(1));
                                System.out.println(quiz.getQuestions().get(i).getQues_id());
                            }
                            else {
                                throw new SQLException("Creating quiz failed, no ID obtained.");
                            }
                            //inserting all options of question object
                            for (int j = 0; j < quiz.getQuestions().get(i).getAlternatives().size(); j++) {
                                String sql_inner2 = "INSERT INTO answers (ans_text, ques_id) VALUES (?, ?)";
                                PreparedStatement statement_inner2 = c.prepareStatement(sql_inner2, Statement.RETURN_GENERATED_KEYS);
                                statement_inner2.setString(1, quiz.getQuestions().get(i).getAlternatives().get(j));
                                statement_inner2.setInt(2, quiz.getQuestions().get(i).getQues_id());

                                int rows_inner2 = statement_inner2.executeUpdate();
                            }
                        }
                    }
                }
                System.out.println("Your quiz inserted");
            }
        }
    }

    //get all quiz available in database
    public static void showAllQuiz() throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT quiz.*, users.username FROM quiz INNER join users ON users.user_id = quiz.creator_id";
            Statement statement = c.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No Employees in Database!");
            } else {
                while (resultSet.next()) {
                    int quiz_id = resultSet.getInt("quiz_id");
                    String quiz_title = resultSet.getString("quiz_topic");
                    String creator_name = resultSet.getString("username");
                    int time_dur = resultSet.getInt("time_to_question");
                    System.out.println(quiz_id + ") " + quiz_title + "; Time limit to each question " + time_dur + " seconds" +"\nCreator: " + creator_name);
                }
            }
        }
    }

    //get quiz object with its all questions and options
    public static Quiz getFullQuiz(int quiz_id) throws SQLException {
        Connection c = connect();
        if (c != null) {
            String sql = "SELECT * FROM quiz WHERE quiz_id=?";
            PreparedStatement pStatement = c.prepareStatement(sql);
            pStatement.setInt(1, quiz_id);
            ResultSet resultSet = pStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                return null;
            } else {
                while (resultSet.next()) {
                    Quiz quiz = new Quiz();
                    quiz.setQuiz_id(resultSet.getInt("quiz_id"));
                    quiz.setQuiz_text(resultSet.getString("quiz_topic"));
                    quiz.setTime_to_question(resultSet.getInt("time_to_question"));
                    quiz.setCreator_id(resultSet.getInt("creator_id"));

                    String sql_inner = "SELECT * FROM questions WHERE quiz_id=?";
                    PreparedStatement pStatement_inner = c.prepareStatement(sql_inner, Statement.RETURN_GENERATED_KEYS);
                    pStatement_inner.setInt(1, quiz.getQuiz_id());
                    ResultSet resultSet_inner = pStatement_inner.executeQuery();

                    if (!resultSet_inner.isBeforeFirst()) {
                        System.out.println("No questions for this quiz");
                        return null;
                    }
                    else {
                        while (resultSet_inner.next()){
                            Question question = new Question();
                            question.setQuestion(resultSet_inner.getString("ques_text"));
                            question.setQues_id(resultSet_inner.getInt("ques_id"));
                            question.setAnswer(resultSet_inner.getInt("answer"));

                            String sql_inner2 = "SELECT * FROM answers WHERE ques_id=?";
                            PreparedStatement pStatement_inner2 = c.prepareStatement(sql_inner2, Statement.RETURN_GENERATED_KEYS);
                            pStatement_inner2.setInt(1, question.getQues_id());
                            ResultSet resultSet_inner2 = pStatement_inner2.executeQuery();

                            if (!resultSet_inner2.isBeforeFirst()) {
                                System.out.println("No questions for this quiz");
                                return null;
                            }
                            else {
                                while (resultSet_inner2.next()){
                                    question.addAlternative(resultSet_inner2.getString("ans_text"));
                                }
                            }

                            quiz.addQuestion(question);
                        }
                    }
                    return quiz;
                }
            }
        }
        return null;
    }
}
