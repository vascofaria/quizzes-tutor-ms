package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto;


import java.util.List;
import java.util.stream.Collectors;

public class MultipleChoiceStatementQuestionDetailsDto extends StatementQuestionDetailsDto {
    private List<StatementOptionDto> options;

    public List<StatementOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<StatementOptionDto> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "MultipleChoiceStatementQuestionDetailsDto{" +
                "options=" + options +
                '}';
    }

    public String toJson() {
        return "{" +
                "options=" + options.stream().map(StatementOptionDto::toJson).collect(Collectors.toList()) +
                '}';
    }
}