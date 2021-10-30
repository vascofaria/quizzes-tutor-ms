package pt.ulisboa.tecnico.socialsoftware.answerservice.answer;


import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Map;

@Entity
@DiscriminatorValue("multiple_choice") //convert to constant
public class MultipleChoiceAnswerItem extends QuestionAnswerItem {

    private Integer optionId;

    public MultipleChoiceAnswerItem() {
    }

    public MultipleChoiceAnswerItem(String username, int quizId, StatementAnswerDto answer, MultipleChoiceStatementAnswerDetailsDto detailsDto) {
        super(username, quizId, answer);
        this.optionId = detailsDto.getOptionId();
    }


    public Integer getOptionId() {
        return optionId;
    }

    public void setOptionId(Integer optionId) {
        this.optionId = optionId;
    }
}
