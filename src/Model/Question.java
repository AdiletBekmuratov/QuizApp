package Model;

import java.util.ArrayList;

public class Question {
    private int ques_id;
    private String question;
    private ArrayList<String> alternatives = new ArrayList<>();
    private int answer;

    public Question() {

    }

    public Question(String question, ArrayList<String> alternatives, int answer) {
        this.question = question;
        this.alternatives = alternatives;
        this.answer = answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public ArrayList<String> getAlternatives() {
        return this.alternatives;
    }

    public int getAnswer() {
        return this.answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAlternatives(ArrayList<String> alternatives) {
        this.alternatives = alternatives;
    }

    public void addAlternative(String alternative) {
        this.alternatives.add(alternative);
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public int getQues_id() {
        return ques_id;
    }

    public void setQues_id(int ques_id) {
        this.ques_id = ques_id;
    }

    @Override
    public String toString() {
        String print = this.question + "\n";
        for (String alternative : this.alternatives) {
            print += alternative + "\n";
        }
        print += "Answer: " + this.answer + "\n";
        return print;
    }
}
