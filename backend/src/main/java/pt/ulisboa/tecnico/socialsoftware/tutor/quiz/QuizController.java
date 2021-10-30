package pt.ulisboa.tecnico.socialsoftware.tutor.quiz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuizAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.TopicController;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(TopicController.class);

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizServiceApplicational quizServiceApplicational;

    @Autowired
    private AnswerService answerService;

    @GetMapping("/executions/{executionId}/quizzes/non-generated")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuizDto> findNonGeneratedQuizzes(@PathVariable int executionId) {
        return quizService.findNonGeneratedQuizzes(executionId);
    }

    @PostMapping("/executions/{executionId}/quizzes")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuizDto createQuiz(@PathVariable int executionId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.createQuiz(executionId, quiz);
    }

    @GetMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto getQuiz(@PathVariable Integer quizId) {
        return this.quizService.findById(quizId);
    }

    @PutMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto updateQuiz(@PathVariable Integer quizId, @Valid @RequestBody QuizDto quiz) {
        return this.quizService.updateQuiz(quizId, quiz);
    }

    @PostMapping("/quizzes/{quizId}/populate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto populateWithQuizAnswers(@PathVariable Integer quizId) {
        return this.quizServiceApplicational.populateWithQuizAnswers(quizId);
    }

    @PostMapping("/quizzes/{quizId}/write")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void writeQuizAnswers(@PathVariable Integer quizId) {
        this.answerService.writeQuestionAnswerFromQuestionAnswerItem(quizId);
    }

    @PostMapping("/quizzes/{quizId}/unpopulate")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizDto removeNonFilledQuizAnswers(@PathVariable Integer quizId) {
        return this.quizServiceApplicational.removeNonFilledQuizAnswers(quizId);
    }

    @DeleteMapping("/quizzes/{quizId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void deleteQuiz(@PathVariable Integer quizId) {
        quizService.removeQuiz(quizId);
    }

    @GetMapping(value = "/quizzes/{quizId}/export")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public void exportQuiz(HttpServletResponse response, @PathVariable Integer quizId) throws IOException {
        answerService.writeQuizAnswers(quizId);

        response.setHeader("Content-Disposition", "attachment; filename=file.zip");
        response.setContentType("application/zip");
        response.getOutputStream().write(this.quizService.exportQuiz(quizId).toByteArray());

        response.flushBuffer();
    }

    @GetMapping("/quizzes/{quizId}/answers")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public QuizAnswersDto getQuizAnswers(@PathVariable Integer quizId) {
        answerService.writeQuizAnswers(quizId);
        return this.quizService.getQuizAnswers(quizId);
    }
}