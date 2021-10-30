package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception;

public class UserNotFoundException extends Exception{
    private Integer userId;
    
    public UserNotFoundException(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getQuizId() {
        return userId;
    }

}
