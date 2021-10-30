package pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto;


import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.MultipleChoiceAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerItem;

public class MultipleChoiceStatementAnswerDetailsDto extends StatementAnswerDetailsDto {
    private Integer optionId;

    public MultipleChoiceStatementAnswerDetailsDto() {
    }

    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }

    @Override
    public boolean emptyAnswer() {
        return optionId == null;
    }


    @Override
    public QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto) {
        return new MultipleChoiceAnswerItem(username, quizId, statementAnswerDto, this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementAnswerDto{" +
                "optionId=" + optionId +
                '}';
    }

    public String toJson() {
        return "{" +
                "optionId=" + optionId +
                '}';
    }
}
