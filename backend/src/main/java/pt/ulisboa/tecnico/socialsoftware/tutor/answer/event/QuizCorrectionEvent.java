package pt.ulisboa.tecnico.socialsoftware.tutor.answer.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;

import java.util.TimerTask;
public class QuizCorrectionEvent extends TimerTask {
    private AnswerService answerService;

    private Integer quizId;

    public QuizCorrectionEvent(Integer quizId, AnswerService answerService) {
        this.quizId = quizId;
        this.answerService = answerService;
    }

    @Override
    public void run() {
        this.answerService.writeQuestionAnswerFromQuestionAnswerItem(quizId);
    }
}
