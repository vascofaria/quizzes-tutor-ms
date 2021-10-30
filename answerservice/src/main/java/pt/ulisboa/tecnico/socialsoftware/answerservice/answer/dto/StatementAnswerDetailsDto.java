package pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerItem;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = MultipleChoiceStatementAnswerDetailsDto.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MultipleChoiceStatementAnswerDetailsDto.class, name = "multiple_choice") //convert to constant
})
public abstract class StatementAnswerDetailsDto {

    //public abstract AnswerDetails getAnswerDetails(QuestionAnswer questionAnswer);

    public abstract boolean emptyAnswer();

    public abstract QuestionAnswerItem getQuestionAnswerItem(String username, int quizId, StatementAnswerDto statementAnswerDto);

    public abstract String toJson();
}
