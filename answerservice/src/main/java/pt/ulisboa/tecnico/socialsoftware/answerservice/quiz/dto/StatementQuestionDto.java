package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto;


import java.io.Serializable;

public class StatementQuestionDto implements Serializable {
    private String content;
    private ImageDto image;
    private Integer sequence;
    private Integer questionId;
    private Integer quizQuestionId;
    private Integer questionAnswerId;

    private StatementQuestionDetailsDto questionDetails;

    public StatementQuestionDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public StatementQuestionDetailsDto getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(StatementQuestionDetailsDto questionDetails) {
        this.questionDetails = questionDetails;
    }

    @Override
    public String toString() {
        return "StatementQuestionDto{" +
                "content='" + content + '\'' +
                ", image=" + image +
                ", sequence=" + sequence +
                ", questionDetails=" + questionDetails +
                ", quizQuestionId=" + quizQuestionId +
                '}';
    }

    public String toJson() {
        return "{" +
                "content='" + content + '\'' +
                ", image=" + image +
                ", sequence=" + sequence +
                ", questionDetails=" + questionDetails.toJson() +
                '}';
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getQuizQuestionId() {
        return quizQuestionId;
    }

    public void setQuizQuestionId(Integer quizQuestionId) {
        this.quizQuestionId = quizQuestionId;
    }

    public Integer getQuestionAnswerId() {
        return questionAnswerId;
    }

    public void setQuestionAnswerId(Integer questionAnswerId) {
        this.questionAnswerId = questionAnswerId;
    }
}