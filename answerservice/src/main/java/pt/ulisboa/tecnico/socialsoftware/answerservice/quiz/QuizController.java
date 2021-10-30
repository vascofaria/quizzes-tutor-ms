package pt.ulisboa.tecnico.socialsoftware.answerservice.quiz;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto.StatementQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.NoMoreQuestionsException;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.QuizNotFoundException;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.exception.UserNotFoundException;

import java.util.List;

@RestController
public class QuizController {

    @Autowired
    private QuizService quizService;


    @PostMapping("/quiz/insert")
    public void insertQuiz(@RequestBody List<StatementQuizDto> statementQuizzesDto) {
        quizService.insertQuiz(statementQuizzesDto);
    }

    @GetMapping("/quiz/{quizId}/get/{userId}")
    public StatementQuizDto getQuiz(@PathVariable int quizId, @PathVariable int userId) throws QuizNotFoundException, UserNotFoundException {
        return quizService.getQuiz(quizId, userId);
    }

	@DeleteMapping("/quiz/{quizId}/delete")
    public void removeQuiz(@PathVariable int quizId) throws QuizNotFoundException {
        quizService.removeQuiz(quizId);
    }

    @GetMapping("/quiz/{quizId}/getNextQuestion/{userId}")
    public StatementQuestionDto getNextQuestion(@PathVariable int quizId, @PathVariable int userId) throws QuizNotFoundException, UserNotFoundException, NoMoreQuestionsException {
        return quizService.getNextQuestion(quizId, userId);
    }
}
