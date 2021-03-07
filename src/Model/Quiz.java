package Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Quiz {
    private int quiz_id, creator_id;
    private ArrayList<Question> questions = new ArrayList<>();
    private String quiz_text;
    private int time_to_question;

    public Quiz() {
    }

    public ArrayList<Question> getQuestions() {
        return this.questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public String getQuiz_text() {
        return quiz_text;
    }

    public void setQuiz_text(String quiz_text) {
        this.quiz_text = quiz_text;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    public int getTime_to_question() {
        return time_to_question;
    }

    public void setTime_to_question(int time_to_question) {
        this.time_to_question = time_to_question;
    }

    public int getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(int quiz_id) {
        this.quiz_id = quiz_id;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public void setQuestionsFromFile(String fileName) {
        try {
            //set up file reader and scanner
            FileReader file = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(file);
            Scanner scanner = new Scanner(reader);

            String line;
            String question = "";
            String[] alternatives = null;

            int answer = 0;

            int numberOfOptions = 0;
            int counter = 0;

            do {
                do {
                    line = scanner.nextLine();

                    if (line.contains("?") || line.contains(":")) { //stores the question
                        question = line;
                    } else if (counter == 0 && line.length() == 1) { //stores the number of options
                        numberOfOptions = Integer.parseInt(line);
                        alternatives = new String[numberOfOptions];
                    } else if (line.contains(")")) { //stores the alternatives
                        if (alternatives != null){
                            alternatives[counter++] = line;
                        }
                        else {
                            System.out.println("Your file should include number of options");
                        }
                    } else if (Character.isDigit(line.indexOf(0)) || counter == numberOfOptions) { //Stores the answer
                        answer = Integer.parseInt(line);
                    }

                } while (answer == 0);
                Question q_obj = new Question();
                q_obj.setQuestion(question);
                q_obj.setAnswer(answer);
                for (String alternative : alternatives) {
                    q_obj.addAlternative(alternative);
                }
                questions.add(q_obj);
                numberOfOptions = 0;
                counter = 0;
                answer = 0;

            } while (scanner.hasNext());


            file.close();
            reader.close();
            scanner.close();
        } catch (IOException e) {
            System.out.println("Incorrect Path! Please, try again");
        }
    }


    @Override
    public String toString() {
        return "{" +
                " questions='" + getQuestions() + "'" +
                "}";
    }

}
