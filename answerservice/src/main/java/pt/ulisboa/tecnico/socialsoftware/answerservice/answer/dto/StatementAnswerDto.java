package pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.answerservice.config.DateHandler;

import java.io.Serializable;

public class StatementAnswerDto implements Serializable {
    private Integer timeTaken;
    private Integer sequence;
    private Integer questionAnswerId;
    private String username;
    //private DiscussionDto userDiscussion;
    private Integer quizQuestionId;
    private Integer timeToSubmission;
    private String answerDate;

    private StatementAnswerDetailsDto answerDetails;


    public StatementAnswerDto() {

    }

    public StatementAnswerDto(QuestionAnswerItem questionAnswerItem) {
        setTimeTaken(questionAnswerItem.getTimeTaken());
        setQuestionAnswerId(questionAnswerItem.getQuestionAnswerId());
        setUsername(questionAnswerItem.getUsername());
        setQuizQuestionId(questionAnswerItem.getQuizQuestionId());
        setAnswerDate(DateHandler.toISOString(questionAnswerItem.getAnswerDate()));
    }


    public Integer getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(Integer timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public Integer getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Integer timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public StatementAnswerDetailsDto getAnswerDetails() {
        return answerDetails;
    }

    public void setAnswerDetails(StatementAnswerDetailsDto answerDetails) {
        this.answerDetails = answerDetails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        this.username = user;
    }


    public boolean emptyAnswer() {
        return this.answerDetails.emptyAnswer();
    }

    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId) {
        return this.answerDetails.getQuestionAnswerItem(username, quizId, this);
    }

    public String getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(String answerDate) {
        this.answerDate = answerDate;
    }

    @Override
    public String toString() {
        return "StatementAnswerDto{" +
                "timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                ", questionAnswerId=" + questionAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", timeToSubmission=" + timeToSubmission +
                ", answerDetails=" + answerDetails +
                '}';
    }

    public String toJson() {
        return "{" +
                "timeTaken=" + timeTaken +
                ", sequence=" + sequence +
                ", questionAnswerId=" + questionAnswerId +
                ", quizQuestionId=" + quizQuestionId +
                ", timeToSubmission=" + timeToSubmission +
                ", answerDetails=" + answerDetails.toJson() +
                '}';
    }

}