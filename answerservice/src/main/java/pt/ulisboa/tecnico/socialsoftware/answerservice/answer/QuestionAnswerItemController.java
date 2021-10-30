package pt.ulisboa.tecnico.socialsoftware.answerservice.answer;

import com.fasterxml.jackson.annotation.JsonAlias;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerAsyncDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.answer.dto.StatementAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.answerservice.quiz.QuizService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class QuestionAnswerItemController {


    private static final String serverURI = "tcp://localhost:1883";
    private static final String TOPIC = "SubmitAnswer";

    @Autowired
    private QuestionAnswerService answerService;

    @PostMapping("/quizzes/{quizId}/submit")
    public void submitAnswer(@PathVariable int quizId, @Valid @RequestBody StatementAnswerDto answer) throws MqttException {
        answerService.submitAnswer(quizId, answer);
    }


    @GetMapping("/quiz/{quizId}/answers")
    public List<StatementAnswerDto> getAnswers(@PathVariable Integer quizId) {
        return answerService.getQuestionsAnswers(quizId);
    }

}
