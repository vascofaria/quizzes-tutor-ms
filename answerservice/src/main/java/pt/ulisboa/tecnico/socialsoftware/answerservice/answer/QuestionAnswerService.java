package pt.ulisboa.tecnico.socialsoftware.answerservice.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.MultipleChoiceStatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.config.DateHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionAnswerService {

    @Autowired
    private QuestionAnswerItemRepository questionAnswerItemRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void submitAnswer(int quizId, StatementAnswerDto answer) {
        if (answer.getTimeToSubmission() == null) {
            answer.setTimeToSubmission(0);
        }


        if (answer.emptyAnswer()) {
            questionAnswerItemRepository.insertQuestionAnswerItemOptionIdNull(answer.getUsername(), quizId, answer.getQuizQuestionId(), DateHandler.now(),
                    answer.getTimeTaken(), answer.getTimeToSubmission());
        } else {
            questionAnswerItemRepository.save(answer.getQuestionAnswerItem(answer.getUsername(), quizId));
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 2000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<StatementAnswerDto> getQuestionsAnswers(Integer quizId) {
        return questionAnswerItemRepository.findAll().stream()
                .filter(qai -> qai.getQuizId() == quizId)
                .map(qai -> {
                    MultipleChoiceStatementAnswerDetailsDto answerDetailsDto = new MultipleChoiceStatementAnswerDetailsDto();
                    answerDetailsDto.setOptionId(((MultipleChoiceAnswerItem)qai).getOptionId());
                    StatementAnswerDto ans1 = new StatementAnswerDto(qai);
                    ans1.setAnswerDetails(answerDetailsDto);
                    return ans1;
                })
                .collect(Collectors.toList());
    }
}
