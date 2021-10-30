package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementQuestionDetailsDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementQuestionDetailsDto.class, name = "multiple_choice"),
})
public abstract class StatementQuestionDetailsDto {
    public abstract String toJson();
}
