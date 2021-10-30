package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.event.QuizCorrectionEvent;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class QuizServiceApplicational {
    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuizRepository quizRepository;

    private static final String ANSWER_SERVICE_BASE_URL1 ="http://localhost:8083";
    private static final String ANSWER_SERVICE_BASE_URL2 ="http://localhost:8084";
    private static final String ANSWER_SERVICE_BASE_URL3 ="http://localhost:8085";
    /*private static final String ANSWER_SERVICE_BASE_URL1 ="http://answer-service1:8083";
    private static final String ANSWER_SERVICE_BASE_URL2 ="http://answer-service2:8083";
    private static final String ANSWER_SERVICE_BASE_URL3 ="http://answer-service3:8083";*/

    private static final Integer SERVICES_NUMBER = 3;
    // TO USE WITH DOCKER (3 SERVICES)

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto populateWithQuizAnswers(Integer quizId) {
        QuizDto quizDto = quizService.populateWithQuizAnswers(quizId);
        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        populateMicroServices(quiz);

        LocalDateTime oneMinuteAfterConclusion = quiz.getConclusionDate().plusSeconds(15);
        Date oneMinuteAfterConclusionAsDate = Date.from(oneMinuteAfterConclusion.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(new QuizCorrectionEvent(quizDto.getId(), answerService), oneMinuteAfterConclusionAsDate);

        return quizDto;
    }

    private void populateMicroServices(Quiz quiz) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<StatementQuizDto> quizzes1 = quiz.getQuizAnswers().stream()
                                            .filter(quizAnswer -> quizAnswer.getUser().getId() % SERVICES_NUMBER == 0 )
                                            .map(StatementQuizDto::new)
                                            .collect(Collectors.toList());
        List<StatementQuizDto> quizzes2 = quiz.getQuizAnswers().stream()
                                            .filter(quizAnswer -> quizAnswer.getUser().getId() % SERVICES_NUMBER == 1 )
                                            .map(StatementQuizDto::new)
                                            .collect(Collectors.toList());
        List<StatementQuizDto> quizzes3 = quiz.getQuizAnswers().stream()
                                            .filter(quizAnswer -> quizAnswer.getUser().getId() % SERVICES_NUMBER == 2 )
                                            .map(StatementQuizDto::new)
                                            .collect(Collectors.toList());
        if (!quizzes1.isEmpty()) {
            HttpEntity<List<StatementQuizDto>> request1 = new HttpEntity<>(quizzes1, headers);
            restTemplate.postForEntity(ANSWER_SERVICE_BASE_URL1 + "/quiz/insert", request1, Void.class);
        }
        if (!quizzes2.isEmpty()) {
            HttpEntity<List<StatementQuizDto>> request2 = new HttpEntity<>(quizzes2, headers);
            restTemplate.postForEntity(ANSWER_SERVICE_BASE_URL2 + "/quiz/insert", request2, Void.class);
        }

        if (!quizzes3.isEmpty()){
            HttpEntity<List<StatementQuizDto>> request3 = new HttpEntity<>(quizzes3, headers);
            restTemplate.postForEntity(ANSWER_SERVICE_BASE_URL3 + "/quiz/insert", request3, Void.class);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizDto removeNonFilledQuizAnswers(Integer quizId) {
        QuizDto quizDto = quizService.removeNonFilledQuizAnswers(quizId);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.delete(ANSWER_SERVICE_BASE_URL1 + "/quiz/" + quizId + "/delete");
        restTemplate.delete(ANSWER_SERVICE_BASE_URL2 + "/quiz/" + quizId + "/delete");
        restTemplate.delete(ANSWER_SERVICE_BASE_URL3 + "/quiz/" + quizId + "/delete");
        return quizDto;
    }
}
