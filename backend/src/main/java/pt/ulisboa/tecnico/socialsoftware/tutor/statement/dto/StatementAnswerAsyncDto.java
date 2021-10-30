package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

public class StatementAnswerAsyncDto {
    private Integer quiz_id;
    private StatementAnswerDto statementAnswerDto;
    public StatementAnswerAsyncDto() {}

    public StatementAnswerAsyncDto(Integer quiz_id, StatementAnswerDto statementAnswerDto) {
        this.quiz_id = quiz_id;
        this.statementAnswerDto = statementAnswerDto;
    }

    public Integer getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(Integer quiz_id) {
        this.quiz_id = quiz_id;
    }

    public StatementAnswerDto getStatementAnswerDto() {
        return statementAnswerDto;
    }

    public void setStatementAnswerDto(StatementAnswerDto statementAnswerDto) {
        this.statementAnswerDto = statementAnswerDto;
    }

}
