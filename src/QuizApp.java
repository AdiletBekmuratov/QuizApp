import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import Database.DatabaseManagement;
import Database.QuizManagement;
import Database.ScoreManagement;
import Database.UserManagement;
import Model.Question;
import Model.Quiz;
import Model.User;
import Model.Validator;

import javax.xml.crypto.Data;

public class QuizApp {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        User user = new User();
        System.out.println("Welcome to quiz app!");
        while (true) {
            System.out.println("1 - Register account\n2 - Login\n3 - Exit");
            int logOpt = scanner.nextInt();
            if (logOpt == 1) {
                System.out.println("Enter Username:");
                user.setUsername(scanner.next().toLowerCase());

                if (UserManagement.getUserByName(user.getUsername())) {
                    boolean isValid = false;
                    while (!isValid) {
                        System.out.println("Enter Password (it should contain al least 1 uppercase, 1 number, 1 special symbol and be minimum 8 characters):");
                        user.setPassword(scanner.next());
                        isValid = Validator.checkPassword(user.getPassword());
                    }

                    System.out.println("Enter Age:");
                    user.setAge(scanner.nextInt());

                    if (UserManagement.registerUser(user)) {
                        mainMenu(user);
                    } else {
                        System.out.println("Some Error while register");
                    }
                } else {
                    System.out.println("Such user already exist");
                }
            } else if (logOpt == 2) {
                System.out.println("Enter Username:");
                String username = scanner.next().toLowerCase();

                System.out.println("Enter Password:");
                String password = scanner.next();

                if (UserManagement.loginUser(username, password)) {
                    mainMenu(UserManagement.getUser(username, password));
                } else {
                    System.out.println("Wrong Email or Password");
                }
            } else if (logOpt == 3) {
                System.out.println("Good luck! See you soon");
                break;
            }
        }
    }

    private static void mainMenu(User user) throws SQLException {
        while (true) {
            System.out.println();
            System.out.println("MENU");
            System.out.println("1 - Start Quiz\n2 - Create Quiz\n3 - My high scores\n4 - High scores\n5 - Exit");
            int menuOpt = scanner.nextInt();

            if (menuOpt == 1) {
                QuizManagement.showAllQuiz();
                System.out.println("Select quiz (by index):");
                int quiz_id = scanner.nextInt();
                Quiz quiz = QuizManagement.getFullQuiz(quiz_id);
                if (quiz != null) {
                    startQuiz(quiz, user);
                } else {
                    System.out.println("Quiz is empty");
                }

            } else if (menuOpt == 2) {
                System.out.println("1 - Create manually\n2 - Import from .txt");
                int quizCreateOpt = scanner.nextInt();
                if (quizCreateOpt == 1) {
                    manuallyCreateQuiz(user);
                } else if (quizCreateOpt == 2) {
                    Quiz quiz = new Quiz();

                    System.out.println("Enter quiz title:");
                    scanner.nextLine();
                    quiz.setQuiz_text(scanner.nextLine());

                    System.out.println(".txt file should be formatted in this way:\nWhat is H20? <- Question should include '?' or ':'\n" +
                            "4 <- number of options\n" +
                            "1) Cocaine <- Options should include numerical order with ')'\n" +
                            "2) Milk\n" +
                            "3) Water\n" +
                            "4) Coffee\n" +
                            "3 <- index of right answer");
                    System.out.println("Please enter full path to file. Example: C:\\Users\\username\\Desktop\\quiz.txt");

                    while (quiz.getQuestions().size() == 0) {
                        String file_name = scanner.next();
                        quiz.setQuestionsFromFile(file_name);
                    }

                    System.out.println("Enter time limit to answer question in seconds:");
                    quiz.setTime_to_question(scanner.nextInt());

                    quizConfirmation(user, quiz);
                } else {
                    System.out.println("Wrong Input");
                }
            } else if (menuOpt == 3) {
                ScoreManagement.getMyHighScores(user);
            } else if (menuOpt == 4) {
                ScoreManagement.getHighScores();
            } else if (menuOpt == 5) {
                System.out.println("Good luck! See you soon");
                break;
            }
        }
    }

    private static void startQuiz(Quiz quiz, User user) throws SQLException {
        int correct = 0;
        System.out.println("Start of quiz: " + quiz.getQuiz_text());
        for (Question question : quiz.getQuestions()) {
            System.out.println(question.getQuestion());
            for (String option : question.getAlternatives()) {
                System.out.println(option);
            }
            int user_answer = 0;
            int finalUser_answer = user_answer;
            TimerTask task = new TimerTask() {
                public void run() {
                    if (finalUser_answer <= 0) {
                        System.out.println("Hurry up! Enter -1 to get next question");
                    }

                }
            };
            Timer timer = new Timer();
            timer.schedule( task, quiz.getTime_to_question() * 1000 );

            System.out.println( "Input within " + quiz.getTime_to_question() + " seconds: " );
            user_answer = scanner.nextInt();
            timer.cancel();
            if (user_answer == question.getAnswer()){
                correct++;
            }
        }
        System.out.println("You get " + correct + "/" + quiz.getQuestions().size());
        ScoreManagement.insertScore(quiz, user, correct);
    }

    private static void manuallyCreateQuiz(User user) throws SQLException {
        Quiz quiz = new Quiz();

        System.out.println("Enter quiz title:");
        scanner.nextLine();
        quiz.setQuiz_text(scanner.nextLine());

        System.out.println("Enter number of questions");
        int number_of_questions = scanner.nextInt();

        for (int i = 0; i < number_of_questions; i++) {
            Question question = new Question();
            System.out.println("Enter question text: ");
            scanner.nextLine();
            question.setQuestion(scanner.nextLine());
            System.out.println("Enter number of options:");
            String[] options = new String[scanner.nextInt()];
            scanner.nextLine();
            for (int j = 0; j < options.length; j++) {
                System.out.println("Enter option " + (j + 1) + " (indexes will be added automatically):");
                options[j] = (j + 1) + ") " + scanner.nextLine();
                question.addAlternative(options[j]);
            }

            System.out.println("Enter right answer's index:");
            question.setAnswer(scanner.nextInt());

            quiz.addQuestion(question);
        }

        System.out.println("Enter time limit to answer question in seconds:");
        quiz.setTime_to_question(scanner.nextInt());

        quizConfirmation(user, quiz);
    }

    private static void quizConfirmation(User user, Quiz quiz) throws SQLException {
        System.out.println("\nConfirmation!!!");
        for (Question q : quiz.getQuestions()) {
            System.out.println(q);
        }

        System.out.println("1 - Confirm\n2 - No");
        int confOpt = scanner.nextInt();
        if (confOpt == 1) {
            QuizManagement.insertQuiz(quiz, user);
        } else if (confOpt == 2) {
            System.out.println("Operation cancelled");
        }
    }
}