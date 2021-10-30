package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.QuestionAnswerItemRepository;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.NoMoreQuestionsException;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.QuizNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.UserNotFoundException;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class QuizService {
    Map<Integer, Map<Integer, StatementQuizDto>> statementQuizzes = new ConcurrentHashMap<>();

    @Autowired
	private QuestionAnswerItemRepository questionItemRepository;

    public void insertQuiz(List<StatementQuizDto> statementQuizzesDto) {
		if (statementQuizzesDto.size() < 1) {
			return;
		}
		Integer quizId = statementQuizzesDto.get(0).getId();

		if (!statementQuizzes.containsKey(quizId)) {
			statementQuizzes.put(quizId, new ConcurrentHashMap<Integer, StatementQuizDto>());
		}

		Map<Integer, StatementQuizDto> mapQuizDto = statementQuizzes.get(quizId);
		statementQuizzesDto.forEach(sq -> {
			mapQuizDto.put(sq.getUserId(), sq);
		});
    }

    public StatementQuizDto getQuiz(Integer quizId, Integer userId) throws QuizNotFoundException, UserNotFoundException {
    	StatementQuizDto quiz;
        if (statementQuizzes.containsKey(quizId)) {
			Map<Integer, StatementQuizDto> mapQuizDto = statementQuizzes.get(quizId);
			if (mapQuizDto.containsKey(userId)) {
				if (mapQuizDto.get(userId).isOneWay()) {
					quiz = new StatementQuizDto(mapQuizDto.get(userId));
				} else {
					quiz = mapQuizDto.get(userId);
				}
				if (quiz.getCurrentQuestion() > 0) {
					quiz.setCurrentQuestion(quiz.getCurrentQuestion() - 1);
				}
				return quiz;
			} else {
				throw new UserNotFoundException(userId);
			}
        }
        throw new QuizNotFoundException(quizId);
    }

	public void removeQuiz(Integer quizId) throws QuizNotFoundException {
		if (statementQuizzes.containsKey(quizId)) {
			statementQuizzes.remove(quizId);
        } else {
			throw new QuizNotFoundException(quizId);
		}
	}

	public StatementQuestionDto getNextQuestion(Integer quizId, Integer userId) throws QuizNotFoundException, UserNotFoundException, NoMoreQuestionsException {
    	if (!statementQuizzes.containsKey(quizId)) {
    		throw new QuizNotFoundException(quizId);
		}
    	Map<Integer, StatementQuizDto> statementQuizDtoMap = statementQuizzes.get(quizId);
    	if (!statementQuizDtoMap.containsKey(userId)) {
    		throw new UserNotFoundException(userId);
		}
    	StatementQuizDto statementQuizDto = statementQuizDtoMap.get(userId);
    	if (statementQuizDto.getCurrentQuestion() + 1 > statementQuizDto.getQuestions().size()) {
    		throw new NoMoreQuestionsException(quizId);
		}
		statementQuizDto.setCurrentQuestion(statementQuizDto.getCurrentQuestion() + 1);
		return statementQuizDto.getQuestions().get(statementQuizDto.getCurrentQuestion() - 1);
	}


}
