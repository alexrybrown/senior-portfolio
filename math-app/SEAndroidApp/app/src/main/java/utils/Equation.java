package utils;

public class Equation {
    private String equation;
    private String answer;

    public Equation(String equation, String answer) {
        this.equation = equation;
        this.answer = answer;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
