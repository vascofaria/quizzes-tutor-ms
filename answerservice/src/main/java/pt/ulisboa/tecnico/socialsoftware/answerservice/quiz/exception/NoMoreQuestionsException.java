package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception;

public class NoMoreQuestionsException extends Exception{
    private Integer quizId;

    public NoMoreQuestionsException(Integer quizId) {
        this.quizId = quizId;
    }

    public Integer getQuizId() {
        return quizId;
    }
}
