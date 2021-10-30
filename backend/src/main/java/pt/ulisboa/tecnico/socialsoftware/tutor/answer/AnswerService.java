package pt.ulisboa.tecnico.socialsoftware.tutor.answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.AnswerDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.AnswerDetailsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlExportVisitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.QuizAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.domain.QuizAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class AnswerService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private AnswerDetailsRepository answerDetailsRepository;

    @Autowired
    private QuizAnswerItemRepository quizAnswerItemRepository;

    @Autowired
    private AnswersXmlImport xmlImporter;

    @Autowired
    private EntityManager entityManager;

    private static final String ANSWER_SERVICE_BASE_URL1 ="http://localhost:8083";
    private static final String ANSWER_SERVICE_BASE_URL2 ="http://localhost:8084";
    private static final String ANSWER_SERVICE_BASE_URL3 ="http://localhost:8085";

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public QuizAnswerDto createQuizAnswer(Integer userId, Integer quizId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Quiz quiz = quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);

        return new QuizAnswerDto(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<CorrectAnswerDto> concludeQuiz(StatementQuizDto statementQuizDto) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(statementQuizDto.getQuizAnswerId())
                .orElseThrow(() -> new TutorException(QUIZ_ANSWER_NOT_FOUND, statementQuizDto.getId()));

        if (quizAnswer.getQuiz().getAvailableDate() != null && quizAnswer.getQuiz().getAvailableDate().isAfter(DateHandler.now())) {
            throw new TutorException(QUIZ_NOT_YET_AVAILABLE);
        }

        if (quizAnswer.getQuiz().getConclusionDate() != null && quizAnswer.getQuiz().getConclusionDate().isBefore(DateHandler.now().minusMinutes(10))) {
            throw new TutorException(QUIZ_NO_LONGER_AVAILABLE);
        }

        if (!quizAnswer.isCompleted()) {
            quizAnswer.setCompleted(true);

            quizAnswer.setAnswerDate(DateHandler.now());

        }
        return new ArrayList<>();
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuizAnswers(Integer quizId) {
        Quiz quiz = quizRepository.findQuizWithAnswersAndQuestionsById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));
        Map<Integer, QuizAnswer> quizAnswersMap = quiz.getQuizAnswers().stream().collect(Collectors.toMap(QuizAnswer::getId, Function.identity()));

        List<QuizAnswerItem> quizAnswerItems = quizAnswerItemRepository.findQuizAnswerItemsByQuizId(quizId);

        quizAnswerItems.forEach(quizAnswerItem -> {
            QuizAnswer quizAnswer = quizAnswersMap.get(quizAnswerItem.getQuizAnswerId());

            if (quizAnswer.getAnswerDate() == null) {
                quizAnswer.setAnswerDate(quizAnswerItem.getAnswerDate());

                for (QuestionAnswer questionAnswer : quizAnswer.getQuestionAnswers()) {
                    writeQuestionAnswer(questionAnswer, quizAnswerItem.getAnswersList());
                }
            }
            quizAnswerItemRepository.deleteById(quizAnswerItem.getId());
        });
    }

    private void writeQuestionAnswer(QuestionAnswer questionAnswer, List<StatementAnswerDto> statementAnswerDtoList) {
        StatementAnswerDto statementAnswerDto = statementAnswerDtoList.stream()
                .filter(statementAnswerDto1 -> statementAnswerDto1.getQuestionAnswerId().equals(questionAnswer.getId()))
                .findAny()
                .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, questionAnswer.getId()));

        questionAnswer.setTimeTaken(statementAnswerDto.getTimeTaken());
        AnswerDetails answer = questionAnswer.setAnswerDetails(statementAnswerDto);
        if (answer != null) {
            answerDetailsRepository.save(answer);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public String exportAnswers() {
        AnswersXmlExportVisitor xmlExport = new AnswersXmlExportVisitor();

        return xmlExport.export(quizAnswerRepository.findAll());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void importAnswers(String answersXml) {
        xmlImporter.importAnswers(answersXml);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteQuizAnswer(QuizAnswer quizAnswer) {
        quizAnswer.remove();
        quizAnswerRepository.delete(quizAnswer);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void writeQuestionAnswerFromQuestionAnswerItem(Integer quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND, quizId));

        if (quiz.isCorrected()) {
            return;
        }

        for (QuizAnswer qa : quiz.getQuizAnswers()) {
            qa.setCompleted(true);
            if (qa.getAnswerDate() == null) {
                qa.setAnswerDate(quiz.getConclusionDate());
            }
        }

        List<StatementAnswerDto> answers = getStatementAnswerDtos(quizId);
        List<StatementAnswerDto> lastAnswers = getStatementAnswerDtos(answers);
        for (StatementAnswerDto answerDto : lastAnswers) {
            quizRepository.findById(quizId).orElseThrow(() -> new TutorException(QUIZ_NOT_FOUND));
            QuestionAnswer questionAnswer = questionAnswerRepository.findById(answerDto.getQuestionAnswerId())
                    .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND));
            questionAnswer.setTimeTaken(answerDto.getTimeTaken());

            deleteDetails(questionAnswer.getId());
            AnswerDetails answer = questionAnswer.setAnswerDetails(answerDto);
            if (answer != null) {
                answerDetailsRepository.save(answer);
            }
        }
        quiz.setCorrected(true);
    }

    private List<StatementAnswerDto> getStatementAnswerDtos(List<StatementAnswerDto> answers) {
        List<Integer> quizQuestionIds = answers.stream()
                .map(StatementAnswerDto::getQuizQuestionId)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<Integer, List<StatementAnswerDto>> quizAnsIds = new HashMap<>();
        for(Integer id : quizQuestionIds) {
            List<StatementAnswerDto> statementAnswers = answers.stream()
                    .filter(statementAnswerDto -> statementAnswerDto.getQuizQuestionId().equals(id))
                    .distinct()
                    .collect(Collectors.toList());

            quizAnsIds.put(id, statementAnswers);
        }

        // filter by quiz question id and question answer id
        List<StatementAnswerDto> lastAnswers = new ArrayList<>();
        for (Integer answerId : quizAnsIds.keySet()) {
            lastAnswers.add(quizAnsIds.get(answerId).stream()
                    .max(Comparator.comparing(StatementAnswerDto::getAnswerLocalDateTime))
                    .orElseThrow(() -> new TutorException(QUESTION_ANSWER_NOT_FOUND, answerId)));
        }
        return lastAnswers.stream().filter(a -> !a.emptyAnswer()).collect(Collectors.toList());
    }

    private List<StatementAnswerDto> getStatementAnswerDtos(Integer quizId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<StatementAnswerDto[]> response1 =
                restTemplate.getForEntity(ANSWER_SERVICE_BASE_URL1 + "/quiz/"+ quizId +"/answers", StatementAnswerDto[].class);
        List<StatementAnswerDto> answers1 = Arrays.asList(response1.getBody());

        ResponseEntity<StatementAnswerDto[]> response2 = restTemplate.getForEntity(ANSWER_SERVICE_BASE_URL2 + "/quiz/"+ quizId +"/answers", StatementAnswerDto[].class);
        List<StatementAnswerDto> answers2 = Arrays.asList(response2.getBody());

        ResponseEntity<StatementAnswerDto[]> response3 = restTemplate.getForEntity(ANSWER_SERVICE_BASE_URL3 + "/quiz/"+ quizId +"/answers", StatementAnswerDto[].class);
        List<StatementAnswerDto> answers3 = Arrays.asList(response3.getBody());


        List<StatementAnswerDto> answers = new ArrayList<>();
        answers.addAll(answers1);
        answers.addAll(answers2);
        answers.addAll(answers3);
        return answers;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteDetails(Integer questionAnswerId) {
        if (answerDetailsRepository.findByQuestionAnswerId(questionAnswerId).isPresent()) {
            AnswerDetails a = answerDetailsRepository.findByQuestionAnswerId(questionAnswerId).get();
            Integer id = a.getId();
            a.remove();
            answerDetailsRepository.deleteById(id);
            entityManager.flush();
        }
    }
}
