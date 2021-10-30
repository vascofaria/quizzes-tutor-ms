package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.*;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_OPTION_MISMATCH;

@Entity
@DiscriminatorValue(Question.QuestionTypes.MULTIPLE_CHOICE_QUESTION)
public class MultipleChoiceAnswer extends AnswerDetails {

    @ManyToOne
    @JoinColumn(name = "option_id")
    private Option option;

    public MultipleChoiceAnswer() {
        super();
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer){
        super(questionAnswer);
    }

    public MultipleChoiceAnswer(QuestionAnswer questionAnswer, Option option){
        super(questionAnswer);
        this.setOption(option);
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;

        if (option != null)
            option.addQuestionAnswer(this);
    }

    public void setOption(MultipleChoiceQuestion question, MultipleChoiceStatementAnswerDetailsDto multipleChoiceStatementAnswerDetailsDto) {
        if (multipleChoiceStatementAnswerDetailsDto.getOptionId() != null) {
            Option option = question.getOptions().stream()
                    .filter(option1 -> option1.getId().equals(multipleChoiceStatementAnswerDetailsDto.getOptionId()))
                    .findAny()
                    .orElseThrow(() -> new TutorException(QUESTION_OPTION_MISMATCH, question.getId(), multipleChoiceStatementAnswerDetailsDto.getOptionId()));

            if (this.getOption() != null) {
                this.getOption().getQuestionAnswers().remove(this);
            }

            this.setOption(option);
        } else {
            this.setOption(null);
        }
    }

    @Override
    public boolean isCorrect() {
        return getOption() != null && getOption().isCorrect();
    }


    public void remove() {
        super.remove();
        if (option != null) {
            option.getQuestionAnswers().remove(this);
            option = null;
        }
    }

    @Override
    public AnswerDetailsDto getAnswerDetailsDto() {
        return new MultipleChoiceAnswerDto(this);
    }

    @Override
    public boolean isAnswered() {
        return this.getOption() != null;
    }

    @Override
    public StatementAnswerDetailsDto getStatementAnswerDetailsDto() {
        return new MultipleChoiceStatementAnswerDetailsDto(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitAnswerDetails(this);
    }
}
