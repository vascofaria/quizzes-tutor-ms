package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto;

import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto;

import java.io.Serializable;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class StatementQuizDto implements Serializable {
    private Integer id;
    private Integer quizAnswerId;
    private Integer userId;
    private String title;
    private boolean oneWay;
    private boolean timed;
    private String availableDate;
    private String conclusionDate;
    private Long timeToAvailability;
    private Long timeToSubmission;
    private Integer currentQuestion = 0;
    private List<StatementQuestionDto> questions = new LinkedList<>();
    private List<StatementAnswerDto> answers = new LinkedList<>();

    public StatementQuizDto() {}

    public StatementQuizDto(StatementQuizDto statement) {
        this.id = statement.getId();
        this.quizAnswerId = statement.getQuizAnswerId();
        this.userId = statement.getUserId();
        this.title = statement.getTitle();
        this.oneWay = statement.isOneWay();
        this.timed = statement.isTimed();
        this.availableDate = statement.getAvailableDate();
        this.conclusionDate = statement.getConclusionDate();
        this.timeToAvailability = statement.getTimeToAvailability();
        this.timeToSubmission = statement.getTimeToSubmission();
        this.questions = null;
        this.answers = statement.getAnswers();
        this.currentQuestion = statement.getCurrentQuestion();
    }



    public Integer getId() {
        return id;
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public Long getTimeToAvailability() {
        return timeToAvailability;
    }

    public void setTimeToAvailability(Long timeToAvailability) {
        this.timeToAvailability = timeToAvailability;
    }

    public Long getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Long timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public Integer getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Integer currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public List<StatementQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<StatementQuestionDto> questions) {
        this.questions = questions;
    }

    public List<StatementAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StatementAnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "StatementQuizDto{" +
                "id=" + id +
                ", quizAnswerId=" + quizAnswerId +
                ", title='" + title + '\'' +
                ", oneWay=" + oneWay +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", timeToAvailability=" + timeToAvailability +
                ", timeToSubmission=" + timeToSubmission +
                ", questions=" + questions +
                ", answers=" + answers +
                '}';
    }

    public String toJson() {
        return "{" +
                "id=" + id +
                ", quizAnswerId=" + quizAnswerId +
                ", title='" + title + '\'' +
                ", oneWay=" + oneWay +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", timeToAvailability=" + timeToAvailability +
                ", timeToSubmission=" + timeToSubmission +
                ", questions=" + questions.stream().map(StatementQuestionDto::toJson).collect(Collectors.toList()) +
                ", answers=" + answers.stream().map(StatementAnswerDto::toJson).collect(Collectors.toList()) +
                '}';
    }
}