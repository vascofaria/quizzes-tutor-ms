package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception;

public class QuizNotFoundException extends Exception{
    private Integer quizId;

    public QuizNotFoundException(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }
}
